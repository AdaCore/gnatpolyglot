//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java;

import java.util.Set;

public class JavaReservedWords {
    public static Set<String> KEYWORDS =
            Set.of(
                    "abstract",
                    "assert",
                    "boolean",
                    "break",
                    "byte",
                    "case",
                    "catch",
                    "char",
                    "class",
                    "continue",
                    "const",
                    "default",
                    "do",
                    "double",
                    "else",
                    "enum",
                    "exports",
                    "extends",
                    "final",
                    "finally",
                    "float",
                    "for",
                    "goto",
                    "if",
                    "implements",
                    "import",
                    "instanceof",
                    "int",
                    "interface",
                    "long",
                    "module",
                    "native",
                    "new",
                    "package",
                    "private",
                    "protected",
                    "public",
                    "requires",
                    "return",
                    "short",
                    "static",
                    "strictfp",
                    "super",
                    "switch",
                    "synchronized",
                    "this",
                    "throw",
                    "throws",
                    "transient",
                    "try",
                    "var",
                    "void",
                    "volatile",
                    "while");

    public static Set<String> RESERVED_METHODS =
            Set.of(
                    "close",
                    "toString",
                    "hashCode",
                    "equals",
                    "clone",
                    "notify",
                    "notifyAll",
                    "wait",
                    "wait0",
                    "finalize");
}
