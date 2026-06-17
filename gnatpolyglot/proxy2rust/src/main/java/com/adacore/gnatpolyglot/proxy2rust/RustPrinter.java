//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust;

import com.adacore.gnatpolyglot.Printer;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.ProxyException;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RustPrinter extends Printer {

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(
                    Path.of("jte-classes"),
                    ContentType.Plain,
                    RustPrinter.class.getClassLoader(),
                    "gg.jte.generate.precompiled.proxy2rust");

    public RustPrinter(Path path) throws IOException, ProxyException {
        super(path);
    }

    @Override
    public void generate(Path outputPath, Path runtimeLocation) throws IOException {
        RustAPI api = new RustAPI(context, proxy.name);

        // Generate Cargo.toml
        Path cargoToml = outputPath.resolve("Cargo.toml");
        try (FileOutput out = new FileOutput(cargoToml)) {
            templateEngine.render("cargo_toml.jte", Map.of("api", api, "proxy", proxy), out);
        }

        // Generate build.rs
        Path buildRs = outputPath.resolve("build.rs");
        try (FileOutput out = new FileOutput(buildRs)) {
            templateEngine.render("build_rs.jte", Map.of("api", api, "proxy", proxy), out);
        }

        // Generate src/lib.rs
        Path srcDir = outputPath.resolve("src");
        Files.createDirectories(srcDir);
        Path libRs = srcDir.resolve("lib.rs");
        try (FileOutput out = new FileOutput(libRs)) {
            templateEngine.render("lib_rs.jte", Map.of("api", api, "proxy", proxy), out);
        }

        // Generate per-module src/<module>.rs
        for (var module : proxy.modules) {
            if (module.name.names.get(0).equals(Name.fromLower("gnatpolyglot"))) continue;

            Path moduleFile = srcDir.resolve(module.name.getLastName().toLower() + ".rs");
            try (FileOutput out = new FileOutput(moduleFile)) {
                templateEngine.render("module.jte", Map.of("api", api, "module", module), out);
            }
        }
    }
}
