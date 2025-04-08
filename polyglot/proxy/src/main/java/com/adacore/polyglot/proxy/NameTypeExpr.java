package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NameTypeExpr extends TypeExpr {

    @JsonProperty(value = "name")
    public final FullyQualifiedName name;

    @JsonCreator
    public NameTypeExpr(@JsonProperty(value = "name", required = true) FullyQualifiedName name) {
        this.name = name;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public FullyQualifiedName getName() {
        return name;
    }
}
