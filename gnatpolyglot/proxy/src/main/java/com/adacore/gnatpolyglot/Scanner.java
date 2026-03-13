//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot;

import com.adacore.gnatpolyglot.proxy.Proxy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

/** Base class to analyze projects and emit a Json proxy along with the proxy code */
public abstract class Scanner {

    /**
     * Analyze a project and its files.
     *
     * @throws FileNotFoundException
     */
    public abstract void scanProject() throws FileNotFoundException;

    /** Generate the code for the proxy. */
    public abstract void generate(Path path, Path runtimeLocation) throws IOException;

    /** Get the proxy for the project. */
    public abstract Proxy getProxy();
}
