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
import com.adacore.polyglot.ada2proxy.proxy.Subtype;
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

        void checkDecl(Libadalang.BasicDecl decl) {
            if (decl.isNone()
                    || decl instanceof Libadalang.BaseTypeDecl type
                            && AdaAPI.checkNativeType(type) != null) return;
            if (decl instanceof Libadalang.BasePackageDecl pack) {
                if (Package.isAdaRuntimePackage(pack)) units.add(Package.getProxyUnitName(pack));
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
    }

    /**
     * Collect the list of all package names that are necessary to with for generating the input
     * proxy package.
     */
    public static List<String> getIncludes(Package pack) {
        Visitor visitor = new Visitor();
        visitor.visit(pack);
        return visitor.units.stream().toList();
    }
}
