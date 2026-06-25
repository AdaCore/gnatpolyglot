//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust.codegen;

import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;

/**
 * Partial implementation of the TypeWorker interface for Rust. In Rust, char and bool are distinct
 * primitive types but follow the same code-generation structure as numeric types, so they delegate
 * to numberType by default. A string is handled like an array of its characters by default (both
 * cross the FFI boundary as the same {@code array_data} descriptor); workers whose safe-API
 * representation differs for strings (PolyglotStr/PolyglotString vs PolyglotArray) override
 * stringType.
 */
public interface RustTypeWorker<T> extends TypeWorker<T> {

    @Override
    default T charType(TypeExpr type) {
        return numberType(type);
    }

    @Override
    default T boolType(TypeExpr type) {
        return numberType(type);
    }

    @Override
    default T stringType(TypeExpr type) {
        return arrayType(type);
    }

    @Override
    default T arrayType(TypeExpr type) {
        throw new UnsupportedOperationException("Array type not yet supported in proxy2rust");
    }

    /**
     * Partial implementation for use when a ReferenceTypeExpr has already been traversed, so
     * reference-to-reference and void-reference are unreachable.
     */
    interface SubreferenceTypeWorker<T>
            extends RustTypeWorker<T>, TypeWorker.SubreferenceTypeWorker<T> {}
}
