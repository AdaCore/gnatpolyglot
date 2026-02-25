//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.Printer;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.ProxyException;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class CppPrinter extends Printer {

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(
                    Path.of("jte-classes"),
                    ContentType.Plain,
                    CppPrinter.class.getClassLoader(),
                    "gg.jte.generate.precompiled.proxy2cpp");

    public CppPrinter(Path path) throws IOException, ProxyException {
        super(path);
    }

    @Override
    public void generate(Path outputPath) throws IOException {
        CppAPI api = new CppAPI(context, outputPath, proxy.name);

        for (var module : proxy.modules) {
            if (module.name.names.get(0).equals(Name.fromLower("polyglot"))) continue;

            Path headerFilename = api.headerFilePath(module);
            try (FileOutput packageSpec = new FileOutput(headerFilename)) {
                templateEngine.render(
                        "header.jte", Map.of("api", api, "module", module), packageSpec);
            }

            Path sourceFilename = api.sourceFilePath(module);
            try (FileOutput packageSpec = new FileOutput(sourceFilename)) {
                templateEngine.render(
                        "source.jte", Map.of("api", api, "module", module), packageSpec);
            }
        }

        Optional<Module> arrayModule =
                proxy.modules.stream()
                        .filter(
                                m ->
                                        m.name.names.get(0).equals(Name.fromLower("polyglot"))
                                                && m.name.getLastName()
                                                        .equals(Name.fromLower("arrays")))
                        .findFirst();
        if (arrayModule.isPresent()) {
            Module module = arrayModule.get();
            Path sourceFilename = api.sourceFilePath(module);
            try (FileOutput packageSpec = new FileOutput(sourceFilename)) {
                templateEngine.render(
                        "arrays.jte", Map.of("api", api, "module", module), packageSpec);
            }
        }

        // Emit the proxy exception files
        Path headerFilename =
                outputPath.resolve("polyglot_exceptions_" + proxy.name.toLower() + ".h");
        try (FileOutput packageSpec = new FileOutput(headerFilename)) {
            templateEngine.render(
                    "exception_handler.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", false),
                    packageSpec);
        }

        Path sourceFilename =
                outputPath.resolve("polyglot_exceptions_" + proxy.name.toLower() + ".cpp");
        try (FileOutput packageSpec = new FileOutput(sourceFilename)) {
            templateEngine.render(
                    "exception_handler.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", true),
                    packageSpec);
        }

        Path gprFilename = outputPath.resolve(proxy.name.toLower() + "_2cpp.gpr");
        try (FileOutput packageSpec = new FileOutput(gprFilename)) {
            templateEngine.render(
                    "gpr_project.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }
    }
}
