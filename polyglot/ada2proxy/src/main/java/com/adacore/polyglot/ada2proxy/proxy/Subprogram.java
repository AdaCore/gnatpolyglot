package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.SubpSpec;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import java.util.List;
import java.util.Objects;

public class Subprogram extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.BasicDecl origin;

    /** List of parameters the function accepts. */
    public List<SubpParam> parameters;

    /** C symbol to generate. */
    public String symbol;

    /** Role of the function. */
    public Role role;

    /** Owner of the value returned by the function. */
    public Owner owner;

    public Subprogram(
            Libadalang.BasicDecl origin,
            Name name,
            List<SubpParam> parameters,
            String symbol,
            Role role,
            Owner owner) {
        super(name);
        this.origin = origin;
        this.parameters = parameters;
        this.symbol = symbol;
        this.role = role;
        this.owner = owner;
    }

    /** Return the SubpSpec of the origin. */
    private Libadalang.SubpSpec getSpec() {
        return (SubpSpec) origin.pSubpSpecOrNull(false);
    }

    @Override
    public String getDoc() {
        StringBuilder builder = new StringBuilder(origin.pDoc());
        // If the function has a different name when binded in the proxy, add documentation to
        // inform the origin.
        if (!name.toPascalWithUnderscore().equals(origin.pRelativeName().getText())) {
            if (!builder.isEmpty()) builder.append("\n\n");
            builder.append("Binds to ").append(origin.pUniqueIdentifyingName());
        }
        return builder.toString();
    }

    /** Return the fully qualified name of the declaration of origin. */
    public String getOriginName() {
        return origin.pFullyQualifiedName();
    }

    /** Return whether ``funDecl`` is a procedure or a function. */
    public boolean isProcedure() {
        return getSpec().fSubpReturns().isNone();
    }

    /** Return the return type of the Ada subprogram. */
    public Libadalang.BaseTypeDecl getReturnType() {
        return getSpec().pReturnType(Libadalang.AdaNode.NONE);
    }

    /** Return whether the subprogram is final or can be overriden. */
    public boolean isFinal() {
        Libadalang.BaseTypeDecl controllingType = getSpec().pPrimitiveSubpTaggedType(false);
        return role == null
                || role.kind != RoleKind.METHOD
                || parameters.isEmpty()
                || !parameters.get(0).getType().pIsTaggedType(Libadalang.AdaNode.NONE)
                // Only subprograms that have their first parameter which are of the controlling
                // type can be overriden.
                || !Objects.equals(controllingType, parameters.get(0).getType())
                // Functions that have the controlling parameter as their return type cannot be
                // overriden.
                || Objects.equals(controllingType, getReturnType());
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(origin)
                .getParentFullyQualifiedName()
                .append(name);
    }
}
