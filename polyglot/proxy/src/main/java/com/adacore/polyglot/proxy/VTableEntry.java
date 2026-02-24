//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VTableEntry implements ProxyObject {

    /** Name of the entry. */
    @JsonProperty("name")
    public final Name name;

    /** Function type of the entry. */
    @JsonProperty("function_type")
    public final FunctionTypeExpr functionType;

    @JsonCreator
    public VTableEntry(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "function_type", required = true) FunctionTypeExpr functionType) {
        this.name = name;
        this.functionType = functionType;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
