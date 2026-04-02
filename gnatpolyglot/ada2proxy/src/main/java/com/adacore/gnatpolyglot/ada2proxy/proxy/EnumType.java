//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.libadalang.Libadalang;
import java.util.List;

public class EnumType extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    /** List of enumeration items. */
    public List<EnumLiteral> items;

    public EnumType(Libadalang.TypeDecl origin, Name name, List<EnumLiteral> items) {
        super(name);
        this.origin = origin;
        this.items = items;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(origin);
    }

    @Override
    public String getDoc() {
        return origin.pDoc();
    }
}
