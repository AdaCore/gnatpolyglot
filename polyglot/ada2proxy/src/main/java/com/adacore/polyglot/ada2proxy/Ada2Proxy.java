//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.PolyglotSetup;
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
        description = {
            "Create a proxy for the given Ada project. ",
            "If no unit is explicitly passed through --spec-files, generate bindings for all"
                + " sources of the project tree, including those of the subprojects, but not those"
                + " of the Ada runtime.",
            "If --process-runtime is set, also generate bindings for all the sources of the"
                    + " runtime.",
            "If --no-subprojects is set, only generate bindings for the root project.",
            "Note that --process-runtime and --no-subprojects are illegal as soon as --spec-files"
                    + " is used.",
            "Moreover, please note that polyglot will always make sure that the generated library"
                + " is usable, which means it may have to include entities that were not part of"
                + " the original files."
        },
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
            names = {"--spec-files"},
            description =
                    "filenames of the units of the input project to bind, separated by commas",
            split = ",")
    List<String> specFiles = new ArrayList<>();

    @Option(
            names = {"-X"},
            description = "Scenario variables to pass to the project file")
    List<String> scenarioVariables = new ArrayList<>();

    @Option(
            names = {"--RTS"},
            description = "Name of the runtime (RTS) to use when loading the project")
    String rts;

    @Option(
            names = {"--target"},
            description = "Specify a target for cross platforms")
    String target;

    @Option(
            names = {"--no-subprojects"},
            description = "Only process units of the root project")
    boolean noSubprojects;

    @Option(
            names = {"--process-runtime"},
            description = "Also process the sources of the Ada runtime")
    boolean processAdaRuntime;

    @Option(
            names = {"--with-runtime"},
            description =
                    "location of the polyglot runtime to use. Defaults to <outputPath>/runtimes")
    Path withRuntime;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display sub-command usage and exit")
    boolean helpRequested;

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    private Libadalang.ProjectOptions getProjectOptions() {
        Libadalang.ProjectOptions options = new Libadalang.ProjectOptions();
        options.addSwitch(Libadalang.ProjectOption.P, project.toString());
        if (rts != null) {
            options.addSwitch(Libadalang.ProjectOption.RTS, rts);
        }
        if (target != null) {
            options.addSwitch(Libadalang.ProjectOption.TARGET, target);
        }
        for (var scenarioVariable : scenarioVariables) {
            options.addSwitch(Libadalang.ProjectOption.X, scenarioVariable);
        }
        return options;
    }

    private Libadalang.SourceFileMode getSourceFileMode() {
        Libadalang.SourceFileMode mode = Libadalang.SourceFileMode.WHOLE_PROJECT;

        if (!specFiles.isEmpty() && noSubprojects) {
            throw new IllegalArgumentException(
                    "--spec-files is incompatible with --no-subprojects");
        } else if (!specFiles.isEmpty() && processAdaRuntime) {
            throw new IllegalArgumentException(
                    "--spec-files is incompatible with --process-runtime");
        } else if (processAdaRuntime && noSubprojects) {
            throw new IllegalArgumentException(
                    "--no-subprojects is incompatible with --process-runtime");
        } else if (noSubprojects) {
            mode = Libadalang.SourceFileMode.ROOT_PROJECT;
        } else if (processAdaRuntime) {
            mode = Libadalang.SourceFileMode.WHOLE_PROJECT_WITH_RUNTIME;
        }
        return mode;
    }

    @Override
    public Integer call() throws Exception {
        Libadalang.SourceFileMode mode;
        try {
            mode = getSourceFileMode();
        } catch (IllegalArgumentException e) {
            System.err.println(spec.commandLine().getColorScheme().errorText(e.getMessage()));
            return 1;
        }
        AdaScanner scanner = new AdaScanner(project, specFiles, getProjectOptions(), mode);

        try {
            scanner.scanProject();
        } catch (FileNotFoundException e) {
            System.err.println(
                    spec.commandLine()
                            .getColorScheme()
                            .errorText("File not found: %s".formatted(e.getMessage())));
            return 1;
        } catch (Libadalang.ProjectManagerException e) {
            System.err.println(
                    spec.commandLine()
                            .getColorScheme()
                            .errorText(
                                    "Error loading GPR project file: %s"
                                            .formatted(e.getMessage())));
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

        if (withRuntime == null) {
            withRuntime = outputPath.resolve("runtimes");
            new PolyglotSetup(withRuntime, Files.isDirectory(withRuntime)).call();
        }
        scanner.generate(outputPath, withRuntime);
        return 0;
    }
}
