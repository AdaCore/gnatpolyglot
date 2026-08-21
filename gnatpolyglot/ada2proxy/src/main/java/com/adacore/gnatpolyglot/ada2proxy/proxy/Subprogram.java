//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Role;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.libadalang.Libadalang;
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

    /**
     * Return the SubpSpec of the decl. If the decl is the type declaration of an access to
     * subprogram, returns its SubpSpec.
     */
    public static Libadalang.BaseSubpSpec getSpec(Libadalang.BasicDecl decl) {
        return decl.pSubpSpecOrNull(true);
    }

    /** Instance variant of {@link #getSpec(Libadalang.BasicDecl)}. */
    private Libadalang.BaseSubpSpec getSpec() {
        return getSpec(origin);
    }

    @Override
    public String getDoc() {
        StringBuilder builder = new StringBuilder(AdaAPI.getDoc(origin));
        // If the function has a different name when binded in the proxy, add documentation to
        // inform the origin.
        if (!name.toPascalWithUnderscore().equals(origin.pRelativeName().getText())) {
            if (!builder.isEmpty()) builder.append("\n\n");
            builder.append("Binds to ").append(origin.pUniqueIdentifyingName());
        }
        return builder.toString();
    }

    /** Return the relative name of the declaration of origin. */
    public String getOriginName() {
        return origin.pRelativeName().getText();
    }

    /** Return the fully qualified name of the declaration of origin. */
    public String getOriginFullyQualifiedName() {
        return origin.pFullyQualifiedName();
    }

    /** Return whether ``funDecl`` is a procedure or a function. */
    public static boolean isProcedure(Libadalang.BaseSubpSpec spec) {
        return spec.pReturnType(spec).isNone();
    }

    /** Return whether ``funDecl`` is a procedure or a function. */
    public boolean isProcedure() {
        return isProcedure(getSpec());
    }

    /** Return the return type of the Ada subprogram. */
    public Libadalang.BaseTypeDecl getReturnType() {
        return getSpec().pReturnType(origin);
    }

    /** Return whether the subprogram is final or can be overriden. */
    public Overridability getOverridability() {
        if (origin instanceof Libadalang.AbstractSubpDecl) return Overridability.ABSTRACT;
        Libadalang.BaseTypeDecl controllingType = getSpec().pPrimitiveSubpTaggedType(false);
        if (role == null
                || role.kind != RoleKind.METHOD
                || parameters.isEmpty()
                || !parameters.get(0).getType().pIsTaggedType(Libadalang.AdaNode.NONE)
                // Only subprograms that have their first parameter which are of the controlling
                // type can be overriden.
                || !Objects.equals(controllingType, parameters.get(0).getType())
                // Functions that have the controlling parameter as their return type cannot be
                // overriden.
                || Objects.equals(controllingType, getReturnType())) return Overridability.FINAL;
        return Overridability.OVERRIDABLE;
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

    /** Return whether the subprogram is overriding an other suprogram. */
    public boolean isOverriding() {
        Libadalang.BasicDecl[] bases = origin.pBaseSubpDeclarations(false);
        // The method is not overriding when its only base is itself.
        return !(bases.length == 1 && bases[0].equals(origin));
    }
}
