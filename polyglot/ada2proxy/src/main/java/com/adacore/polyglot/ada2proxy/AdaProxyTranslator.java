package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxyVisitor;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.EnumLiteral;
import com.adacore.polyglot.ada2proxy.proxy.EnumType;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.Declaration;
import com.adacore.polyglot.proxy.EnumItem;
import com.adacore.polyglot.proxy.EnumerationDecl;
import com.adacore.polyglot.proxy.Field;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Proxy;
import com.adacore.polyglot.proxy.ProxyObject;
import com.adacore.polyglot.proxy.TypeExpr;
import java.util.ArrayList;
import java.util.List;

public class AdaProxyTranslator {

    private static class Visitor implements AdaProxyVisitor<ProxyObject> {

        private List<Declaration> declarations;

        @Override
        public Proxy visit(AdaProxy proxy) {
            List<Module> modules = new ArrayList<>(proxy.packages.size());
            for (var pack : proxy.packages) {
                modules.add((Module) pack.accept(this));
            }
            modules.add(new Module(new FullyQualifiedName(Name.fromLower("polyglot")), List.of()));
            modules.add(
                    new Module(
                            new FullyQualifiedName(
                                    Name.fromLower("polyglot"), Name.fromLower("ada")),
                            List.of()));
            return new Proxy(modules);
        }

        @Override
        public Module visit(Package pack) {

            declarations = new ArrayList<>(pack.declarations.size());

            for (var decl : pack.declarations) {
                declarations.add((Declaration) decl.accept(this));
            }

            return new Module(pack.getProxyFullyQualifiedName(), declarations);
        }

        @Override
        public FunctionDecl visit(Subprogram subprogram) {
            return new FunctionDecl(
                    subprogram.getProxyFullyQualifiedName(),
                    subprogram.getDoc(),
                    subprogram.role,
                    subprogram.symbol,
                    subprogram.parameters.stream().map(p -> (Parameter) p.accept(this)).toList(),
                    AdaAPI.makeTypeExpr(subprogram.getReturnType()),
                    subprogram.owner,
                    false,
                    false,
                    false);
        }

        @Override
        public EnumerationDecl visit(EnumType enumType) {
            return new EnumerationDecl(
                    enumType.getProxyFullyQualifiedName(),
                    enumType.getDoc(),
                    enumType.items.stream().map(lit -> (EnumItem) lit.accept(this)).toList());
        }

        @Override
        public Parameter visit(SubpParam subpParam) {
            TypeExpr typeRef = AdaAPI.makeTypeExpr(subpParam.getType());
            boolean isConst = false;
            // If the parameter has the mode ``in`` or default, it is constant.
            if (!subpParam.isOutMode()) isConst = true;
            // If the parameter is not a scalar, or has ``out`` or ``in out`` mode, it must be a
            // reference.
            if (AdaAPI.checkNativeType(subpParam.getType()) == null || subpParam.isOutMode())
                typeRef = typeRef.makeReference(isConst);
            return new Parameter(subpParam.name, typeRef, subpParam.transfer);
        }

        @Override
        public EnumItem visit(EnumLiteral enumLiteral) {
            return new EnumItem(enumLiteral.name, enumLiteral.value, enumLiteral.getDoc());
        }

        @Override
        public ClassDecl visit(Record rec) {
            declarations.add(rec.getAllocFunction());
            declarations.add(rec.getFreeFunction());
            declarations.add(rec.getCloneFunction());
            declarations.addAll(rec.getGettersAndSetters());
            if (rec.getTypeDef() instanceof Libadalang.RecordTypeDef
                    || rec.getTypeDef() instanceof Libadalang.PrivateTypeDef) {
                return new ClassDecl(
                        rec.getProxyFullyQualifiedName(),
                        rec.getDoc(),
                        null,
                        8,
                        false,
                        rec.components.stream().map(c -> (Field) c.accept(this)).toList());
            }
            throw new UnsupportedOperationException(
                    "Unsupported Ada type:" + rec.getTypeDef().getImage());
        }

        @Override
        public Field visit(Component component) {
            return new Field(
                    component.name, component.getDoc(), AdaAPI.makeTypeExpr(component.getType()));
        }
    }

    public static Proxy translate(AdaProxy proxy) {
        Visitor visitor = new Visitor();
        return visitor.visit(proxy);
    }
}
