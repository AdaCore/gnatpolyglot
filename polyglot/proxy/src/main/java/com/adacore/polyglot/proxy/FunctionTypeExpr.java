//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FunctionTypeExpr extends TypeExpr {

    /** List of parameters taken by the function. */
    @JsonProperty("parameters")
    public final List<Parameter> parameters;

    /** Type of the returned value. */
    @JsonProperty("return_type")
    public final TypeExpr returnType;

    /** Owner of the returned value. */
    @JsonProperty("return_owner")
    public final Owner returnOwner;

    @JsonCreator
    public FunctionTypeExpr(
            @JsonProperty(value = "parameters", required = true) List<Parameter> parameters,
            @JsonProperty(value = "return_type", required = true) TypeExpr returnType,
            @JsonProperty(value = "return_owner", required = true) Owner returnOwner) {
        this.parameters = parameters;
        this.returnType = returnType;
        this.returnOwner = returnOwner;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public FullyQualifiedName getName() {
        return null;
    }
}
