//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.Name;

public class EnumLiteral implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.EnumLiteralDecl origin;

    /** Name of the enumeration item. */
    public Name name;

    /** Integer value of the enumeration item. */
    public int value;

    public EnumLiteral(Libadalang.EnumLiteralDecl origin, Name name, int value) {
        this.origin = origin;
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getDoc() {
        return origin.pDoc();
    }
}
