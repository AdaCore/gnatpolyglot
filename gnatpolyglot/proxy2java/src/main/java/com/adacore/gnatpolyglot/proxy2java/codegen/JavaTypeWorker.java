//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.ExceptionDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;

public interface JavaTypeWorker<T> extends TypeWorker<T> {

    @Override
    default T apply(TypeExpr type) {
        // In Java, exceptions are classes, and their usage in the JNI layer especially requires
        // similar handling.
        if (type.isName() && getContext().getTypeDecl(type.getName()) instanceof ExceptionDecl)
            return classType(type);
        // Unwrap const references to pointers: it should behave the same in Java.
        if (type.isReference() && type.isConst() && type.referencedType().isPointer())
            return pointerType(type.referencedType());
        return TypeWorker.super.apply(type);
    }

    @Override
    default T charType(TypeExpr type) {
        return numberType(type);
    }

    @Override
    default T stringType(TypeExpr type) {
        return arrayType(type);
    }

    public interface JavaSubreferenceTypeWorker<T> extends TypeWorker.SubreferenceTypeWorker<T> {

        @Override
        default T charType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        default T stringType(TypeExpr type) {
            return arrayType(type);
        }
    }
}
