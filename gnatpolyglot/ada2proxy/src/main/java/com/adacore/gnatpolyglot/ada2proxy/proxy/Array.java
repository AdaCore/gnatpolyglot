//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.proxy.ArrayTypeExpr;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.Role;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.Transfer;
import com.adacore.gnatpolyglot.proxy.Transfer.RequiredOwner;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.libadalang.Libadalang;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Array extends AdaDeclaration {

    public final Libadalang.BaseTypeDecl arrayType;

    private static HashMap<Libadalang.BaseTypeDecl, List<FunctionDecl>> functionsMap =
            new HashMap<>();

    public Array(Libadalang.BaseTypeDecl arrayType) {
        super(AdaAPI.getName(arrayType.pDefiningName()));
        this.arrayType = arrayType;
    }

    /** Create a symbol for generated member functions. */
    private static String buildMemberSymbol(String componentTypename, String suffix) {
        // The 3rd character of symbols for GNATpolyglot genererated functions is "G" (for
        // Generated)
        StringBuilder symbolBuilder = new StringBuilder("_PG_Polyglot_Arrays_");
        symbolBuilder.append(componentTypename).append(suffix);
        return symbolBuilder.toString();
    }

    public static List<FunctionDecl> memberFunctions(Libadalang.BaseTypeDecl componentType) {
        List<FunctionDecl> functions = functionsMap.get(componentType);
        if (functions == null) {
            functions = new ArrayList<>();

            FullyQualifiedName moduleName =
                    new FullyQualifiedName(
                            Name.fromLower("gnatpolyglot"),
                            Name.fromLower("ada"),
                            Name.fromLower("arrays"));
            String componentTypename = componentType.pFullyQualifiedName().replace(".", "_");

            TypeExpr componentTypeExpr = AdaAPI.makeTypeExpr(componentType);
            ArrayTypeExpr arrayTypeExpr = componentTypeExpr.makeArray();

            // Declare the allocation function
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("alloc")),
                            "Returns a newly allocated array",
                            new Role(RoleKind.ALLOC, arrayTypeExpr, null),
                            buildMemberSymbol(componentTypename, "_Alloc"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("first"),
                                                    NativeType.SINT32.typeExpr,
                                                    new Transfer(RequiredOwner.ANY)),
                                            new Parameter(
                                                    Name.fromLower("last"),
                                                    NativeType.SINT32.typeExpr,
                                                    new Transfer(RequiredOwner.ANY))),
                                    arrayTypeExpr,
                                    Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("clone")),
                            "Return a newly allocated copy of ``self``",
                            new Role(RoleKind.ALLOC, arrayTypeExpr, null),
                            buildMemberSymbol(componentTypename, "_Clone"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    arrayTypeExpr.makeReference(true),
                                                    new Transfer(RequiredOwner.ANY))),
                                    arrayTypeExpr,
                                    Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Declare the freeing function
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("free")),
                            "Frees the array held by ``Data``",
                            new Role(RoleKind.FREE, arrayTypeExpr, null),
                            buildMemberSymbol(componentTypename, "_Free"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    arrayTypeExpr.makePointer(false, false),
                                                    new Transfer(RequiredOwner.ANY))),
                                    NativeType.VOID.typeExpr,
                                    Owner.UNKNOWN),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Declare the getter
            TypeExpr getterReturnType =
                    componentTypeExpr.isPointer() || componentTypeExpr.isFunction()
                            ? componentTypeExpr
                            : componentTypeExpr.makeReference(false);
            // If the array component is an access type, the owner must be the library: it is
            // technically already escaped since contained in the array, and returned by copy so the
            // user will have its own instance of the pointer instead of a reference.
            // Otherwise, since we are returning a reference to the component of an entity, it
            // should never be freed so its owner is static.
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("get")),
                            "Return the value of the element at ``Index``",
                            new Role(RoleKind.GETTER, arrayTypeExpr, null),
                            buildMemberSymbol(componentTypename, "_Getter"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    arrayTypeExpr.makeReference(true),
                                                    new Transfer(RequiredOwner.ANY)),
                                            new Parameter(
                                                    Name.fromLower("index"),
                                                    NativeType.SINT32.typeExpr,
                                                    new Transfer(RequiredOwner.ANY))),
                                    getterReturnType,
                                    componentTypeExpr instanceof PointerTypeExpr
                                            ? Owner.LIBRARY
                                            : Owner.STATIC),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Callbacks cannot be escaped, so do not generate a setter.
            if (!componentTypeExpr.isFunction()) {
                // Declare the setter
                //
                // If the component is an access type, the owner must be library: the subprogram
                // explicitely escapes the pointer. In other cases, the owner does not mater as the
                // element will be copied, creating a new instance in the array.
                functions.add(
                        new FunctionDecl(
                                moduleName.append(Name.fromLower("set")),
                                "Sets the value of the element at ``Index`` to ``New_Val``",
                                new Role(RoleKind.SETTER, arrayTypeExpr, null),
                                buildMemberSymbol(componentTypename, "_Setter"),
                                new FunctionTypeExpr(
                                        List.of(
                                                new Parameter(
                                                        Name.fromLower("self"),
                                                        arrayTypeExpr.makeReference(false),
                                                        new Transfer(RequiredOwner.ANY)),
                                                new Parameter(
                                                        Name.fromLower("index"),
                                                        NativeType.SINT32.typeExpr,
                                                        new Transfer(RequiredOwner.ANY)),
                                                new Parameter(
                                                        Name.fromLower("new_val"),
                                                        arrayTypeExpr.typeExpr.makeReference(true),
                                                        new Transfer(
                                                                componentTypeExpr
                                                                                instanceof
                                                                                PointerTypeExpr
                                                                        ? RequiredOwner.LIBRARY
                                                                        : RequiredOwner.ANY))),
                                        NativeType.VOID.typeExpr,
                                        Owner.UNKNOWN),
                                FunctionDecl.Visibility.PUBLIC,
                                FunctionDecl.Overridability.FINAL,
                                FunctionDecl.Staticness.NON_STATIC));
            }
        }
        return functions;
    }

    public String getFullyQualifiedName() {
        return arrayType.pFullyQualifiedName();
    }

    public Libadalang.BaseTypeDecl getComponentType() {
        return arrayType.pCompType(false, arrayType);
    }

    public Libadalang.BaseTypeDecl getIndexType() {
        return arrayType.pIndexType(0, arrayType);
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return AdaAPI.getDoc(arrayType);
    }
}
