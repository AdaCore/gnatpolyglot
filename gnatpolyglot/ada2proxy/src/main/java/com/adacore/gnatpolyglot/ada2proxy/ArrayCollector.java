//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy;

import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaException;
import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxyVisitor;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Array;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Callback;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Component;
import com.adacore.gnatpolyglot.ada2proxy.proxy.EnumLiteral;
import com.adacore.gnatpolyglot.ada2proxy.proxy.EnumType;
import com.adacore.gnatpolyglot.ada2proxy.proxy.GlobalVariable;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Package;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Record;
import com.adacore.gnatpolyglot.ada2proxy.proxy.SubpParam;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Subtype;
import com.adacore.libadalang.Libadalang;
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

        @Override
        public Void visit(Subtype subtype) {
            return null;
        }

        @Override
        public Void visit(Callback callback) {
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
                .map(t -> t.pBaseSubtype(t))
                .distinct()
                .toList();
    }
}
