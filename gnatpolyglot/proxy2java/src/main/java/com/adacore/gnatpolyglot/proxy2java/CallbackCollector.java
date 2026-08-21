package com.adacore.gnatpolyglot.proxy2java;

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

public class CallbackCollector {

    static class Visitor implements ProxyVisitor<Void> {

        public HashSet<FunctionTypeExpr> callbacks = new HashSet<>();

        @Override
        public Void visit(Proxy proxy) {
            proxy.modules.forEach(m -> m.visit(this));
            return null;
        }

        @Override
        public Void visit(Module module) {
            module.declarations.forEach(d -> d.visit(this));
            return null;
        }

        @Override
        public Void visit(FunctionDecl functionDecl) {
            // Do not enter the functionType directly.
            functionDecl.type.parameters.forEach(p -> p.visit(this));
            functionDecl.type.returnType.visit(this);
            return null;
        }

        @Override
        public Void visit(Parameter parameter) {
            parameter.type.visit(this);
            return null;
        }

        @Override
        public Void visit(FunctionTypeExpr functionTypeExpr) {
            this.callbacks.add(functionTypeExpr);
            functionTypeExpr.parameters.forEach(p -> p.visit(this));
            functionTypeExpr.returnType.visit(this);
            return null;
        }

        @Override
        public Void visit(ReferenceTypeExpr referenceTypeExpr) {
            referenceTypeExpr.typeExpr.visit(this);
            return null;
        }

        @Override
        public Void visit(ClassDecl classDecl) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(EnumerationDecl enumerationDecl) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(Role role) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(Field field) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(EnumItem enumItem) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(Transfer transfer) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(FullyQualifiedName name) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(ArrayTypeExpr arrayTypeExpr) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(NameTypeExpr nameTypeExpr) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(PointerTypeExpr pointerTypeExpr) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(VTableEntry vTableEntry) {
            // Nothing to do.
            return null;
        }

        @Override
        public Void visit(ExceptionDecl exceptionDecl) {
            // Nothing to do.
            return null;
        }
    }

    public static List<FunctionTypeExpr> getCallbacks(Proxy proxy) {
        Visitor v = new Visitor();
        v.visit(proxy);
        return v.callbacks.stream().toList();
    }
}
