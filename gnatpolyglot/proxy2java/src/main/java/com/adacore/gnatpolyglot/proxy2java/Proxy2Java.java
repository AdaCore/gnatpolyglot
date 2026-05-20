//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.proxy.GNATpolyglotSetup;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "proxy2java", description = "Create the Java interface of a proxy representation")
public class Proxy2Java implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "proxy_file", description = "input proxy json file")
    Path proxyFile;

    @Option(
            names = {"-o", "--output"},
            description =
                    "path to the directory in which the Java interface should be generated."
                            + " Directories along the path are created if they do not exist yet.",
            required = true)
    Path outputPath;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display sub-command usage and exit")
    boolean helpRequested;

    @Option(
            names = {"--with-runtime"},
            description =
                    "location of the GNATpolyglot runtime to use. Defaults to"
                            + " <outputPath>/runtimes")
    Path withRuntime;

    @Option(
            names = {"--group-id"},
            description = "groupId of the generated Maven project file.",
            split = ".")
    List<String> groupId = List.of("com", "adacore");

    @Override
    public Integer call() throws Exception {
        JavaPrinter printer = new JavaPrinter(proxyFile, groupId);

        if (withRuntime == null) {
            withRuntime = outputPath.resolve("runtimes");
            new GNATpolyglotSetup(
                            withRuntime,
                            Files.isDirectory(withRuntime),
                            GNATpolyglotSetup.Lang.valueOf(printer.getProxy().inputLanguage),
                            GNATpolyglotSetup.Lang.java,
                            true)
                    .call();
        }

        printer.generate(outputPath, withRuntime);

        return 0;
    }
}
