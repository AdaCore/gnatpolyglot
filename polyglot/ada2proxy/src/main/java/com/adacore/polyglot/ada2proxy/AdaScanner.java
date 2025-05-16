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

    @Override
    public void scanProject(Path projectFile) {
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
                Stream.of(projectManager.getFiles(SourceFileMode.DEFAULT))
                        .filter(s -> s.endsWith(".ads"))
                        .map(s -> ctx.getUnitFromFile(s))
                        .map(u -> visitor.analyzeSpec(u))
                        .toList();
        this.proxy = new AdaProxy(modules);
    }

    @Override
    public Proxy getProxy() {
        return proxy.toPolyglotProxy();
    }

    @Override
    public void generate(Path path) throws IOException {
        // Write the json proxy file.
        try {
            proxy.toPolyglotProxy().writeProxy(path.resolve("proxy.json").toFile());
        } catch (Exception e) {
            throw new IOException(e);
        }

        // Generate the Gpr file for the proxy.
        Path proxyGprFile = Path.of(projectName + "_proxy.gpr");
        try (FileOutput gprOutput = new FileOutput(path.resolve(proxyGprFile))) {
            templateEngine.render(
                    "proxy_gpr.jte",
                    Map.of(
                            "relLibPath", path.relativize(projectFile).toString(),
                            "projectName", Name.fromLower(projectName),
                            "proxy", proxy),
                    gprOutput);
        }

        // Create the specification and body files.
        Path proxySrc = path.resolve("src");
        for (var pack : proxy.packages) {
            Path packageSpecFile = AdaAPI.toAdaFilename(pack, "-proxy.ads");
            try (FileOutput packageSpec = new FileOutput(proxySrc.resolve(packageSpecFile))) {
                templateEngine.render("package_ads.jte", proxy.packages.get(0), packageSpec);
            }

            Path packageBodyFile = AdaAPI.toAdaFilename(pack, "-proxy.adb");
            try (FileOutput packageBody = new FileOutput(proxySrc.resolve(packageBodyFile))) {
                templateEngine.render("package_adb.jte", proxy.packages.get(0), packageBody);
            }
        }
    }
}
