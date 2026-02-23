package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import com.adacore.polyglot.NativeType;
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
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.ClassDecl.Inheritability;
import com.adacore.polyglot.proxy.Declaration;
import com.adacore.polyglot.proxy.EnumItem;
import com.adacore.polyglot.proxy.EnumerationDecl;
import com.adacore.polyglot.proxy.ExceptionDecl;
import com.adacore.polyglot.proxy.Field;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Proxy;
import com.adacore.polyglot.proxy.ProxyObject;
import com.adacore.polyglot.proxy.TypeExpr;
import com.adacore.polyglot.proxy.VTableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdaProxyTranslator {

    private static class Visitor implements AdaProxyVisitor<ProxyObject> {

        private List<Declaration> declarations;

        AdaAPI api;

        public Visitor(AdaAPI api) {
            this.api = api;
        }

        private Inheritability getInheritability(Record rec) {
            if (rec.isAbstract()) return Inheritability.VIRTUAL;
            if (!rec.isInheritable(api)) return Inheritability.FINAL;
            return Inheritability.INHERITABLE;
        }

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
            modules.add(
                    new Module(
                            new FullyQualifiedName(
                                    Name.fromLower("polyglot"),
                                    Name.fromLower("ada"),
                                    Name.fromLower("arrays")),
                            ArrayCollector.getAllComponentTypes(proxy).stream()
                                    .filter(c -> AdaAPI.checkNativeType(c) == null)
                                    .flatMap(
                                            c ->
                                                    Array.memberFunctions(c).stream()
                                                            .map(f -> (Declaration) f))
                                    .toList()));
            return new Proxy(proxy.name, modules);
        }

        @Override
        public Module visit(Package pack) {

            declarations = new ArrayList<>(pack.declarations.size());

            for (var decl : pack.declarations) {
                Optional.ofNullable((Declaration) decl.accept(this))
                        .ifPresent(d -> declarations.add(d));
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
                    new FunctionTypeExpr(
                            subprogram.parameters.stream()
                                    .map(p -> (Parameter) p.accept(this))
                                    .toList(),
                            AdaAPI.makeTypeExpr(subprogram.getReturnType()),
                            subprogram.owner),
                    FunctionDecl.Visibility.PUBLIC,
                    subprogram.getOverridability(),
                    FunctionDecl.Staticness.NON_STATIC);
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
            BaseTypeDecl type = subpParam.getType();
            TypeExpr typeRef = AdaAPI.makeTypeExpr(type);
            boolean isConst = false;
            // If the parameter has the mode ``in`` or default, it is constant.
            if (!subpParam.isOutMode()) isConst = true;
            // If the parameter is neither a scalar, an enum type, an access type, or has ``out`` or
            // ``in out`` mode, it must be a reference.
            NativeType nat = AdaAPI.checkNativeType(type);
            if ((!type.pIsEnumType(Libadalang.AdaNode.NONE)
                            && !type.pIsAccessType(Libadalang.AdaNode.NONE)
                            && (nat == null || nat == NativeType.STRING))
                    || subpParam.isOutMode()) typeRef = typeRef.makeReference(isConst);
            return new Parameter(subpParam.name, typeRef, subpParam.transfer);
        }

        @Override
        public EnumItem visit(EnumLiteral enumLiteral) {
            return new EnumItem(enumLiteral.name, enumLiteral.value, enumLiteral.getDoc());
        }

        /**
         * Return the vtable for the given class declaration.
         *
         * <p>Any type that is inheritable or virtual should have a vtable, and if a type is final,
         * then the `null` list is returned. If a type is not inheritable and not final, it will
         * still have a vtable, although empty. Minimal vtables always have some function pointers
         * for freeing and cloning shadow objects.
         */
        private List<VTableEntry> makeVtable(Record classDecl) {
            // Only tagged types can have a vtable.
            if (getInheritability(classDecl) == Inheritability.FINAL) return null;
            if (getInheritability(classDecl) == Inheritability.VIRTUAL
                    && !classDecl.isInheritable(api)) return List.of();
            List<VTableEntry> entries = new ArrayList<>();
            for (var m : classDecl.getAllMethods()) {
                Name name = m.name;
                entries.add(
                        new VTableEntry(
                                name,
                                new FunctionTypeExpr(
                                        m.parameters.stream()
                                                .map(p -> (Parameter) p.accept(this))
                                                .toList(),
                                        AdaAPI.makeTypeExpr(m.getReturnType()),
                                        m.owner)));
            }
            return entries;
        }

        @Override
        public ClassDecl visit(Record rec) {
            declarations.addAll(rec.getAllocFunctions());
            declarations.add(rec.getFreeFunction());
            if (!rec.isLimited()) {
                declarations.add(rec.getCloneFunction());
                declarations.add(rec.getCopyFunction());
            }
            declarations.addAll(rec.getGettersAndSetters());
            if (rec.isInheritable(api)) declarations.addAll(rec.getShadowAllocFunctions());
            if (rec.getTypeDef() instanceof Libadalang.RecordTypeDef
                    || rec.getTypeDef() instanceof Libadalang.PrivateTypeDef
                    || rec.getTypeDef() instanceof Libadalang.DerivedTypeDef) {
                FullyQualifiedName parentType =
                        rec.parent == null ? null : rec.parent.getProxyFullyQualifiedName();
                return new ClassDecl(
                        rec.getProxyFullyQualifiedName(),
                        rec.getDoc(),
                        parentType,
                        8,
                        getInheritability(rec),
                        rec.components.stream().map(c -> (Field) c.accept(this)).toList(),
                        makeVtable(rec));
            }
            throw new UnsupportedOperationException(
                    "Unsupported Ada type:" + rec.getTypeDef().getImage());
        }

        @Override
        public Field visit(Component component) {
            return new Field(
                    component.name, component.getDoc(), AdaAPI.makeTypeExpr(component.getType()));
        }

        @Override
        public ProxyObject visit(Array array) {
            // Nothing to do
            return null;
        }

        @Override
        public ProxyObject visit(AdaException adaException) {
            declarations.addAll(adaException.getAllocFunctions());
            return new ExceptionDecl(
                    adaException.getProxyFullyQualifiedName(),
                    adaException.getDoc(),
                    adaException.getValue());
        }

        @Override
        public ProxyObject visit(GlobalVariable globalVariable) {
            declarations.add(globalVariable.getGetter());
            Optional.ofNullable(globalVariable.getSetter()).ifPresent(d -> declarations.add(d));
            return null;
        }

        @Override
        public ProxyObject visit(Subtype subtype) {
            // Nothing to do
            return null;
        }
    }

    public static Proxy translate(AdaProxy proxy, AdaAPI api) {
        Visitor visitor = new Visitor(api);
        return visitor.visit(proxy);
    }
}
