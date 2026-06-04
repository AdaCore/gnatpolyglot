//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.libadalang.Libadalang;

public class EnumLiteral implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.EnumLiteralDecl origin;

    /** Name of the enumeration item. */
    public Name name;

    /** Integer value of the enumeration item. */
    public long value;

    public EnumLiteral(Libadalang.EnumLiteralDecl origin, Name name, long value) {
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
