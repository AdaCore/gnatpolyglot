//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent a field of a class. */
public class Field implements ProxyObject {
    /** The name of the field. */
    @JsonProperty("name")
    public final Name name;

    /** The documentation of the field. */
    @JsonProperty("doc")
    public final String doc;

    /** Type of the field. */
    @JsonProperty("type")
    public final TypeExpr type;

    @JsonCreator
    public Field(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "type", required = true) TypeExpr type) {
        this.name = name;
        this.doc = doc;
        this.type = type;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
