//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust.codegen;

import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2rust.RustAPI;

/** Generates FFI call argument expressions for converting safe Rust values to raw C types. */
public class ParameterConverter {

    private final RustAPI api;

    public ParameterConverter(RustAPI api) {
        this.api = api;
    }

    /** Return the FFI call argument expression for the given parameter. */
    public String callArg(Parameter p) {
        return new CallArgWorker(p.name.toLower()).apply(p.type);
    }

    private class CallArgWorker implements RustTypeWorker<String> {

        private final String name;

        CallArgWorker(String name) {
            this.name = name;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return name;
        }

        @Override
        public String enumType(TypeExpr type) {
            return RustGenerator.cast(name, "i32");
        }

        @Override
        public String classType(TypeExpr type) {
            return RustGenerator.asPtr(name);
        }

        @Override
        public String voidType(TypeExpr type) {
            throw new UnsupportedOperationException("Cannot pass void as call argument");
        }

        // Strings and arrays only ever reach a parameter as a reference, so they are handled by
        // the reference worker in refType below, not directly here.

        @Override
        public String pointerType(TypeExpr type) {
            return RustGenerator.cast(RustGenerator.asPtr(name), RustGenerator.cVoidPtr(false));
        }

        @Override
        public String refType(TypeExpr refType) {
            return new RustTypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                @Override
                public String numberType(TypeExpr type) {
                    return RustGenerator.cast(name, refType.isConst() ? "*const _" : "*mut _");
                }

                @Override
                public String enumType(TypeExpr type) {
                    var enumName = api.safeTypename(type);
                    return RustGenerator.cast(
                            RustGenerator.cast(name, "*mut " + enumName),
                            RustGenerator.cVoidPtr(false));
                }

                @Override
                public String classType(TypeExpr type) {
                    return RustGenerator.cast(
                            RustGenerator.asPtr(name), RustGenerator.cVoidPtr(refType.isConst()));
                }

                /**
                 * A {@code &PolyglotStr} / {@code &PolyglotArray<E>} parameter yields its raw,
                 * non-owning {@code array_data} (stringType delegates to arrayType via the worker
                 * default).
                 */
                @Override
                public String arrayType(TypeExpr type) {
                    return name + ".raw()";
                }

                @Override
                public String pointerType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "Pointer inside reference not supported");
                }
            }.apply(refType.referencedType());
        }
    }
}
