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

    /** The safe element type name of an array, e.g. {@code i32} or {@code crate::pkg::MyInt}. */
    private String elementTypename(TypeExpr element) {
        var decl = api.getContext().getTypeDecl(element.getName());
        if (decl instanceof ClassDecl classDecl) {
            return userTypename(classDecl);
        }
        return nativeTypename(element);
    }

    /** The safe array type name for an array TypeExpr, e.g. {@code PolyglotArray<i32>}. */
    private String arrayTypename(TypeExpr arrayType) {
        return RustGenerator.runtimePath("ada::arrays::PolyglotArray")
                + "<"
                + elementTypename(arrayType.elementType())
                + ">";
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

        /**
         * A reference to a string is borrowed as {@code PolyglotStr} (the enclosing {@code refType}
         * adds the {@code &}), like Rust's {@code &str}.
         */
        @Override
        public String stringType(TypeExpr type) {
            return RustGenerator.runtimePath("ada::strings::PolyglotStr");
        }

        /**
         * A reference to an array is borrowed as {@code PolyglotArray<E>} (the enclosing {@code
         * refType} adds the {@code &} / {@code &mut}).
         */
        @Override
        public String arrayType(TypeExpr type) {
            return arrayTypename(type);
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

        // Strings and arrays only ever reach a parameter as a reference, so they are
        // handled by the SubreferenceTypenameWorker via refType below, not directly here.

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

        /**
         * A returned string is an owned {@code PolyglotString} (freed on drop), unless the library
         * keeps ownership ({@code STATIC}), in which case it is a non-owning {@code ManuallyDrop}
         * view.
         */
        @Override
        public String stringType(TypeExpr type) {
            var name = RustGenerator.runtimePath("ada::strings::PolyglotString");
            return owner == Owner.STATIC ? RustGenerator.manuallyDropType(name) : name;
        }

        /**
         * A returned array is an owned {@code PolyglotArray<E>} (freed on drop), unless the library
         * keeps ownership ({@code STATIC}), in which case it is a non-owning {@code ManuallyDrop}
         * view.
         */
        @Override
        public String arrayType(TypeExpr type) {
            var name = arrayTypename(type);
            return owner == Owner.STATIC ? RustGenerator.manuallyDropType(name) : name;
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

        /**
         * A string or array crosses the FFI boundary by value as the raw {@code array_data}
         * descriptor (stringType delegates to arrayType via the RustTypeWorker default).
         */
        @Override
        public String arrayType(TypeExpr type) {
            return RustGenerator.runtimePath("ada::arrays::ArrayData");
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

                /**
                 * A string or array passes by value as the {@code array_data} descriptor, not as a
                 * pointer to it, so the reference wrapper is absorbed here (no pointer prefix).
                 * stringType delegates to arrayType via the RustTypeWorker default.
                 */
                @Override
                public String arrayType(TypeExpr type) {
                    return RustGenerator.runtimePath("ada::arrays::ArrayData");
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return pointerPrefix() + RustGenerator.cVoidPtr(false);
                }
            }.apply(refType.referencedType());
        }
    }
}
