//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.libadalang.Libadalang;

public class Subtype extends AdaDeclaration {

    private Libadalang.BaseSubtypeDecl origin;

    public Subtype(Libadalang.BaseSubtypeDecl origin, Name name) {
        super(name);
        this.origin = origin;
    }

    public String getFullyQualifiedName() {
        return origin.pFullyQualifiedName();
    }

    public Libadalang.BaseTypeDecl getDecl() {
        return origin;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return AdaAPI.getDoc(origin);
    }
}
