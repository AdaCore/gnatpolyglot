//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot;

import com.adacore.gnatpolyglot.proxy.Proxy;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.ProxyException;
import com.adacore.gnatpolyglot.proxy.ProxyValidator;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Printer {

    /** The proxy. */
    protected Proxy proxy;

    /** The resolution context of the proxy. */
    protected ProxyContext context;

    public Printer(Path path) throws IOException, ProxyException {
        this.proxy = Proxy.readProxy(path.toFile());
        this.context = ProxyValidator.validateAndGetContext(proxy);
    }

    public Proxy getProxy() {
        return proxy;
    }

    public abstract void generate(Path path, Path runtimeLocation) throws IOException;
}
