//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust.codegen;

import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2rust.RustAPI;

/** Wraps raw FFI call expressions into safe Rust return values. */
public class ReturnConverter {

    private final RustAPI api;

    public ReturnConverter(RustAPI api) {
        this.api = api;
    }

    /**
     * Return the safe return expression wrapping {@code rawCall} according to the return type and
     * ownership.
     */
    public String wrapReturn(String rawCall, TypeExpr returnType, Owner owner) {
        return new WrapReturnWorker(rawCall, owner).apply(returnType);
    }

    private class WrapReturnWorker implements RustTypeWorker<String> {

        private final String rawCall;
        private final Owner owner;

        WrapReturnWorker(String rawCall, Owner owner) {
            this.rawCall = rawCall;
            this.owner = owner;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return rawCall;
        }

        @Override
        public String voidType(TypeExpr type) {
            return rawCall;
        }

        @Override
        public String enumType(TypeExpr type) {
            var enumDecl = (EnumerationDecl) api.getContext().getTypeDecl(type.getName());
            var enumName = api.safeTypename(type);
            // The C ABI returns enums by value as i32; narrow to the enum's repr width before
            // reinterpreting, since the enum is not necessarily laid out as i32.
            var repr = api.rustEnumRepr(enumDecl);
            return RustGenerator.transmute(repr, enumName, RustGenerator.cast(rawCall, repr));
        }

        @Override
        public String classType(TypeExpr type) {
            var name = api.safeTypename(type);
            var owned = RustGenerator.wrapPointer(name, rawCall);
            return owner == Owner.STATIC ? RustGenerator.manuallyDropNew(owned) : owned;
        }

        /**
         * Take ownership of a returned {@code array_data} (string or array) as {@code
         * ownedTypePath} via {@code from_raw}. The path is the bare owning wrapper without generics
         * ({@code PolyglotArray}'s element type is inferred from the wrapper's declared return
         * type). When the library keeps ownership ({@code STATIC}) the value is wrapped so it is
         * never freed.
         */
        private String wrapArrayData(String ownedTypePath) {
            var owned = ownedTypePath + "::from_raw(" + rawCall + ")";
            return owner == Owner.STATIC ? RustGenerator.manuallyDropNew(owned) : owned;
        }

        @Override
        public String stringType(TypeExpr type) {
            return wrapArrayData(RustGenerator.runtimePath("ada::strings::PolyglotString"));
        }

        @Override
        public String arrayType(TypeExpr type) {
            return wrapArrayData(RustGenerator.runtimePath("ada::arrays::PolyglotArray"));
        }

        @Override
        public String pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Raw pointer return type not yet supported");
        }

        @Override
        public String refType(TypeExpr type) {
            return new RustTypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                /** A scalar is dereferenced to a copy (idiomatic for a {@code Copy} type). */
                @Override
                public String numberType(TypeExpr type) {
                    return RustGenerator.deref(rawCall);
                }

                /** An enum is read at its native repr width and reinterpreted to a copy. */
                @Override
                public String enumType(TypeExpr type) {
                    var enumDecl = (EnumerationDecl) api.getContext().getTypeDecl(type.getName());
                    var enumName = api.safeTypename(type);
                    // The reference points at a native-width enum object, not an i32: read it
                    // through a pointer of the enum's repr width to avoid an out-of-bounds read.
                    var repr = api.rustEnumRepr(enumDecl);
                    return RustGenerator.transmute(
                            repr,
                            enumName,
                            RustGenerator.deref(
                                    RustGenerator.cast(
                                            "(" + rawCall + ")",
                                            RustGenerator.rawPtr(true, repr))));
                }

                @Override
                public String classType(TypeExpr type) {
                    var name = api.safeTypename(type);
                    return RustGenerator.manuallyDropNew(RustGenerator.wrapPointer(name, rawCall));
                }

                @Override
                public String pointerType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "Pointer inside reference not supported");
                }
            }.apply(type.referencedType());
        }
    }

    /**
     * Return the body of a {@code _mut} accessor: a real {@code &mut} reference into a scalar or
     * enum field, for in-place mutation. Unlike {@link SubreferenceWrapReturnWorker} (which copies
     * out), this borrows the storage. {@code referencedType} is the scalar/enum the field holds.
     */
    public String mutAccessorExpr(String rawCall, TypeExpr referencedType) {
        return new RustTypeWorker.SubreferenceTypeWorker<String>() {
            @Override
            public ProxyContext getContext() {
                return api.getContext();
            }

            /** The FFI pointer is already {@code *mut <scalar>}, so borrow the deref directly. */
            @Override
            public String numberType(TypeExpr type) {
                return RustGenerator.reference(false, RustGenerator.deref(rawCall));
            }

            /** The enum FFI pointer is an opaque {@code *mut c_void}; cast it to the enum first. */
            @Override
            public String enumType(TypeExpr type) {
                var enumName = api.safeTypename(type);
                return RustGenerator.reference(
                        false,
                        RustGenerator.deref(
                                RustGenerator.cast(
                                        rawCall, RustGenerator.rawPtr(false, enumName))));
            }

            @Override
            public String classType(TypeExpr type) {
                throw new UnsupportedOperationException(
                        "Classes use a ManuallyDrop view, not a _mut accessor");
            }

            @Override
            public String pointerType(TypeExpr type) {
                throw new UnsupportedOperationException("Pointer inside reference not supported");
            }
        }.apply(referencedType);
    }
}
