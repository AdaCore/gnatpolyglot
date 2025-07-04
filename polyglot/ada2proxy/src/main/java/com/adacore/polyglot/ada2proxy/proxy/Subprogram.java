package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Role;
import java.util.List;

public class Subprogram extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.SubpDecl origin;

    /** List of parameters the function accepts. */
    public List<SubpParam> parameters;

    /** C symbol to generate. */
    public String symbol;

    /** Role of the function. */
    public Role role;

    /** Owner of the value returned by the function. */
    public Owner owner;

    public Subprogram(
            Libadalang.SubpDecl origin,
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
        return origin.fSubpSpec().fSubpReturns().isNone();
    }

    /** Return the return type of the Ada subprogram. */
    public Libadalang.BaseTypeDecl getReturnType() {
        return origin.fSubpSpec().pReturnType(Libadalang.AdaNode.NONE);
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
