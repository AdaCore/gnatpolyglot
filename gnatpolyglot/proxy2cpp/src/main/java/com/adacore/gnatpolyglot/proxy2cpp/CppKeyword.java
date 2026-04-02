//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp;

import com.adacore.gnatpolyglot.proxy.Name;

public enum CppKeyword {
    ALIGNAS(11),
    ALIGNOF(11),
    AND,
    AND_EQ,
    ASM,
    ASSERT,
    AUTO,
    BITAND,
    BITOR,
    BOOL,
    BREAK,
    CASE,
    CATCH,
    CHAR,
    CHAR8_T(20),
    CHAR16_T(11),
    CHAR32_T(11),
    CLASS,
    COMPL,
    CONCEPT(20),
    CONST,
    CONSTEVAL(20),
    CONSTEXPR(11),
    CONSTINIT(20),
    CONST_CAST,
    CONTINUE,
    CONTRACT_ASSERT(26),
    CO_AWAIT(20),
    CO_RETURN(20),
    CO_YIELD(20),
    DECLTYPE(11),
    DEFAULT,
    DELETE,
    DO,
    DOUBLE,
    DYNAMIC_CAST,
    ELSE,
    ENUM,
    ERRNO,
    EXPLICIT,
    EXPORT,
    EXTERN,
    FALSE,
    FLOAT,
    FOR,
    FRIEND,
    GOTO,
    IF,
    INLINE,
    INT,
    LINUX,
    LONG,
    MUTABLE,
    NAMESPACE,
    NEW,
    NOEXCEPT(11),
    NOT,
    NOT_EQ,
    NULLPTR(11),
    OPERATOR,
    OR,
    OR_EQ,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    REGISTER,
    REINTERPRET_CAST,
    REQUIRES(20),
    RETURN,
    SHORT,
    SIGNED,
    SIZEOF,
    STATIC,
    STATIC_ASSERT(11),
    STATIC_CAST,
    STRUCT,
    SWITCH,
    TEMPLATE,
    THIS,
    THREAD_LOCAL(11),
    THROW,
    TRUE,
    TRY,
    TYPEDEF,
    TYPEID,
    TYPENAME,
    UNION,
    UNIX,
    UNSIGNED,
    USING,
    VIRTUAL,
    VOID,
    VOLATILE,
    WCHAR_T,
    WHILE,
    XOR,
    XOR_EQ;

    int standard;

    CppKeyword() {
        this.standard = 98;
    }

    CppKeyword(int standard) {
        this.standard = standard;
    }

    public static Boolean isKeyword(Name name) {
        try {
            CppKeyword.valueOf(name.toUpper());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
