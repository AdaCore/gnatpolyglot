package com.adacore.polyglot.cli;

import com.adacore.polyglot.ada2proxy.Ada2Proxy;
import com.adacore.polyglot.proxy.PolyglotSetup;
import com.adacore.polyglot.proxy.ProxyValidator;
import com.adacore.polyglot.proxy2cpp.Proxy2Cpp;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "polyglot",
        subcommands = {ProxyValidator.class, Ada2Proxy.class, Proxy2Cpp.class, PolyglotSetup.class},
        // Version information are substituted by anod for production builds
        version = {
            "Polyglot <POLYGLOT_VERSION>",
            "Copyright (C) 2025-2026, AdaCore.",
            "Tools are licensed under GPL-3.0-or-later.",
            "Runtime libraries are licensed under Apache-2.0."
        },
        description = {
            "Polyglot - Multi-language interface generator.",
            "See documentation for more information."
        })
public class PolyglotMain {
    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "display usage and exit")
    boolean helpRequested;

    @Option(
            names = {"-v", "--version"},
            versionHelp = true,
            description = "display version and exit")
    boolean versionRequested;

    public static void main(String... args) {
        System.exit(new CommandLine(new PolyglotMain()).execute(args));
    }
}
