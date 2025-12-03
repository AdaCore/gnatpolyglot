package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
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
        description =
                "Create a proxy for an ada project."
                        + " If no unit are explicitly given, process all of them.",
        abbreviateSynopsis = true,
        sortOptions = false)
public class Ada2Proxy implements Callable<Integer> {

    @Option(
            names = {"-P"},
            paramLabel = "project_file",
            description = "input GPR project file",
            required = true)
    Path project;

    @Option(
            names = {"-o", "--output"},
            description = "output path for the proxy project",
            required = true)
    Path outputPath;

    @Option(
            names = {"--units"},
            description = "filenames of the units of the input project to bind")
    List<String> units = new ArrayList<>();

    @Option(
            names = {"-X"},
            description = "Scenario variables to pass to the project file")
    List<String> scenarioVariables = new ArrayList<>();

    @Option(
            names = {"--RTS"},
            description = "Name of the runtime (RTS) to use when loading the project")
    String rts;

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    private Libadalang.ProjectOptions getProjectOptions() {
        Libadalang.ProjectOptions options = new Libadalang.ProjectOptions();
        if (rts != null) {
            options.addSwitch(Libadalang.ProjectOption.RTS, rts);
        }
        for (var scenarioVariable : scenarioVariables) {
            options.addSwitch(Libadalang.ProjectOption.X, scenarioVariable);
        }
        return options;
    }

    @Override
    public Integer call() throws Exception {
        AdaScanner scanner = new AdaScanner();

        try {
            scanner.scanProject(project, units, getProjectOptions());
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
