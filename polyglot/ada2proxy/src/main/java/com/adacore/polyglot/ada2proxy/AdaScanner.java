package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang.*;
import com.adacore.polyglot.Scanner;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Proxy;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** Scanner for the Ada language. */
public class AdaScanner extends Scanner {

    /** The Ada visitor. */
    private final AdaVisitor visitor = new AdaVisitor();

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(
                    Path.of("jte-classes").resolve("ada2proxy"),
                    ContentType.Plain,
                    AdaScanner.class.getClassLoader(),
                    "gg.jte.generate.precompiled.ada2proxy");

    /** The path to the Gpr project file. */
    private Path projectFile;

    /** The name of the project. */
    private String projectName;

    /** The Ada proxy. */
    private AdaProxy proxy;

    private List<String> getFilesToAnalyze(ProjectManager projectManager, List<String> units)
            throws FileNotFoundException {
        Stream<String> filenames =
                Stream.of(projectManager.getFiles(SourceFileMode.DEFAULT))
                        .filter(s -> s.endsWith(".ads"));
        if (units == null || units.isEmpty()) return filenames.toList();
        // Filter all the units to bind.
        List<String> result =
                filenames
                        .filter(
                                units == null || units.isEmpty()
                                        ? (s -> true)
                                        : (s ->
                                                units.contains(
                                                        Path.of(s).getFileName().toString())))
                        .toList();
        // Verify that all the units in ``units`` have been found.
        List<Path> resultBasenames = result.stream().map(s -> Path.of(s).getFileName()).toList();
        for (var unit : units) {
            units.stream()
                    .filter(u -> resultBasenames.contains(Path.of(u)))
                    .findFirst()
                    .orElseThrow(() -> new FileNotFoundException(unit));
        }
        return result;
    }

    @Override
    public void scanProject(Path projectFile, List<String> units) throws FileNotFoundException {
        this.projectFile = projectFile;
        // Get the name of the project
        this.projectName =
                projectFile
                        .getFileName()
                        .toString()
                        .substring(0, projectFile.getFileName().toString().lastIndexOf(".gpr"));

        // Analyze all the ``.ads`` source files.
        ProjectManager projectManager = ProjectManager.create(projectFile.toString());
        AnalysisContext ctx = projectManager.createContext(null, null, true, 8);
        List<Package> modules =
                getFilesToAnalyze(projectManager, units).stream()
                        .map(s -> ctx.getUnitFromFile(s))
                        .map(u -> visitor.analyzeSpec(u))
                        .toList();
        this.proxy = new AdaProxy(Name.fromLower(projectName), modules, visitor.getArrayTypes());
    }

    @Override
    public Proxy getProxy() {
        return AdaProxyTranslator.translate(proxy);
    }

    @Override
    public void generate(Path path) throws IOException {
        // Write the json proxy file.
        Proxy jsonProxy = getProxy();
        AdaAPI api = new AdaAPI(Name.fromLower(projectName));
        try {
            jsonProxy.writeProxy(path.resolve("proxy.json").toFile());
        } catch (Exception e) {
            throw new IOException(e);
        }

        // Generate the Gpr file for the proxy.
        Path proxyGprFile = Path.of(projectName + "-proxy.gpr");
        try (FileOutput gprOutput = new FileOutput(path.resolve(proxyGprFile))) {
            templateEngine.render(
                    "proxy_gpr.jte",
                    Map.of(
                            "api",
                            api,
                            "relLibPath",
                            path.relativize(projectFile).toString(),
                            "projectName",
                            Name.fromLower(projectName),
                            "proxy",
                            proxy),
                    gprOutput);
        }
        Path aggGprFile = Path.of(projectName + "-proxy-agg.gpr");
        try (FileOutput gprOutput = new FileOutput(path.resolve(aggGprFile))) {
            templateEngine.render(
                    "proxy_agg_gpr.jte",
                    Map.of(
                            "api", api,
                            "relLibPath", path.relativize(projectFile).toString(),
                            "projectName", Name.fromLower(projectName),
                            "proxy", proxy,
                            "runtimeLocation", getRuntimeLocation()),
                    gprOutput);
        }

        // Create the specification and body files.
        Path proxySrc = path.resolve("src");
        for (var pack : proxy.packages) {
            Path packageSpecFile = AdaAPI.toAdaFilename(pack, "-proxy.ads");
            try (FileOutput packageSpec = new FileOutput(proxySrc.resolve(packageSpecFile))) {
                templateEngine.render(
                        "package_ads.jte", Map.of("api", api, "pack", pack), packageSpec);
            }

            Path packageBodyFile = AdaAPI.toAdaFilename(pack, "-proxy.adb");
            try (FileOutput packageBody = new FileOutput(proxySrc.resolve(packageBodyFile))) {
                templateEngine.render(
                        "package_adb.jte", Map.of("api", api, "pack", pack), packageBody);
            }
        }

        // Create the array specific functions
        if (!proxy.arrayTypes.isEmpty()) {
            Path arraySpecFile = Path.of("polyglot-ada-arrays-non_native.ads");
            try (FileOutput arraysSpec = new FileOutput(proxySrc.resolve(arraySpecFile))) {
                templateEngine.render(
                        "arrays_ads.jte",
                        Map.of("api", api, "arrays", proxy.arrayTypes),
                        arraysSpec);
            }
        }
    }
}
