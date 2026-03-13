//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

public interface AdaProxyVisitor<T> {
    T visit(AdaProxy proxy);

    T visit(Package proxy);

    T visit(Subprogram subprogram);

    T visit(EnumType enumType);

    T visit(SubpParam subpParam);

    T visit(EnumLiteral enumLiteral);

    T visit(Record rec);

    T visit(Component component);

    T visit(Array array);

    T visit(AdaException adaException);

    T visit(GlobalVariable globalVariable);

    T visit(Subtype subtype);
}
