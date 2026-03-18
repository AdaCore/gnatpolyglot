//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.adacore.gnatpolyglot.GNATpolyglotUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "setup",
        description = {
            "Set up the GNATpolyglot runtime.",
            "If none of --from-lang or --to-lang is passed, then the entire runtime is created.",
            "If only --from-lang is set, only the runtime used to build the bindings for that"
                    + " language is created.",
            "If --from-lang and --to-lang are both set, both the runtime for building the bindings"
                + " and the interfaces are created, unless --target-only is set in which case only"
                + " the runtime for the interfaces is created.",
            "Note that --to-lang cannot be used without --from-lang.",
        },
        sortOptions = false)
public class GNATpolyglotSetup implements Callable<Integer> {

    public static enum Lang {
        ada("Ada"),
        cpp("C++"),
        java("Java");

        public String name;

        Lang(String name) {
            this.name = name;
        }
    }

    @Option(
            names = {"--prefix"},
            description = "installation path",
            required = true)
    Path prefixPath;

    @Option(
            names = {"--check-only"},
            description =
                    "do not copy the runtime. Instead, emit warnings if the runtime was modified.")
    boolean checkOnly;

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display sub-command usage and exit")
    boolean helpRequested;

    @Option(
            names = {"--from-lang"},
            description = "input language")
    Lang fromLang;

    @Option(
            names = {"--to-lang"},
            description = "output language")
    Lang toLang;

    @Option(
            names = {"--target-only"},
            description = "only create the runtime for the target")
    boolean targetOnly = false;

    public GNATpolyglotSetup() {}

    public GNATpolyglotSetup(Path prefixPath, boolean checkOnly, Lang fromLang) {
        this.prefixPath = prefixPath;
        this.checkOnly = checkOnly;
        this.fromLang = fromLang;
    }

    public GNATpolyglotSetup(
            Path prefixPath, boolean checkOnly, Lang fromLang, Lang toLang, boolean targetOnly) {
        this.prefixPath = prefixPath;
        this.checkOnly = checkOnly;
        this.fromLang = fromLang;
        this.toLang = toLang;
        this.targetOnly = targetOnly;
    }

    /** Return the location of the runtime found in the "POLYGLOT_RUNTIME" environment variable. */
    public static String getInstallRuntimeLocation() {
        return System.getenv("POLYGLOT_RUNTIME");
    }

    private List<Path> getSourcesFromList(Path pathToList) throws IOException {
        Path runtime = Path.of(getInstallRuntimeLocation());
        ArrayList<Path> res = new ArrayList<>();
        for (var p : Files.readAllLines(pathToList)) {
            res.addAll(Files.walk(runtime.resolve(p)).map(runtime::resolve).toList());
        }
        return res;
    }

    public List<Path> getSources() throws IOException {
        return Files.walk(Path.of(getInstallRuntimeLocation())).toList();
    }

    public List<Path> getSources(Lang fromLang) throws IOException {
        Path srcFile =
                Path.of(
                        getInstallRuntimeLocation(),
                        "%s2proxy-sources.lst".formatted(fromLang.toString()));
        if (!Files.isRegularFile(srcFile))
            throw new IllegalArgumentException("No runtime found for %s".formatted(fromLang.name));
        return getSourcesFromList(srcFile);
    }

    public List<Path> getSources(Lang fromLang, Lang toLang) throws IOException {
        Path srcFile =
                Path.of(
                        getInstallRuntimeLocation(),
                        "%s2%s-sources.lst".formatted(fromLang.toString(), toLang.toString()));
        if (!Files.isRegularFile(srcFile))
            throw new IllegalArgumentException(
                    "No runtime found for targetting %s from %s"
                            .formatted(toLang.name, fromLang.name));
        List<Path> sources = getSourcesFromList(srcFile);
        if (!targetOnly) sources.addAll(getSources(fromLang));
        return sources;
    }

    @Override
    public Integer call() throws Exception {
        Path runtime = Path.of(getInstallRuntimeLocation());
        boolean diffFound = false;
        final List<Path> paths;
        try {
            if (toLang != null && fromLang == null) {
                throw new IllegalArgumentException("--from-lang must be set if --to-lang is set");
            } else if (toLang == null && targetOnly) {
                throw new IllegalArgumentException("--to-lang must be set if --target-only is set");
            } else if (toLang != null && fromLang != null) {
                paths = getSources(fromLang, toLang);
            } else if (fromLang != null) {
                paths = getSources(fromLang);
            } else {
                paths = getSources();
            }
        } catch (IllegalArgumentException e) {
            GNATpolyglotUtils.emitError(e.getMessage());
            return 1;
        } catch (IOException e) {
            GNATpolyglotUtils.emitError("Could not get runtime: %s".formatted(e.getMessage()));
            return 1;
        }
        for (var p : paths) {
            Path result = prefixPath.resolve(runtime.relativize(p));
            if (Files.isDirectory(p)) {
                Files.createDirectories(result);
            } else if (Files.isRegularFile(p)) {
                if (checkOnly) {
                    if (!Files.isRegularFile(result)) {
                        GNATpolyglotUtils.emitWarning("%s is missing".formatted(result));
                        diffFound = true;
                    } else if (Files.mismatch(p, result) != -1L) {
                        GNATpolyglotUtils.emitWarning("%s was modified".formatted(result));
                        diffFound = true;
                    }
                } else {
                    Files.createDirectories(result.getParent());
                    Files.copy(p, result, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        if (checkOnly && diffFound) {
            GNATpolyglotUtils.emitWarning(
                    "consider running `gnatpolyglot setup --prefix=%s'".formatted(prefixPath));
        }

        return 0;
    }
}
