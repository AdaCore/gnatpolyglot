package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import java.util.List;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    /** Reference to the record. */
    private Reference ref;

    /** Default freeing function of the type. */
    private FunctionDecl freeFunction;

    /** Default allocating function of the type. */
    private FunctionDecl allocFunction;

    /** Default cloning function of the type. */
    private FunctionDecl cloneFunction;

    public Record(Libadalang.TypeDecl origin, Name name) {
        super(name);
        this.origin = origin;
    }

    /** Return the fully qualified name of the type. */
    public String getFullyQualifiedName() {
        return this.origin.pFullyQualifiedName();
    }

    /** Create a reference to the current type. */
    public Reference getReference() {
        if (this.ref == null) this.ref = AdaAPI.makeReferenceTo(this.origin, false);
        return this.ref;
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        StringBuilder symbolBuilder = new StringBuilder("_P_");
        symbolBuilder.append(getFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    /** Return the freeing function of the type, or generate a new one if necessary. */
    public FunctionDecl getFreeFunction() {
        if (this.freeFunction == null)
            this.freeFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_free"))),
                            "Generated function to free ``Self``",
                            new Role(RoleKind.FREE, getReference(), null),
                            buildMemberSymbol("_Default_Free"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            getReference().withIsPointer(true),
                                            new Transfer(RequiredOwner.USER))),
                            NativeType.VOID.reference,
                            Owner.UNKNOWN,
                            false,
                            false,
                            false);
        return this.freeFunction;
    }

    /** Return the allocating function of the type, or generate a new one if necessary. */
    public FunctionDecl getAllocFunction() {
        if (this.allocFunction == null) {
            Reference type = getReference();
            this.allocFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Alloc"),
                            List.of(),
                            type,
                            Owner.USER,
                            false,
                            false,
                            false);
        }
        return this.allocFunction;
    }

    /** Return the cloning function of the type, or generate a new one if necessary. */
    public FunctionDecl getCloneFunction() {
        if (this.cloneFunction == null) {
            Reference type = getReference();
            this.cloneFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_clone"))),
                            "Generated function to clone a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Clone"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            type.withIsReference(true).withIsConst(true),
                                            new Transfer(RequiredOwner.USER))),
                            type,
                            Owner.USER,
                            false,
                            false,
                            false);
        }
        return this.cloneFunction;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public ClassDecl toPolyglotProxy() {
        if (this.origin.fTypeDef() instanceof Libadalang.PrivateTypeDef def) {
            return new ClassDecl(
                    AdaAPI.makeProxyFullyQualifiedName(origin, false),
                    this.origin.pDoc(),
                    null,
                    8,
                    false,
                    List.of());
        }
        throw new UnsupportedOperationException(
                "Unsupported Ada type:" + this.origin.fTypeDef().getImage());
    }
}
