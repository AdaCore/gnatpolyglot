//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.codegen;

import com.adacore.gnatpolyglot.ada2proxy.AdaTypeMatcher;
import com.adacore.libadalang.Libadalang;

public interface TypeWorker<T> {
    T intType(Libadalang.BaseTypeDecl type);

    T boolType(Libadalang.BaseTypeDecl type);

    T characterType(Libadalang.BaseTypeDecl type);

    T enumType(Libadalang.BaseTypeDecl type);

    T stringType(Libadalang.BaseTypeDecl type);

    T recordType(Libadalang.BaseTypeDecl type);

    T taggedType(Libadalang.BaseTypeDecl type);

    T privateType(Libadalang.BaseTypeDecl type);

    T arrayType(Libadalang.BaseTypeDecl type);

    T arrayAccessType(Libadalang.BaseTypeDecl type);

    T accessType(Libadalang.BaseTypeDecl type);

    default T subpAccessType(Libadalang.BaseTypeDecl type) {
        return null;
    }

    default T apply(Libadalang.BaseTypeDecl type) {
        if (AdaTypeMatcher.isNumber(type)) return intType(type);
        if (type.equals(type.pBoolType())) return boolType(type);
        if (AdaTypeMatcher.isCharacter(type)) return characterType(type);
        if (AdaTypeMatcher.isEnum(type)) return enumType(type);
        if (AdaTypeMatcher.isStringType(type)) return stringType(type);
        if (type.pIsTaggedType(type)) return taggedType(type);
        if (AdaTypeMatcher.isPrivate(type)) return privateType(type);
        if (type.pIsRecordType(type)) return recordType(type);
        if (type.pIsArrayType(type)) return arrayType(type);
        if (AdaTypeMatcher.isAccessToSubp(type)) return subpAccessType(type);
        if (AdaTypeMatcher.isArrayAccess(type)) return arrayAccessType(type);
        if (type.pIsAccessType(type)) return accessType(type);
        throw new RuntimeException("Unreachable: missing case for type %s".formatted(type));
    }
}
