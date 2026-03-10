//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Type expression that represents references. */
public class ReferenceTypeExpr extends TypeExpr {

    /** Type of the referenced value. */
    @JsonProperty(value = "type_expr")
    public final TypeExpr typeExpr;

    /** Whether the referenced value is const. */
    @JsonProperty(value = "is_const")
    public final boolean isConst;

    @JsonCreator
    public ReferenceTypeExpr(
            @JsonProperty(value = "type_expr", required = true) TypeExpr typeExpr,
            @JsonProperty(value = "is_const") boolean isConst) {
        this.typeExpr = typeExpr;
        this.isConst = isConst;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public FullyQualifiedName getName() {
        return typeExpr.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeExpr, isConst);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ReferenceTypeExpr other) {
            return this.isConst == other.isConst
                    && Objects.deepEquals(this.typeExpr, other.typeExpr);
        }
        return false;
    }

    @Override
    public TypeExpr referencedType() {
        return typeExpr;
    }

    @Override
    public boolean isReference() {
        return true;
    }
}
