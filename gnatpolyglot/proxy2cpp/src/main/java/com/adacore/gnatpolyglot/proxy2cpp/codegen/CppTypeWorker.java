//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;

/**
 * Partial implementation of the TypeWorker interface that considers all C++ integer-like types are
 * bound similarly, and string are bound similarly to arrays.
 */
public interface CppTypeWorker<T> extends TypeWorker<T> {

    default T charType(TypeExpr type) {
        return numberType(type);
    }

    default T boolType(TypeExpr type) {
        return numberType(type);
    }

    default T enumType(TypeExpr type) {
        return numberType(type);
    }

    default T stringType(TypeExpr type) {
        return arrayType(type);
    }

    /**
     * Partial implementation of the CppTypeWorker interface that considers a ReferenceTypeExpr was
     * traversed. In essence, some types are no longer reachable.
     */
    public interface SubreferenceTypeWorker<T>
            extends CppTypeWorker<T>, TypeWorker.SubreferenceTypeWorker<T> {}
}
