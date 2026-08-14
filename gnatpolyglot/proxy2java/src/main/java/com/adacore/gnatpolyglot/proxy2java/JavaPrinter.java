//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.Printer;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.ExceptionDecl;
import com.adacore.gnatpolyglot.proxy.ProxyException;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

    private Path proxyLocation;

    public JavaPrinter(Path path, List<String> groupId) throws IOException, ProxyException {
        super(path);
        this.groupId = new ArrayList<>(groupId);
        this.groupId.add("lib" + proxy.name.toLower());
        this.proxyLocation = path.getParent();
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
            templateEngine.render(
                    "jni_gpr.jte",
                    Map.of(
                            "api",
                            api,
                            "proxy",
                            proxy,
                            "runtimeLocation",
                            outputPath.relativize(runtimeLocation).toString(),
                            "proxyLocation",
                            outputPath
                                    .toAbsolutePath()
                                    .relativize(proxyLocation.toAbsolutePath())
                                    .toString()),
                    packageSpec);
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
                Path clazz = srcDir.resolve(api.filepath(classDecl.name));
                try (FileOutput packageSpec = new FileOutput(clazz)) {
                    templateEngine.render(
                            "class.jte", Map.of("api", api, "classDecl", classDecl), packageSpec);
                }
            }

            for (var exceptionDecl :
                    module.declarations.stream().filter(ExceptionDecl.class::isInstance).toList()) {
                // Generate the class.
                Path exception = srcDir.resolve(api.filepath(exceptionDecl.name));
                try (FileOutput packageSpec = new FileOutput(exception)) {
                    templateEngine.render(
                            "exception.jte",
                            Map.of("api", api, "exceptionDecl", exceptionDecl),
                            packageSpec);
                }
            }

            for (var enumDecl :
                    module.declarations.stream()
                            .filter(EnumerationDecl.class::isInstance)
                            .toList()) {
                // Generate the class.
                Path clazz = srcDir.resolve(api.filepath(enumDecl.name));
                try (FileOutput packageSpec = new FileOutput(clazz)) {
                    templateEngine.render(
                            "enum.jte", Map.of("api", api, "enumDecl", enumDecl), packageSpec);
                }
            }
        }

        // Generate the base Library.java file.
        // It contains notable the call to ``System.loadLibrary`` that loads the bound dynamic
        // library.
        Path packageClass =
                srcDir.resolve(String.join(File.separator, groupId)).resolve("Library.java");
        try (FileOutput packageSpec = new FileOutput(packageClass)) {
            templateEngine.render(
                    "library_java.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }

        // Generate the Callbacks.java file.
        Path callbacksClass =
                srcDir.resolve(String.join(File.separator, groupId)).resolve("Callbacks.java");
        try (FileOutput packageSpec = new FileOutput(callbacksClass)) {
            templateEngine.render("callbacks.jte", Map.of("api", api, "proxy", proxy), packageSpec);
        }

        Path refsHeader = outputPath.resolve("jni", proxy.name.toLower().concat("__refs.h"));
        try (FileOutput packageSpec = new FileOutput(refsHeader)) {
            templateEngine.render(
                    "jni/refs.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", false),
                    packageSpec);
        }

        Path refsBody = outputPath.resolve("jni", proxy.name.toLower().concat("__refs.c"));
        try (FileOutput packageSpec = new FileOutput(refsBody)) {
            templateEngine.render(
                    "jni/refs.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", true),
                    packageSpec);
        }

        Path classesHeader = outputPath.resolve("jni", proxy.name.toLower().concat("__classes.h"));
        try (FileOutput packageSpec = new FileOutput(classesHeader)) {
            templateEngine.render(
                    "jni/classes.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", false),
                    packageSpec);
        }

        Path classesBody = outputPath.resolve("jni", proxy.name.toLower().concat("__classes.c"));
        try (FileOutput packageSpec = new FileOutput(classesBody)) {
            templateEngine.render(
                    "jni/classes.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", true),
                    packageSpec);
        }

        Path callbacksHeader =
                outputPath.resolve("jni", proxy.name.toLower().concat("__callbacks.h"));
        try (FileOutput packageSpec = new FileOutput(callbacksHeader)) {
            templateEngine.render(
                    "jni/callbacks.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", false),
                    packageSpec);
        }

        Path callbacksBody =
                outputPath.resolve("jni", proxy.name.toLower().concat("__callbacks.c"));
        try (FileOutput packageSpec = new FileOutput(callbacksBody)) {
            templateEngine.render(
                    "jni/callbacks.jte",
                    Map.of("api", api, "proxy", proxy, "isSource", true),
                    packageSpec);
        }
    }
}
