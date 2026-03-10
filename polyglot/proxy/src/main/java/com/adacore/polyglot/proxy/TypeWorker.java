//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.NativeType.NativeTypeDecl;

public interface TypeWorker<T> {
    ProxyContext getContext();

    T numberType(TypeExpr type);

    T charType(TypeExpr type);

    T boolType(TypeExpr type);

    T enumType(TypeExpr type);

    T stringType(TypeExpr type);

    T arrayType(TypeExpr type);

    T classType(TypeExpr type);

    T pointerType(TypeExpr type);

    T voidType(TypeExpr type);

    T refType(TypeExpr type);

    default T apply(TypeExpr type) {
        ProxyContext context = getContext();
        if (type.isReference()) return refType(type);
        if (type.isArray()) return arrayType(type);
        if (type.isPointer()) return pointerType(type);
        if (context.isStringType(type)) return stringType(type);
        if (type.isName()) {
            TypeDecl typeDecl = context.getTypeDecl(type.getName());
            if (typeDecl instanceof ClassDecl) return classType(type);
            if (typeDecl instanceof EnumerationDecl) return enumType(type);
            if (typeDecl instanceof NativeTypeDecl nativeType) {
                if (nativeType.nativeType == NativeType.BOOL) return boolType(type);
                else if (nativeType.nativeType == NativeType.CHAR) return charType(type);
                else if (nativeType.nativeType == NativeType.VOID) return voidType(type);
                else return numberType(type);
            }
            throw new RuntimeException(
                    "Unreachable: missing case for type %s"
                            .formatted(
                                    type.getName()
                                            .join(
                                                    (name) -> name.getLastName().toLower(),
                                                    "",
                                                    "::",
                                                    "")));
        }
        throw new RuntimeException("Unreachable: missing case for type %s".formatted(type));
    }

    /**
     * Partial implementation of the TypeWorker interface that considers a ReferenceTypeExpr was
     * traversed. In essence, some types are no longer reachable.
     */
    public interface SubreferenceTypeWorker<T> extends TypeWorker<T> {
        @Override
        default T refType(TypeExpr type) {
            throw new IllegalArgumentException("Unreachable: reference-to-reference type");
        }

        @Override
        default T voidType(TypeExpr type) {
            throw new IllegalArgumentException("Unreachable: void reference type");
        }
    }

    /**
     * Partial implementation of the TypeWorker interface that returns a default case for all types
     * except PointerTypeExpr and ReferenceTypeExpr that reference a pointer.
     */
    public interface PointerTypeWorker<T> extends TypeWorker<T> {

        T defaultCase();

        @Override
        default T numberType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T charType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T boolType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T enumType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T stringType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T arrayType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T classType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T voidType(TypeExpr type) {
            return defaultCase();
        }

        @Override
        default T apply(TypeExpr type) {
            if (type.isReference() && !type.referencedType().isPointer()) return defaultCase();
            return TypeWorker.super.apply(type);
        }
    }
}
