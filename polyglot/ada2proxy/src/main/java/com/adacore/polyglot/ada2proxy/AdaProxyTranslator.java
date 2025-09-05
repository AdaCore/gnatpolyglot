package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.AdaException;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxy;
import com.adacore.polyglot.ada2proxy.proxy.AdaProxyVisitor;
import com.adacore.polyglot.ada2proxy.proxy.Array;
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
import java.util.HashSet;
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
            modules.add(
                    new Module(
                            new FullyQualifiedName(
                                    Name.fromLower("polyglot"),
                                    Name.fromLower("ada"),
                                    Name.fromLower("arrays")),
                            proxy.arrayTypes.stream()
                                    .flatMap(
                                            a ->
                                                    a.memberFunctions().stream()
                                                            .map(f -> (Declaration) f))
                                    .toList()));
            return new Proxy(proxy.name, modules);
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
                    new FunctionTypeExpr(
                            subprogram.parameters.stream()
                                    .map(p -> (Parameter) p.accept(this))
                                    .toList(),
                            AdaAPI.makeTypeExpr(subprogram.getReturnType()),
                            subprogram.owner),
                    FunctionDecl.Visibility.PUBLIC,
                    subprogram.isFinal()
                            ? FunctionDecl.Overridability.FINAL
                            : FunctionDecl.Overridability.OVERRIDABLE,
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
            TypeExpr typeRef = AdaAPI.makeTypeExpr(subpParam.getType());
            boolean isConst = false;
            // If the parameter has the mode ``in`` or default, it is constant.
            if (!subpParam.isOutMode()) isConst = true;
            // If the parameter is not a scalar, or has ``out`` or ``in out`` mode, it must be a
            // reference.
            NativeType nat = AdaAPI.checkNativeType(subpParam.getType());
            if (nat == null || nat == NativeType.STRING || subpParam.isOutMode())
                typeRef = typeRef.makeReference(isConst);
            return new Parameter(subpParam.name, typeRef, subpParam.transfer);
        }

        @Override
        public EnumItem visit(EnumLiteral enumLiteral) {
            return new EnumItem(enumLiteral.name, enumLiteral.value, enumLiteral.getDoc());
        }

        private List<VTableEntry> makeVtable(Record classDecl) {
            // Only tagged types can have a vtable.
            if (!classDecl.isTaggedType()) return null;

            // Stores the names of all functions inside the vtable of the record.
            HashSet<Name> names = new HashSet<>();
            List<VTableEntry> entries = new ArrayList<>();

            for (var m : classDecl.getAllMethods()) {
                Name name = m.name;
                int suffix = 1;
                // We must avoid having multiple entries with the same name.
                while (!names.add(name)) {
                    name = Name.fromLower("%s_%d".formatted(m.name.toLower(), suffix));
                }
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
            declarations.add(rec.getCloneFunction());
            declarations.addAll(rec.getGettersAndSetters());
            if (rec.isTaggedType()) declarations.addAll(rec.getShadowAllocFunctions());
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
                        !rec.isTaggedType(),
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
    }

    public static Proxy translate(AdaProxy proxy) {
        Visitor visitor = new Visitor();
        return visitor.visit(proxy);
    }
}
