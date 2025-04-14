package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Type expression that represents pointers. */
public class PointerTypeExpr extends TypeExpr {

    /** Type of the pointed data. */
    @JsonProperty(value = "type_expr")
    public final TypeExpr typeExpr;

    /** Whether the pointed memory is const. */
    @JsonProperty(value = "is_const")
    public final boolean isConst;

    /** Whether the pointer can be null. */
    @JsonProperty(value = "is_non_null")
    public final boolean isNonNull;

    @JsonCreator
    public PointerTypeExpr(
            @JsonProperty(value = "type_expr", required = true) TypeExpr typeExpr,
            @JsonProperty(value = "is_const") boolean isConst,
            @JsonProperty(value = "is_non_null") boolean isNonNull) {
        this.typeExpr = typeExpr;
        this.isConst = isConst;
        this.isNonNull = isNonNull;
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
