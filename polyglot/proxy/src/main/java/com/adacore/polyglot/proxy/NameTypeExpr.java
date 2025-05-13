package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof NameTypeExpr other) {
            return Objects.deepEquals(this.name, other.name);
        }
        return false;
    }
}
