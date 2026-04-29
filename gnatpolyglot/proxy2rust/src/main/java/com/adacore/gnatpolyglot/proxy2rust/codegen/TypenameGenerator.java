//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust.codegen;

import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2rust.RustAPI;

/** Generates Rust type names for the safe public API and the unsafe FFI declarations. */
public class TypenameGenerator {

    private final RustAPI api;

    public TypenameGenerator(RustAPI api) {
        this.api = api;
    }

    // ===== Public entry points =====

    /** Safe-API type name for a parameter (class types passed by reference as &Class). */
    public String safeTypename(TypeExpr typeExpr) {
        return new SafeTypenameWorker().apply(typeExpr);
    }

    /** Safe-API return type name, considering ownership semantics. */
    public String safeReturnTypename(TypeExpr typeExpr, Owner owner) {
        return new SafeReturnTypenameWorker(owner).apply(typeExpr);
    }

    /** FFI declaration type name for a TypeExpr. */
    public String ffiTypename(TypeExpr typeExpr) {
        return new FfiTypenameWorker().apply(typeExpr);
    }

    // ===== Shared helpers =====

    private String nativeTypename(TypeExpr type) {
        return api.rustNativeTypename(
                ((NativeTypeDecl) api.getContext().getTypeDecl(type.getName())).nativeType);
    }

    // A class or enum is named by its fully qualified Rust type path, so the reference is valid
    // whichever module it appears in (see RustAPI.rustTypePath).

    private String userTypename(TypeDecl type) {
        return api.rustTypePath(type.name);
    }

    // ===== Worker: inner type of a reference (no & prefix) =====

    private class SubreferenceTypenameWorker
            implements RustTypeWorker.SubreferenceTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return nativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return userTypename((EnumerationDecl) api.getContext().getTypeDecl(type.getName()));
        }

        @Override
        public String classType(TypeExpr type) {
            return userTypename((ClassDecl) api.getContext().getTypeDecl(type.getName()));
        }

        @Override
        public String pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Pointer inside reference not supported");
        }
    }

    // ===== Worker: safe API parameter types (class passed as &Class) =====

    private class SafeTypenameWorker implements RustTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return nativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return userTypename((EnumerationDecl) api.getContext().getTypeDecl(type.getName()));
        }

        @Override
        public String classType(TypeExpr type) {
            return userTypename((ClassDecl) api.getContext().getTypeDecl(type.getName()));
        }

        @Override
        public String voidType(TypeExpr type) {
            return "()";
        }

        @Override
        public String pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Raw pointer in safe param type position");
        }

        @Override
        public String refType(TypeExpr type) {
            return RustGenerator.reference(
                    type.isConst(), new SubreferenceTypenameWorker().apply(type.referencedType()));
        }
    }

    // ===== Worker: safe API return types (ownership-aware) =====

    private class SafeReturnTypenameWorker implements RustTypeWorker<String> {

        private final Owner owner;

        SafeReturnTypenameWorker(Owner owner) {
            this.owner = owner;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return nativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return userTypename((EnumerationDecl) api.getContext().getTypeDecl(type.getName()));
        }

        @Override
        public String classType(TypeExpr type) {
            var name = userTypename((ClassDecl) api.getContext().getTypeDecl(type.getName()));
            return owner == Owner.STATIC ? RustGenerator.manuallyDropType(name) : name;
        }

        @Override
        public String voidType(TypeExpr type) {
            return "()";
        }

        @Override
        public String pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Raw pointer in safe return type position");
        }

        @Override
        public String refType(TypeExpr type) {
            return new SubreferenceTypenameWorker() {

                /**
                 * Scalars and enums are returned by copy (inherited from the base worker). A class
                 * is kept alive as a {@code ManuallyDrop} view rather than a borrow.
                 */
                @Override
                public String classType(TypeExpr type) {
                    var name =
                            userTypename((ClassDecl) api.getContext().getTypeDecl(type.getName()));
                    return RustGenerator.manuallyDropType(name);
                }
            }.apply(type.referencedType());
        }
    }

    // ===== Worker: FFI declaration type names =====

    private class FfiTypenameWorker implements RustTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return nativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return "i32";
        }

        @Override
        public String classType(TypeExpr type) {
            return RustGenerator.cVoidPtr(false);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "()";
        }

        @Override
        public String pointerType(TypeExpr type) {
            return RustGenerator.cVoidPtr(false);
        }

        @Override
        public String refType(TypeExpr refType) {
            return new RustTypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                private String pointerPrefix() {
                    return refType.isConst() ? "*const " : "*mut ";
                }

                @Override
                public String numberType(TypeExpr type) {
                    return pointerPrefix() + nativeTypename(type);
                }

                @Override
                public String enumType(TypeExpr type) {
                    return RustGenerator.cVoidPtr(false);
                }

                @Override
                public String classType(TypeExpr type) {
                    return RustGenerator.cVoidPtr(refType.isConst());
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return pointerPrefix() + RustGenerator.cVoidPtr(false);
                }
            }.apply(refType.referencedType());
        }
    }
}
