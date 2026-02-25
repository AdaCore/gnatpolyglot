//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.polyglot.proxy.Name;

public abstract class AdaDeclaration implements AdaProxyObject {

    /** Name of the declaration. */
    public Name name;

    public AdaDeclaration(Name name) {
        this.name = name;
    }

    public abstract String getDoc();
}
