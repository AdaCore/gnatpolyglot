package com.adacore.polyglot.proxy2cpp;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "proxy2cpp", description = "create the C++ interface of a proxy representation")
public class Proxy2Cpp implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "proxy_file", description = "input proxy json file")
    Path gprfile;

    @Option(
            names = {"-o", "--output"},
            description = "output path",
            required = true)
    Path outputPath;

    @Override
    public Integer call() throws Exception {
        CppPrinter printer = new CppPrinter(gprfile);

        try {
            Files.createDirectories(outputPath);
        } catch (FileAlreadyExistsException e) {
            System.err.println("%s is not a directory".formatted(e.getMessage()));
            return 1;
        }

        printer.generate(outputPath);
        return 0;
    }
}
