//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Represent a function parameter. */
public class Parameter implements ProxyObject {
    /** The name of the parameter. */
    @JsonProperty("name")
    public final Name name;

    /** The type of the parameter. */
    @JsonProperty("type")
    public final TypeExpr type;

    /** Transfer informations. */
    @JsonProperty("transfer")
    public final Transfer transfer;

    @JsonCreator
    public Parameter(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "type", required = true) TypeExpr type,
            @JsonProperty(value = "transfer", required = true) Transfer transfer) {
        this.name = name;
        this.type = type;
        this.transfer = transfer;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Parameter other) {
            return Objects.deepEquals(name, other.name)
                    && Objects.deepEquals(type, other.type)
                    && Objects.deepEquals(transfer, other.transfer);
        }
        return false;
    }
}
