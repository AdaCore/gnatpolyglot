package com.adacore.polyglot.ada2proxy;

import com.adacore.polyglot.proxy.Proxy;
import java.nio.file.Path;

public class Ada2Proxy {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: ./ada2proxy <gprfile> <output_path>");
            System.exit(1);
        }
        AdaScanner scanner = new AdaScanner();
        scanner.scanProject(Path.of(args[0]));
        Proxy p = scanner.getProxy();
        p.validate();
        scanner.generate(Path.of(args[1]));
    }
}
