//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.Printer;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.ProxyException;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JavaPrinter extends Printer {

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(
                    Path.of("jte-classes"),
                    ContentType.Plain,
                    JavaPrinter.class.getClassLoader(),
                    "gg.jte.generate.precompiled.proxy2java");

    private List<String> groupId;

    public JavaPrinter(Path path, List<String> groupId) throws IOException, ProxyException {
        super(path);
        this.groupId = groupId;
    }

    @Override
    public void generate(Path outputPath, Path runtimeLocation) throws IOException {
        JavaAPI api = new JavaAPI(context, groupId, proxy.name);

        // Generate the pom.xml Maven file
        Path pomXml = outputPath.resolve("pom.xml");
        try (FileOutput packageSpec = new FileOutput(pomXml)) {
            templateEngine.render("pom_xml.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }

        // Generate the gprbuild file for the JNI C layer
        Path gprFilename = outputPath.resolve(proxy.name.toLower().concat("_jni.gpr"));
        try (FileOutput packageSpec = new FileOutput(gprFilename)) {
            templateEngine.render("jni_gpr.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }

        Path srcDir = outputPath.resolve("src", "main", "java");
        for (var module : proxy.modules) {
            // Generate the package class that contains free functions of the module.
            Path packageClass = srcDir.resolve(api.filepath(module));
            try (FileOutput packageSpec = new FileOutput(packageClass)) {
                templateEngine.render(
                        "package.jte", Map.of("api", api, "module", module), packageSpec);
            }

            // Generate the corresponding JNI C file.
            Path jniFile =
                    outputPath.resolve(
                            "jni",
                            module.name.join((n) -> n.getLastName().toLower(), "", "_", ".c"));
            try (FileOutput packageSpec = new FileOutput(jniFile)) {
                templateEngine.render(
                        "jni_c.jte", Map.of("api", api, "module", module), packageSpec);
            }

            for (var classDecl :
                    module.declarations.stream().filter(ClassDecl.class::isInstance).toList()) {
                // Generate the class.
                Path clazz = srcDir.resolve(api.filepath((ClassDecl) classDecl));
                try (FileOutput packageSpec = new FileOutput(clazz)) {
                    templateEngine.render(
                            "class.jte", Map.of("api", api, "classDecl", classDecl), packageSpec);
                }
            }
        }

        // Generate the base Library.java file.
        // It contains notable the call to ``System.loadLibrary`` that loads the bound dynamic
        // library.
        Path packageClass = srcDir.resolve("Library.java");
        try (FileOutput packageSpec = new FileOutput(packageClass)) {
            templateEngine.render(
                    "library_java.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }
    }
}
