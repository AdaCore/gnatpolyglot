//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.ada2proxy.AdaTypeMatcher;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;

public class Component implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.ComponentDecl origin;

    /** Name of the Ada component */
    public Name name;

    public Component(Libadalang.ComponentDecl origin, Name name) {
        this.origin = origin;
        this.name = name;
    }

    public boolean hasDefaultValue() {
        return !origin.fDefaultExpr().isNone()
                // Component who are of a controlled type implicitely have a default value.
                || AdaTypeMatcher.isControlledType(origin.pFormalType(origin));
    }

    /**
     * Return the type that the will be use to set this component's value in constructors and
     * setters.
     */
    public TypeExpr getSetterType() {
        // Get the type of the setter's new value.
        BaseTypeDecl type = getType();
        TypeExpr setterType = AdaAPI.makeTypeExpr(type);
        if (!type.pIsScalarType(Libadalang.AdaNode.NONE)
                && !type.pIsAccessType(Libadalang.AdaNode.NONE))
            // If the argument is not a scalar not an access, get a const reference to the new
            // value.
            setterType = setterType.makeReference(true);
        return setterType;
    }

    /** Return the type return type of the getter used to get this component. */
    public TypeExpr getGetterType() {

        Libadalang.BaseTypeDecl type = getType();
        TypeExpr typeExpr = AdaAPI.makeTypeExpr(type);
        return type.pIsAccessType(Libadalang.AdaNode.NONE)
                ? typeExpr
                : typeExpr.makeReference(false);
    }

    /** Return the type of the component. */
    public Libadalang.BaseTypeDecl getType() {
        return this.origin.pFormalType(origin);
    }

    public boolean isLimited() {
        return getType().pIsLimitedType();
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getDoc() {
        return origin.pDoc();
    }
}
