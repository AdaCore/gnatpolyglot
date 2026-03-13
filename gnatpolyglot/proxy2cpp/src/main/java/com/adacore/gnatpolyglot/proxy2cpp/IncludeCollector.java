//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp;

import com.adacore.gnatpolyglot.proxy.ArrayTypeExpr;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.EnumItem;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.ExceptionDecl;
import com.adacore.gnatpolyglot.proxy.Field;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.NameTypeExpr;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.Proxy;
import com.adacore.gnatpolyglot.proxy.ProxyVisitor;
import com.adacore.gnatpolyglot.proxy.ReferenceTypeExpr;
import com.adacore.gnatpolyglot.proxy.Role;
import com.adacore.gnatpolyglot.proxy.Transfer;
import com.adacore.gnatpolyglot.proxy.VTableEntry;
import java.util.HashSet;
import java.util.List;

public class IncludeCollector {

    private static class Visitor implements ProxyVisitor<Void> {

        HashSet<FullyQualifiedName> includedNames = new HashSet<>();

        @Override
        public Void visit(Proxy proxy) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Module module) {
            module.declarations.forEach(d -> d.visit(this));
            includedNames.remove(module.name);
            return null;
        }

        @Override
        public Void visit(FunctionDecl functionDecl) {
            functionDecl.type.visit(this);
            return null;
        }

        @Override
        public Void visit(ClassDecl classDecl) {
            classDecl.fields.forEach(f -> f.type.visit(this));
            if (classDecl.vtable != null) classDecl.vtable.forEach(f -> f.functionType.visit(this));
            if (classDecl.parent != null) {
                FullyQualifiedName parentName = classDecl.parent.getParentFullyQualifiedName();
                if (parentName != null) includedNames.add(parentName);
            }
            return null;
        }

        @Override
        public Void visit(ArrayTypeExpr arrayTypeExpr) {
            arrayTypeExpr.typeExpr.visit(this);
            return null;
        }

        @Override
        public Void visit(NameTypeExpr nameTypeExpr) {
            FullyQualifiedName parentName = nameTypeExpr.name.getParentFullyQualifiedName();
            if (parentName != null) includedNames.add(parentName);
            return null;
        }

        @Override
        public Void visit(ReferenceTypeExpr referenceTypeExpr) {
            referenceTypeExpr.typeExpr.visit(this);
            return null;
        }

        @Override
        public Void visit(PointerTypeExpr pointerTypeExpr) {
            pointerTypeExpr.typeExpr.visit(this);
            return null;
        }

        @Override
        public Void visit(FunctionTypeExpr functionTypeExpr) {
            functionTypeExpr.parameters.forEach(f -> f.type.visit(this));
            functionTypeExpr.returnType.visit(this);
            return null;
        }

        @Override
        public Void visit(ExceptionDecl exceptionDecl) {
            // Nothing to do
            return null;
        }

        @Override
        public Void visit(EnumerationDecl enumerationDecl) {
            // Nothing to do
            return null;
        }

        @Override
        public Void visit(Role role) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Field field) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(EnumItem enumItem) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Transfer transfer) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Parameter parameter) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(FullyQualifiedName name) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(VTableEntry vTableEntry) {
            throw new UnsupportedOperationException("Unreachable");
        }
    }

    public static List<String> getIncludes(Module module) {
        Visitor visitor = new Visitor();
        visitor.visit(module);
        return visitor.includedNames.stream()
                .map(n -> n.join(r -> r.getLastName().toLower(), "", "_", ".h"))
                .toList();
    }
}
