package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
