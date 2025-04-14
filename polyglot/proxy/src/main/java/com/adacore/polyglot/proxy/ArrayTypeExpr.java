package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
