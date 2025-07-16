package com.adacore.polyglot.ada2proxy;

import com.adacore.polyglot.proxy.Proxy;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "ada2proxy",
        description = "create a proxy for an ada project",
        abbreviateSynopsis = true,
        sortOptions = false)
public class Ada2Proxy implements Callable<Integer> {

    @Option(
            names = {"-P"},
            paramLabel = "project_file",
            description = "input project file",
            required = true)
    Path project;

    @Option(
            names = {"-o", "--output"},
            description = "output path",
            required = true)
    Path outputPath;

    @Option(
            names = {"--units"},
            description = "output path")
    List<String> units = new ArrayList<>();

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws Exception {
        AdaScanner scanner = new AdaScanner();

        try {
            scanner.scanProject(project, units);
        } catch (FileNotFoundException e) {
            spec.commandLine().getColorScheme().errorText(null);
            System.err.println(
                    spec.commandLine()
                            .getColorScheme()
                            .errorText("File not found: %s".formatted(e.getMessage())));
            return 1;
        }

        Proxy p = scanner.getProxy();
        p.validate();

        try {
            Files.createDirectories(outputPath);
        } catch (FileAlreadyExistsException e) {
            System.err.println(
                    spec.commandLine()
                            .getColorScheme()
                            .errorText("%s is not a directory".formatted(e.getMessage())));
            return 1;
        }

        scanner.generate(outputPath);
        return 0;
    }
}
