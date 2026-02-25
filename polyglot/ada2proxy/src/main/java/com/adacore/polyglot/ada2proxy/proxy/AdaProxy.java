//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.polyglot.proxy.Name;
import java.util.List;

public class AdaProxy implements AdaProxyObject {

    /** Name of the project */
    public Name name;

    /** List of Ada packages to generate in the proxy. */
    public List<Package> packages;

    public AdaProxy(Name name, List<Package> declarations) {
        this.name = name;
        this.packages = declarations;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
