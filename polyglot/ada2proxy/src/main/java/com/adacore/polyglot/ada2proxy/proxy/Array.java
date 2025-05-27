package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.ArrayTypeExpr;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import com.adacore.polyglot.proxy.TypeExpr;
import java.util.ArrayList;
import java.util.List;

public class Array implements AdaProxyObject {

    public final Libadalang.BaseTypeDecl componentType;

    private List<FunctionDecl> functions;

    public Array(Libadalang.BaseTypeDecl componentType) {
        this.componentType = componentType;
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        // The 3rd character of symbols for polyglot genererated functions is "G" (for Generated)
        StringBuilder symbolBuilder = new StringBuilder("_PG_Polyglot_Arrays_");
        symbolBuilder.append(componentType.pFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    public List<FunctionDecl> memberFunctions() {
        if (functions == null) {
            functions = new ArrayList<>();

            FullyQualifiedName moduleName =
                    new FullyQualifiedName(
                            Name.fromLower("polyglot"),
                            Name.fromLower("ada"),
                            Name.fromLower("arrays"));

            TypeExpr componentTypeExpr = AdaAPI.makeTypeExpr(componentType);
            ArrayTypeExpr arrayTypeExpr = componentTypeExpr.makeArray();

            // Declare the allocation function
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("alloc")),
                            "Returns a newly allocated array",
                            new Role(RoleKind.ALLOC, arrayTypeExpr, null),
                            buildMemberSymbol("_Alloc"),
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
                            Owner.USER,
                            false,
                            false,
                            false));
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("clone")),
                            "Return a newly allocated copy of ``self``",
                            new Role(RoleKind.ALLOC, arrayTypeExpr, null),
                            buildMemberSymbol("_Clone"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            arrayTypeExpr.makeReference(true),
                                            new Transfer(RequiredOwner.ANY))),
                            arrayTypeExpr,
                            Owner.USER,
                            false,
                            false,
                            false));

            // Declare the constructor functions
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("construct")),
                            "Construct a new array at ``Self``, starting at ``first`` and ending at"
                                    + " ``last``",
                            new Role(RoleKind.CONSTRUCT, arrayTypeExpr, null),
                            buildMemberSymbol("_Construct"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            arrayTypeExpr.makePointer(false, true),
                                            new Transfer(RequiredOwner.ANY)),
                                    new Parameter(
                                            Name.fromLower("first"),
                                            NativeType.SINT32.typeExpr,
                                            new Transfer(RequiredOwner.ANY)),
                                    new Parameter(
                                            Name.fromLower("last"),
                                            NativeType.SINT32.typeExpr,
                                            new Transfer(RequiredOwner.ANY))),
                            NativeType.VOID.typeExpr,
                            Owner.USER,
                            false,
                            false,
                            false));
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("copy")),
                            "Create a newly allocated copy of ``from`` at ``to``",
                            new Role(RoleKind.CONSTRUCT, arrayTypeExpr, null),
                            buildMemberSymbol("_Copy"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("to"),
                                            arrayTypeExpr.makePointer(false, true),
                                            new Transfer(RequiredOwner.ANY)),
                                    new Parameter(
                                            Name.fromLower("from"),
                                            arrayTypeExpr.makeReference(true),
                                            new Transfer(RequiredOwner.ANY))),
                            NativeType.VOID.typeExpr,
                            Owner.USER,
                            false,
                            false,
                            false));

            // Declare the freeing function
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("free")),
                            "Frees the array held by ``Data``",
                            new Role(RoleKind.FREE, arrayTypeExpr, null),
                            buildMemberSymbol("_Free"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            arrayTypeExpr.makePointer(false, false),
                                            new Transfer(RequiredOwner.ANY))),
                            NativeType.VOID.typeExpr,
                            Owner.UNKNOWN,
                            false,
                            false,
                            false));

            // Declare the getter
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("get")),
                            "Return the value of the element at ``Index``",
                            new Role(RoleKind.GETTER, arrayTypeExpr, null),
                            buildMemberSymbol("_Getter"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            arrayTypeExpr.makeReference(true),
                                            new Transfer(RequiredOwner.ANY)),
                                    new Parameter(
                                            Name.fromLower("index"),
                                            NativeType.SINT32.typeExpr,
                                            new Transfer(RequiredOwner.ANY))),
                            arrayTypeExpr.typeExpr.makeReference(false),
                            Owner.UNKNOWN,
                            false,
                            false,
                            false));

            // Declare the setter
            functions.add(
                    new FunctionDecl(
                            moduleName.append(Name.fromLower("set")),
                            "Sets the value of the element at ``Index`` to ``New_Val``",
                            new Role(RoleKind.SETTER, arrayTypeExpr, null),
                            buildMemberSymbol("_Setter"),
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
                                            new Transfer(RequiredOwner.ANY))),
                            NativeType.VOID.typeExpr,
                            Owner.UNKNOWN,
                            false,
                            false,
                            false));
        }
        return functions;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
