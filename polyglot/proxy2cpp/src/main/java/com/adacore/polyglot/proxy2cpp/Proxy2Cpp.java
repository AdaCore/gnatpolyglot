package com.adacore.polyglot.proxy2cpp;

import java.nio.file.Path;

public class Proxy2Cpp {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: ./ada2proxy <proxy> <output_path>");
            System.exit(1);
        }

        CppPrinter printer = new CppPrinter(Path.of(args[0]));
        printer.generate(Path.of(args[1]));
    }
}
