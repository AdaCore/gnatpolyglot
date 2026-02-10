package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.*;
import com.adacore.polyglot.PolyglotUtils;
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

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(
                    Path.of("jte-classes").resolve("ada2proxy"),
                    ContentType.Plain,
                    AdaScanner.class.getClassLoader(),
                    "gg.jte.generate.precompiled.ada2proxy");

    /** The Ada visitor. */
    private AdaVisitor visitor;

    /** The path to the Gpr project file. */
    private Path projectFile;

    /** The name of the project. */
    private String projectName;

    /** The list of files to process. */
    private List<String> specFiles;

    /** The options to use when loading the GPR project file. */
    private ProjectOptions gprOptions;

    /** List of all interfaces used in the project. */
    private List<String> interfaces;

    /** The Ada proxy. */
    private AdaProxy proxy;

    private AdaAPI api;

    public AdaScanner(Path projectFile, List<String> specFiles, ProjectOptions options) {
        this.projectFile = projectFile;
        this.specFiles = specFiles;
        this.gprOptions = options;
        this.projectName =
                projectFile
                        .getFileName()
                        .toString()
                        .substring(0, projectFile.getFileName().toString().lastIndexOf(".gpr"));
        this.api = new AdaAPI(Name.fromLower(projectName));
        this.visitor = new AdaVisitor(api);
    }

    private static String prettyPrint(Libadalang.BasicDecl decl, String message) {
        return "Could not bind %s: %s".formatted(AdaAPI.getDisplayName(decl), message);
    }

    public static void warning(UnbindableDeclException exc) {
        Throwable cause = exc.getCause();
        Libadalang.BasicDecl decl = exc.getDecl();
        String location = decl.fullSlocImage();
        location = location.substring(0, location.length() - 2);
        String message;
        if (cause == null) {
            message = prettyPrint(exc.getDecl(), exc.getMessage());
        } else if (cause instanceof UnbindableDeclException c) {
            warning(c);
            message = prettyPrint(exc.getDecl(), exc.getMessage());
        } else {
            message = prettyPrint(exc.getDecl(), cause.toString());
        }
        PolyglotUtils.emitWarning(location, message);
    }

    public static void error(UnbindableDeclException exc) {
        Throwable cause = exc.getCause();
        Libadalang.BasicDecl decl = exc.getDecl();
        String location = decl.fullSlocImage();
        location = location.substring(0, location.length() - 2);
        String message;
        if (cause == null) {
            message = prettyPrint(exc.getDecl(), exc.getMessage());
        } else if (cause instanceof UnbindableDeclException c) {
            error(c);
            message = prettyPrint(exc.getDecl(), exc.getMessage());
        } else {
            message = prettyPrint(exc.getDecl(), cause.toString());
        }
        PolyglotUtils.emitError(location, message);
    }

    private List<String> getFilesToAnalyze(ProjectManager projectManager)
            throws FileNotFoundException {
        Stream<String> filenames =
                Stream.of(projectManager.getFiles(SourceFileMode.DEFAULT))
                        .filter(s -> s.endsWith(".ads"));
        if (specFiles == null || specFiles.isEmpty()) return filenames.toList();
        // Filter all the spec files to bind.
        List<String> result =
                filenames
                        .filter(
                                specFiles == null || specFiles.isEmpty()
                                        ? (s -> true)
                                        : (s ->
                                                specFiles.contains(
                                                        Path.of(s).getFileName().toString())))
                        .toList();
        // Verify that all the files in ``specFiles`` have been found.
        List<Path> resultBasenames = result.stream().map(s -> Path.of(s).getFileName()).toList();
        for (var file : specFiles) {
            specFiles.stream()
                    .filter(f -> resultBasenames.contains(Path.of(f)))
                    .findFirst()
                    .orElseThrow(() -> new FileNotFoundException(file));
        }
        return result;
    }

    @Override
    public void scanProject() throws FileNotFoundException {
        // Analyze all the ``.ads`` source files.
        ProjectManager projectManager = new ProjectManager(gprOptions, false);
        AnalysisContext ctx = projectManager.createContext(null, null, true, 8);
        List<Package> modules =
                getFilesToAnalyze(projectManager).stream()
                        .map(s -> ctx.getUnitFromFile(s))
                        .map(u -> visitor.analyzeSpec(u))
                        .filter(p -> p != null)
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
        return AdaProxyTranslator.translate(proxy, api);
    }

    @Override
    public void generate(Path path, Path runtimeLocation) throws IOException {

        // Write the json proxy file.
        Proxy jsonProxy = getProxy();
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
                            "runtimeLocation",
                            path.relativize(runtimeLocation).toString(),
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
                            path.relativize(runtimeLocation).toString(),
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

            // Only create a package body file if a declaration requires one
            if (pack.declarations.stream().anyMatch(AdaAPI::requiresBodyPackage)) {
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
