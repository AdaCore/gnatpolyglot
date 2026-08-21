//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import java.util.List;
import java.util.stream.Collectors;

public class CppGenerator {

    public static StringBuilder makeCast(
            CharSequence castKind, CharSequence typename, CharSequence expr) {
        return new StringBuilder(castKind)
                .append("<")
                .append(typename)
                .append(">(")
                .append(expr)
                .append(")");
    }

    /** Create a static_cast expression. */
    public static StringBuilder makeStaticCast(CharSequence typename, CharSequence expr) {
        return makeCast("static_cast", typename, expr);
    }

    /** Create a reinterpret_cast expression. */
    public static StringBuilder makeReinterpretCast(CharSequence typename, CharSequence expr) {
        return makeCast("reinterpret_cast", typename, expr);
    }

    /** Create a static_cast expression. */
    public static StringBuilder getData(CharSequence expr) {
        return new StringBuilder(expr).append(".data_()");
    }

    /** Create a ternary expression. */
    public static StringBuilder makeTernary(
            CharSequence cond, CharSequence thenExpr, CharSequence elseExpr) {
        return new StringBuilder("(")
                .append(cond)
                .append(" ? ")
                .append(thenExpr)
                .append(" : ")
                .append(elseExpr)
                .append(")");
    }

    public static StringBuilder makeCall(String name, List<? extends CharSequence> args) {
        return new StringBuilder(name)
                .append(" (")
                .append(args.stream().collect(Collectors.joining(", ")))
                .append(")");
    }

    public static StringBuilder makeView(String type, CharSequence arg) {
        return makeCall(type.concat("::view::create"), List.of(arg));
    }

    public static StringBuilder makeNew(String name, List<? extends CharSequence> args) {
        return new StringBuilder("new ").append(makeCall(name, args));
    }

    public static StringBuilder deref(CharSequence ptrValue) {
        return new StringBuilder("* ").append(ptrValue);
    }
}
