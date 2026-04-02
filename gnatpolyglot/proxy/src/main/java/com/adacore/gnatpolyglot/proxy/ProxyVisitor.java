//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

/** Interface to visit the Proxy. */
public interface ProxyVisitor<T> {
    public abstract T visit(Proxy proxy);

    public abstract T visit(Module module);

    public abstract T visit(FunctionDecl functionDecl);

    public abstract T visit(ClassDecl classDecl);

    public abstract T visit(EnumerationDecl enumerationDecl);

    public abstract T visit(Role role);

    public abstract T visit(Field field);

    public abstract T visit(EnumItem enumItem);

    public abstract T visit(Transfer transfer);

    public abstract T visit(Parameter parameter);

    public abstract T visit(FullyQualifiedName name);

    public abstract T visit(ArrayTypeExpr arrayTypeExpr);

    public abstract T visit(NameTypeExpr nameTypeExpr);

    public abstract T visit(ReferenceTypeExpr referenceTypeExpr);

    public abstract T visit(PointerTypeExpr pointerTypeExpr);

    public abstract T visit(FunctionTypeExpr functionTypeExpr);

    public abstract T visit(VTableEntry vTableEntry);

    public abstract T visit(ExceptionDecl exceptionDecl);
}
