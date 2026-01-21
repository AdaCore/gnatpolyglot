package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import java.util.ArrayList;
import java.util.List;

public class AdaException extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.ExceptionDecl origin;

    /**
     * Specific defining name of this exception.
     *
     * <p>Useful when the ``origin`` ExceptionDecl has multiple defining names, to identify the
     * specific one that this instance represents.
     */
    private final Libadalang.DefiningName definingName;

    /** Enum value in the proxy. */
    private final int value;

    /** Default allocating function of the exception. */
    private ArrayList<FunctionDecl> allocFunctions;

    public AdaException(
            Name name, Libadalang.ExceptionDecl origin, Libadalang.DefiningName dn, int value) {
        super(name);
        this.origin = origin;
        this.definingName = dn;
        this.value = value;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return origin.pDoc();
    }

    public int getValue() {
        return value;
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(definingName)
                .getParentFullyQualifiedName()
                .append(name);
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        // The 3rd character of symbols for polyglot genererated functions is "G" (for Generated)
        StringBuilder symbolBuilder = new StringBuilder("_PG");
        symbolBuilder.append(getFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    public String getFullyQualifiedName() {
        return definingName.pFullyQualifiedName();
    }

    public List<FunctionDecl> getAllocFunctions() {
        if (allocFunctions == null) {
            allocFunctions = new ArrayList<>();
            // Constructor with no message
            allocFunctions.add(
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(
                                    RoleKind.ALLOC,
                                    getProxyFullyQualifiedName().asTypeExpr(),
                                    null),
                            buildMemberSymbol("_Default_Alloc"),
                            new FunctionTypeExpr(
                                    List.of(),
                                    getProxyFullyQualifiedName().asTypeExpr(),
                                    Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Constructor with message
            allocFunctions.add(
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_alloc_message"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(
                                    RoleKind.ALLOC,
                                    getProxyFullyQualifiedName().asTypeExpr(),
                                    null),
                            buildMemberSymbol("_Default_Alloc_Message"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("what"),
                                                    NativeType.STRING.typeExpr.makeReference(true),
                                                    new Transfer(RequiredOwner.ANY))),
                                    getProxyFullyQualifiedName().asTypeExpr(),
                                    Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));
        }
        return allocFunctions;
    }
}
