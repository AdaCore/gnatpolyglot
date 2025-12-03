package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.*;
import com.adacore.polyglot.Scanner;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.polyglot.ada2proxy.proxy.Array;
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

    /** List of all interfaces used in the project. */
    private List<String> interfaces;

    /** The Ada proxy. */
    private AdaProxy proxy;

    private static void prettyPrint(UnbindableDeclException exc, String message, String kind) {
        Libadalang.BasicDecl decl = exc.getDecl();
        System.err.println(
                "%s%s: Could not bind %s: %s"
                        .formatted(
                                decl.fullSlocImage(), kind, AdaAPI.getDisplayName(decl), message));
    }

    public static void warning(UnbindableDeclException exc) {
        Throwable cause = exc.getCause();
        if (cause == null) {
            prettyPrint(exc, exc.getMessage(), "warning");
        } else if (cause instanceof UnbindableDeclException c) {
            warning(c);
            prettyPrint(exc, exc.getMessage(), "warning");
        } else {
            prettyPrint(exc, cause.toString(), "warning");
        }
    }

    public static void error(UnbindableDeclException exc) {
        Throwable cause = exc.getCause();
        if (cause == null) {
            prettyPrint(exc, exc.getMessage(), "error");
        } else if (cause instanceof UnbindableDeclException c) {
            error(c);
            prettyPrint(exc, exc.getMessage(), "error");
        } else {
            prettyPrint(exc, cause.toString(), "error");
        }
    }

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
    public void scanProject(Path projectFile, List<String> units, Object options)
            throws FileNotFoundException {
        this.projectFile = projectFile;
        // Get the name of the project
        this.projectName =
                projectFile
                        .getFileName()
                        .toString()
                        .substring(0, projectFile.getFileName().toString().lastIndexOf(".gpr"));

        // Analyze all the ``.ads`` source files.
        ProjectOptions gprOptions = null;
        if (options != null) {
            if (options instanceof Libadalang.ProjectOptions opts) gprOptions = opts;
            else throw new IllegalArgumentException("Options must be a Libadalang.ProjectOptions");
        } else {
            gprOptions = new ProjectOptions();
        }
        gprOptions.addSwitch(ProjectOption.P, projectFile.toString());
        ProjectManager projectManager = new ProjectManager(gprOptions, false);
        AnalysisContext ctx = projectManager.createContext(null, null, true, 8);
        List<Package> modules =
                getFilesToAnalyze(projectManager, units).stream()
                        .map(s -> ctx.getUnitFromFile(s))
                        .map(u -> visitor.analyzeSpec(u))
                        .toList();
        modules =
                Stream.concat(modules.stream(), visitor.getNonVisitedPackages().stream()).toList();
        // In order to write the gpr files, we must know which units of the binded library are used.
        // To do so, get the full dependencies of all the binded units, and filter out those that
        // are not from the project being analyzed.
        List<String> sources =
                Stream.of(projectManager.getFiles(SourceFileMode.DEFAULT))
                        .map(f -> Path.of(f).getFileName().toString())
                        .toList();

        interfaces =
                modules.stream()
                        .flatMap(p -> p.getUnitDependencies(true).stream())
                        .filter(p -> !p.getUnit().equals(p.pStandardUnit()))
                        .distinct()
                        .map(u -> u.getUnit().getFileName(false))
                        .filter(filename -> sources.contains(filename))
                        .toList();
        this.proxy = new AdaProxy(Name.fromLower(projectName), modules);
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
                            "api",
                            api,
                            "relLibPath",
                            path.relativize(projectFile).toString(),
                            "projectName",
                            Name.fromLower(projectName),
                            "proxy",
                            proxy,
                            "runtimeLocation",
                            getRuntimeLocation(),
                            "sources",
                            interfaces),
                    gprOutput);
        }

        // Create the specification and body files.
        Path proxySrc = path.resolve("src");
        for (var pack : proxy.packages) {
            Path packageSpecFile = AdaAPI.toAdaFilename(pack, ".ads");
            try (FileOutput packageSpec = new FileOutput(proxySrc.resolve(packageSpecFile))) {
                templateEngine.render(
                        "package_ads.jte", Map.of("api", api, "pack", pack), packageSpec);
            }

            // Only array do not create function in these package bodies
            if (pack.declarations.stream().anyMatch(d -> !(d instanceof Array))) {
                Path packageBodyFile = AdaAPI.toAdaFilename(pack, ".adb");
                try (FileOutput packageBody = new FileOutput(proxySrc.resolve(packageBodyFile))) {
                    templateEngine.render(
                            "package_adb.jte", Map.of("api", api, "pack", pack), packageBody);
                }
            }
        }

        // Create the array specific functions
        Path arraySpecFile = Path.of("polyglot-ada-arrays-non_native.ads");
        try (FileOutput arraysSpec = new FileOutput(proxySrc.resolve(arraySpecFile))) {
            templateEngine.render("arrays_ads.jte", Map.of("api", api, "proxy", proxy), arraysSpec);
        }

        // Create the exception specific functions
        Path exceptionSpecFile = Path.of("polyglot-exceptions-%s.ads".formatted(projectName));
        try (FileOutput exceptionSpec = new FileOutput(proxySrc.resolve(exceptionSpecFile))) {
            templateEngine.render(
                    "exceptions.jte",
                    Map.of(
                            "api",
                            api,
                            "proxy",
                            proxy,
                            "projectName",
                            Name.fromLower(projectName),
                            "isSource",
                            false),
                    exceptionSpec);
        }
        Path exceptionBodyFile = Path.of("polyglot-exceptions-%s.adb".formatted(projectName));
        try (FileOutput exceptionBody = new FileOutput(proxySrc.resolve(exceptionBodyFile))) {
            templateEngine.render(
                    "exceptions.jte",
                    Map.of(
                            "api",
                            api,
                            "proxy",
                            proxy,
                            "projectName",
                            Name.fromLower(projectName),
                            "isSource",
                            true),
                    exceptionBody);
        }
    }
}
