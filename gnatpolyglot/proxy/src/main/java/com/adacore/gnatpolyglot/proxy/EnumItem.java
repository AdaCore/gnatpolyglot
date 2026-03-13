//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent a C-like enumeration value. */
public class EnumItem implements ProxyObject {
    /** Name of the enumeration item. */
    @JsonProperty("name")
    public final Name name;

    /** Integer value of the enumeration item. */
    @JsonProperty("value")
    public final int value;

    /** Documentation of the enumeration item. */
    @JsonProperty("doc")
    public final String doc;

    @JsonCreator
    public EnumItem(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "value", required = true) int value,
            @JsonProperty(value = "doc", required = true) String doc) {
        this.name = name;
        this.value = value;
        this.doc = doc;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
