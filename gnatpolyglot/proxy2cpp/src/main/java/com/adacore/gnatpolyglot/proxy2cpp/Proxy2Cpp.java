//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp;

import com.adacore.gnatpolyglot.proxy.GNATpolyglotSetup;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "proxy2cpp", description = "Create the C++ interface of a proxy representation")
public class Proxy2Cpp implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "proxy_file", description = "input proxy json file")
    Path proxyFile;

    @Option(
            names = {"-o", "--output"},
            description =
                    "path to the directory in which the C++ interface should be generated."
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

    @Override
    public Integer call() throws Exception {
        CppPrinter printer = new CppPrinter(proxyFile);

        try {
            Files.createDirectories(outputPath);
        } catch (FileAlreadyExistsException e) {
            System.err.println("%s is not a directory".formatted(e.getMessage()));
            return 1;
        }

        if (withRuntime == null) {
            withRuntime = outputPath.resolve("runtimes");
            new GNATpolyglotSetup(
                            withRuntime,
                            Files.isDirectory(withRuntime),
                            GNATpolyglotSetup.Lang.valueOf(printer.getProxy().inputLanguage),
                            GNATpolyglotSetup.Lang.cpp,
                            true)
                    .call();
        }

        printer.generate(outputPath, withRuntime);
        return 0;
    }
}
