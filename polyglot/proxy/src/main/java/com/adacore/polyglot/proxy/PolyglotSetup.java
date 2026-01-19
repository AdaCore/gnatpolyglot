package com.adacore.polyglot.proxy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "setup", description = "Setup the Polyglot runtime")
public class PolyglotSetup implements Callable<Integer> {

    @Option(
            names = {"--prefix"},
            description = "installation path",
            required = true)
    Path prefixPath;

    @Option(
            names = {"--check-only"},
            description =
                    "do not copy the runtime. Instead, emitt warnings if the runtime was modified.")
    boolean checkOnly;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display sub-command usage and exit")
    boolean helpRequested;

    public PolyglotSetup() {}

    public PolyglotSetup(Path prefixPath, boolean checkOnly) {
        this.prefixPath = prefixPath;
        this.checkOnly = checkOnly;
    }

    /** Return the location of the runtime found in the "POLYGLOT_RUNTIME" environment variable. */
    public static String getInstallRuntimeLocation() {
        return System.getenv("POLYGLOT_RUNTIME");
    }

    @Override
    public Integer call() throws Exception {

        Path runtime = Path.of(getInstallRuntimeLocation());
        boolean diffFound = false;
        for (var p : Files.walk(runtime).toList()) {
            Path result = prefixPath.resolve(runtime.relativize(p));
            if (Files.isDirectory(p)) {
                Files.createDirectories(result);
            } else if (Files.isRegularFile(p)) {
                if (checkOnly) {
                    if (!Files.isRegularFile(result)) {
                        System.err.format("warning: %s is missing\n", result);
                        diffFound = true;
                    } else if (Files.mismatch(p, result) != -1L) {
                        System.err.format("warning: %s was modified\n", result);
                        diffFound = true;
                    }
                } else {
                    Files.copy(p, result, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        if (checkOnly && diffFound) {
            System.err.format(
                    "warning: consider running `polyglot setup --prefix=%s'\n", prefixPath);
        }

        return 0;
    }
}
