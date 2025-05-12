package com.adacore.polyglot.ada2proxy;

import com.adacore.polyglot.proxy.Proxy;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ada2proxy", description = "create a proxy for an ada project")
public class Ada2Proxy implements Callable<Integer> {

    @Parameters(index = "0", paramLabel = "project_file", description = "input project file")
    Path gprfile;

    @Option(
            names = {"output", "-o"},
            description = "output path",
            required = true)
    Path outputPath;

    @Override
    public Integer call() throws Exception {
        AdaScanner scanner = new AdaScanner();
        scanner.scanProject(gprfile);
        Proxy p = scanner.getProxy();
        p.validate();

        try {
            Files.createDirectories(outputPath);
        } catch (FileAlreadyExistsException e) {
            System.err.println("%s is not a directory".formatted(e.getMessage()));
            return 1;
        }

        scanner.generate(outputPath);
        return 0;
    }
}
