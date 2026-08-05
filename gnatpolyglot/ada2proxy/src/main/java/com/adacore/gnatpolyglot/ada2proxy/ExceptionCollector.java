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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExceptionCollector {
    private static class Visitor implements AdaProxyVisitor<Void> {

        public List<AdaException> exceptions = new ArrayList<>();
        public Set<Package> packages = new HashSet<>();

        Package currentPackage = null;

        @Override
        public Void visit(AdaProxy proxy) {
            proxy.packages.forEach(p -> p.accept(this));
            return null;
        }

        @Override
        public Void visit(Package pack) {
            currentPackage = pack;
            pack.declarations.forEach(d -> d.accept(this));
            currentPackage = null;
            return null;
        }

        @Override
        public Void visit(AdaException adaException) {
            exceptions.add(adaException);
            packages.add(currentPackage);
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
        public Void visit(Array array) {
            return null;
        }

        @Override
        public Void visit(SubpParam subpParam) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(EnumLiteral enumLiteral) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Component component) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public Void visit(Subtype subtype) {
            return null;
        }

        @Override
        public Void visit(Callback Callback) {
            return null;
        }
    }

    /**
     * Return the list of package that needs to have a with-clause in the generated binding sources
     * to have all the exceptions.
     */
    public static List<Package> getPackagesToInclude(AdaProxy proxy) {
        Visitor visitor = new Visitor();
        visitor.visit(proxy);
        return visitor.packages.stream().toList();
    }

    /** Return all the exceptions in the proxy, sorted in ascending order of their enum value. */
    public static List<AdaException> getExceptions(AdaProxy proxy) {
        Visitor visitor = new Visitor();
        visitor.visit(proxy);
        visitor.exceptions.sort(Comparator.comparingInt(AdaException::getValue));
        return visitor.exceptions;
    }
}
