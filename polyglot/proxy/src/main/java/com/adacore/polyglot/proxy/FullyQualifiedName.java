//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Fully qualified name of a declaration. */
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

    /** Create a new {@link FullyQualifiedName} and adds ``name`` to it. */
    public FullyQualifiedName append(Name name) {
        List<Name> newNames = new ArrayList<>(names);
        newNames.add(name);
        return new FullyQualifiedName(newNames);
    }

    /** Create a new {@link FullyQualifiedName} and adds ``other.names`` to it. */
    public FullyQualifiedName append(FullyQualifiedName other) {
        List<Name> newNames = new ArrayList<>(names);
        newNames.addAll(other.names);
        return new FullyQualifiedName(newNames);
    }

    /** Return the last name of the {@link FullyQualifiedName}. */
    public Name getLastName() {
        return names.get(names.size() - 1);
    }

    /** Create a new {@link FullyQualifiedName} without the last name. */
    public FullyQualifiedName getParentFullyQualifiedName() {
        if (names.size() == 1) return null;
        return new FullyQualifiedName(names.subList(0, names.size() - 1));
    }

    /** Create a {@link NameTypeExpr} from the current fully qualified name. */
    public NameTypeExpr asTypeExpr() {
        return new NameTypeExpr(this);
    }

    /**
     * Join all the names with a prefix, suffix and separator, using a function to convert the names
     * to strings. The converter is run on every sub-FullyQualifiedName and should return a
     * conversion of the last name only.
     */
    public String join(
            Function<FullyQualifiedName, String> converter,
            String prefix,
            String separator,
            String suffix) {
        StringBuilder builder = new StringBuilder(prefix);
        FullyQualifiedName current = new FullyQualifiedName(names.subList(0, 1));
        builder.append(converter.apply(current));
        for (int i = 2; i <= names.size(); i++) {
            current = new FullyQualifiedName(names.subList(0, i));
            builder.append(separator).append(converter.apply(current));
        }
        return builder.append(suffix).toString();
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
        return Objects.hash(names);
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
