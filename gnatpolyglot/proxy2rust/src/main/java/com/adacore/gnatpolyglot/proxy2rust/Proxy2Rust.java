//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "proxy2rust", description = "Create the Rust interface of a proxy representation")
public class Proxy2Rust implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "proxy_file", description = "input proxy json file")
    Path proxyFile;

    @Option(
            names = {"-o", "--output"},
            description =
                    "path to the directory in which the Rust interface should be generated."
                            + " Directories along the path are created if they do not exist yet.",
            required = true)
    Path outputPath;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display sub-command usage and exit")
    boolean helpRequested;

    @Override
    public Integer call() throws Exception {
        RustPrinter printer = new RustPrinter(proxyFile);

        try {
            Files.createDirectories(outputPath);
        } catch (FileAlreadyExistsException e) {
            System.err.println("%s is not a directory".formatted(e.getMessage()));
            return 1;
        }

        printer.generate(outputPath, null);

        return 0;
    }
}
