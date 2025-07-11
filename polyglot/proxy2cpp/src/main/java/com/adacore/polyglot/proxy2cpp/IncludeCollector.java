package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.proxy.ArrayTypeExpr;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.EnumItem;
import com.adacore.polyglot.proxy.EnumerationDecl;
import com.adacore.polyglot.proxy.ExceptionDecl;
import com.adacore.polyglot.proxy.Field;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.NameTypeExpr;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.PointerTypeExpr;
import com.adacore.polyglot.proxy.Proxy;
import com.adacore.polyglot.proxy.ProxyVisitor;
import com.adacore.polyglot.proxy.ReferenceTypeExpr;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.VTableEntry;
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
            functionDecl.type.parameters.forEach(p -> p.type.visit(this));
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
        public Void visit(EnumerationDecl enumerationDecl) {
            throw new UnsupportedOperationException("Unreachable");
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

        public Void visit(ExceptionDecl exceptionDecl) {
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
