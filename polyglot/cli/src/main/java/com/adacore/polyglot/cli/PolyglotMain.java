package com.adacore.polyglot.cli;

import com.adacore.polyglot.ada2proxy.Ada2Proxy;
import com.adacore.polyglot.proxy.ProxyValidator;
import com.adacore.polyglot.proxy2cpp.Proxy2Cpp;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "polyglot",
    subcommands = {
        ProxyValidator.class,
        Ada2Proxy.class,
        Proxy2Cpp.class
    }
)
public class PolyglotMain {
    public static void main(String... args) {
        System.exit(new CommandLine(new PolyglotMain()).execute(args));
    }
}
