//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
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
