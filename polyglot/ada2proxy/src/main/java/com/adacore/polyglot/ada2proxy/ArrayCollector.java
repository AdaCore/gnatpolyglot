package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.proxy.AdaException;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxyVisitor;
import com.adacore.polyglot.ada2proxy.proxy.Array;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.EnumLiteral;
import com.adacore.polyglot.ada2proxy.proxy.EnumType;
import com.adacore.polyglot.ada2proxy.proxy.GlobalVariable;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import java.util.ArrayList;
import java.util.List;

public class ArrayCollector {
    private static class Visitor implements AdaProxyVisitor<Void> {

        public List<Array> arrays = new ArrayList<>();

        @Override
        public Void visit(AdaProxy proxy) {
            proxy.packages.forEach(p -> p.accept(this));
            return null;
        }

        @Override
        public Void visit(Package pack) {
            pack.declarations.forEach(d -> d.accept(this));
            return null;
        }

        @Override
        public Void visit(Array array) {
            arrays.add(array);
            return null;
        }

        @Override
        public Void visit(AdaException adaException) {
            return null;
        }

        @Override
        public Void visit(Subprogram subprogram) {
            return null;
        }

        @Override
        public Void visit(EnumType enumType) {
            return null;
        }

        @Override
        public Void visit(Record rec) {
            return null;
        }

        @Override
        public Void visit(GlobalVariable globalVariable) {
            return null;
        }

        @Override
        public Void visit(SubpParam subpParam) {
            return null;
        }

        @Override
        public Void visit(EnumLiteral enumLiteral) {
            return null;
        }

        @Override
        public Void visit(Component component) {
            return null;
        }
    }

    /** Return all the exceptions in the proxy, sorted in ascending order of their enum value. */
    public static List<Array> getArrays(AdaProxy proxy) {
        Visitor visitor = new Visitor();
        visitor.visit(proxy);
        return visitor.arrays;
    }

    public static List<Libadalang.BaseTypeDecl> getAllComponentTypes(AdaProxy proxy) {
        return ArrayCollector.getArrays(proxy).stream()
                .map(Array::getComponentType)
                .distinct()
                .toList();
    }
}
