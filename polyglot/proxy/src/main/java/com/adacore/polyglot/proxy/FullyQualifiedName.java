package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Prefix of a reference. */
public class FullyQualifiedName implements ProxyObject {
    /** Name of the refered type or module. */
    @JsonProperty("names")
    public final List<Name> names;

    @JsonCreator
    public FullyQualifiedName(@JsonProperty(value = "names", required = true) List<Name> names) {
        this.names = names;
    }

    public FullyQualifiedName(Name... name) {
        this.names = List.of(name);
    }

    public FullyQualifiedName append(Name name) {
        List<Name> newNames = new ArrayList<>(names);
        newNames.add(name);
        return new FullyQualifiedName(newNames);
    }

    public Name getLastName() {
        return names.get(names.size() - 1);
    }

    public FullyQualifiedName getParentFullyQualifiedName() {
        if (names.size() == 1) return null;
        return new FullyQualifiedName(names.subList(0, names.size() - 1));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof FullyQualifiedName other) {
            return names.equals(other.names);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + names.hashCode();
        return hash;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
