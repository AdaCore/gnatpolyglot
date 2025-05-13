package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Type expression that represents arrays. */
public class ArrayTypeExpr extends TypeExpr {

    /** Type of the elements of the array. */
    @JsonProperty(value = "type_expr")
    public final TypeExpr typeExpr;

    @JsonCreator
    public ArrayTypeExpr(@JsonProperty(value = "type_expr", required = true) TypeExpr typeExpr) {
        this.typeExpr = typeExpr;
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
        return Objects.hash(typeExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ArrayTypeExpr other) {
            return Objects.deepEquals(this.typeExpr, other.typeExpr);
        }
        return false;
    }
}
