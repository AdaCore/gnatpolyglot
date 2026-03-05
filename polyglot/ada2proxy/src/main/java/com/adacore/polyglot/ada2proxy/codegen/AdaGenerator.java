//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.codegen;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaTypeMatcher;
import java.util.List;
import java.util.stream.Collectors;

public class AdaGenerator {
    /** Create an Ada cast expression to converter value to the given type. */
    public static StringBuilder makeCast(Libadalang.BaseTypeDecl type, CharSequence value) {
        return new StringBuilder(type.pFullyQualifiedName()).append("(").append(value).append(")");
    }

    /** Create a call to a function. */
    public static StringBuilder makeCall(String name, List<? extends CharSequence> args) {
        StringBuilder builder = new StringBuilder(name);
        if (!args.isEmpty()) {
            builder.append(" (")
                    .append(args.stream().collect(Collectors.joining(", ")))
                    .append(")");
        }
        return builder;
    }

    /** Create a conditionnal expression. */
    public static StringBuilder makeConditionalExpr(
            CharSequence cond, CharSequence trueExpr, CharSequence falseExpr) {
        return new StringBuilder("(if ")
                .append(cond)
                .append(" then ")
                .append(trueExpr)
                .append(" else ")
                .append(falseExpr)
                .append(")");
    }

    /** Create an Ada.Unchecked_Conversion function that converts a value of type `from` to `to`. */
    public static StringBuilder uncheckedConverter(
            String name, CharSequence from, CharSequence to) {
        return new StringBuilder("function ")
                .append(name)
                .append(" is new Ada.Unchecked_Conversion (")
                .append(from)
                .append(", ")
                .append(to)
                .append(")");
    }

    /**
     * Create an array bound (lower or upper) from a Polyglot_(Array|String). If the array is empty,
     * the bound the lower bound will be `type'First`, and the upper bound will be `type'First + 1`
     */
    public static StringBuilder createBoundCast(
            String data, Libadalang.BaseTypeDecl type, boolean isLowerBound) {
        Libadalang.BaseTypeDecl indexType = type.pIndexType(0, type);
        String indexTypeName = indexType.pFullyQualifiedName();
        String lengthFunc =
                "Polyglot.Ada.%s.Length"
                        .formatted(AdaTypeMatcher.isStringType(type) ? "Strings" : "Arrays");
        String dataBound;
        String emptyBound = "%s'First".formatted(indexTypeName);
        if (isLowerBound) {
            dataBound = data.concat(".First");
            emptyBound = emptyBound.concat(" + 1");
        } else {
            dataBound = data.concat(".Last");
        }
        return makeConditionalExpr(
                makeCall(lengthFunc, List.of(data)).append(" /= 0"),
                makeCast(indexType, dataBound),
                emptyBound);
    }

    /** Create an Ada cast expression to converter value to the given type. */
    public static StringBuilder makeImport(String entityName) {
        return new StringBuilder("pragma Import(Ada,").append(entityName).append(")");
    }

    /** Create an Ada `new` expression that copies value to the heap */
    public static StringBuilder makeHeapCopy(Libadalang.BaseTypeDecl type, String value) {
        return new StringBuilder("new ")
                .append(type.pFullyQualifiedName())
                .append("'(")
                .append(value)
                .append(")");
    }

    /** Create a Polyglot_Array aggregate from an Ada array. */
    public static StringBuilder makePolyglotArray(String array, boolean isAccess) {
        if (isAccess) array = array.concat(".all");
        return new StringBuilder("(First => ")
                .append(makeCall("Interfaces.C.int", List.of(array.concat("'First"))))
                .append(", Last => ")
                .append(makeCall("Interfaces.C.int", List.of(array.concat("'Last"))))
                .append(", Data => ")
                .append(array)
                .append("'Address)");
    }
}
