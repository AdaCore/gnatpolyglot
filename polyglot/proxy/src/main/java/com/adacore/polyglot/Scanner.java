package com.adacore.polyglot;

import com.adacore.polyglot.proxy.Proxy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Base class to analyze projects and emit a Json proxy along with the proxy code */
public abstract class Scanner {

    /** Analyze a project and its files. */
    public abstract void scanProject(Path projectFile);

    /** Generate the code for the proxy. */
    public abstract void generate(Path path) throws IOException;

    /** Get the proxy for the project. */
    public abstract Proxy getProxy();

    /**
     * Return the location of the runtime. Supposes that it is located at
     * ${dirname(polyglotExec)}/../polyglot/runtimes
     */
    public static String getRuntimeLocation() {
        return ProcessHandle.current()
                .info()
                .command()
                .map(Paths::get)
                .orElseThrow()
                .getParent()
                .getParent()
                .resolve(Path.of("polyglot", "runtimes"))
                .toAbsolutePath()
                .toString();
    }
}
