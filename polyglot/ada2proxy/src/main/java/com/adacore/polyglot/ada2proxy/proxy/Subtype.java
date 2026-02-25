//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.Name;

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
        return origin.pDoc();
    }
}
