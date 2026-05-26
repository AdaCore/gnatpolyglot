//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy;

import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaException;
import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaProxyVisitor;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Array;
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
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.util.HashSet;
import java.util.List;

public class WithCollector {
    /**
     * Visitor that collects the necessary with-clauses to generate a proxy package.
     *
     * <p>Since with-clauses are not always transitive, and we may need to refer to generated
     * declarations, it may be necessary to add with clauses in the generated packages to make some
     * declarations visible.
     */
    private static class Visitor implements AdaProxyVisitor<Void> {

        public HashSet<String> units = new HashSet<>();

        AdaAPI api;

        public Visitor(AdaAPI api) {
            this.api = api;
        }

        /**
         * Return whether returning type in a binded subprogram uses the accessed type declared in
         * its corresponding proxy package.
         */
        private boolean returnTypeUsesAccess(Libadalang.BaseTypeDecl type) {
            return !type.isNone()
                    && !type.getUnit().equals(type.pStandardUnit())
                    && (type.pIsRecordType(Libadalang.AdaNode.NONE)
                            || AdaTypeMatcher.isPrivate(type)
                            || type.pIsTaggedType(Libadalang.AdaNode.NONE)
                            || type.pIsArrayType(Libadalang.AdaNode.NONE));
        }

        /** Include the parent proxy package of the decl, or decl if it already is a package. */
        void includeDecl(Libadalang.BasicDecl decl) {
            if (decl instanceof Libadalang.PackageRenamingDecl pack)
                includeDecl(pack.pRenamedPackage());
            else if (decl instanceof Libadalang.BasePackageDecl pack) {
                units.add(Package.getProxyUnitName(pack));
                units.add(Package.getWithPackageName(pack));
            } else includeDecl(decl.pParentBasicDecl());
        }

        void checkDecl(Libadalang.BasicDecl decl) {
            if (decl.isNone() || decl.getUnit().equals(decl.pStandardUnit())) return;
            if (decl instanceof Libadalang.PackageRenamingDecl pack)
                checkDecl(pack.pRenamedPackage());
            else if (decl instanceof Libadalang.BasePackageDecl pack) {
                includeDecl(pack);
            } else checkDecl(decl.pParentBasicDecl());
        }

        @Override
        public Void visit(AdaProxy proxy) {
            return null;
        }

        @Override
        public Void visit(Package pack) {
            // When the binded packages withs a renaming package, the with-clause of the latter are
            // not transitive. Since the package is only a renaming, getting the fully qualified
            // name of the declaration from the renaming package will use the renamed package name
            // instead. Since the with clause was not transitive, the proxy package will not have
            // visibility on the declaration.
            units.addAll(pack.getRenamedDependencies());
            pack.declarations.forEach(d -> d.accept(this));
            return null;
        }

        @Override
        public Void visit(Array array) {
            checkDecl(array.getComponentType());
            checkDecl(array.getIndexType());
            return null;
        }

        @Override
        public Void visit(AdaException adaException) {
            return null;
        }

        @Override
        public Void visit(Subprogram subprogram) {
            for (var param : subprogram.parameters) {
                checkDecl(param.getType());
            }
            checkDecl(subprogram.getReturnType());
            return null;
        }

        @Override
        public Void visit(EnumType enumType) {
            return null;
        }

        @Override
        public Void visit(Record rec) {
            for (var c : rec.getAllComponents()) {
                checkDecl(c.getType());
            }

            // It is necessary to with the package that contains the first private parent
            // type for extension aggregate.
            Libadalang.BaseTypeDecl parent = rec.getFirstPrivateParentType();
            if (!parent.isNone()) includeDecl(parent);

            // We need to include all types that the shadow type will override.
            if (rec.isInheritable(api)) {
                for (var m : rec.getAllMethods()) {
                    m.accept(this);
                }
            }
            return null;
        }

        @Override
        public Void visit(GlobalVariable globalVariable) {
            BaseTypeDecl type = globalVariable.getType();
            // Since a function is generated to return the global variable, we need to be able to
            // reach the declaration of the global variable's type access similarly to regular
            // functions.
            if (returnTypeUsesAccess(type)) checkDecl(type);
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
    }

    /**
     * Collect the list of all package names that are necessary to with for generating the input
     * proxy package.
     */
    public static List<String> getIncludes(Package pack, AdaAPI api) {
        Visitor visitor = new Visitor(api);
        visitor.visit(pack);
        visitor.units.remove(pack.getProxyUnitName());
        return visitor.units.stream().sorted().toList();
    }
}
