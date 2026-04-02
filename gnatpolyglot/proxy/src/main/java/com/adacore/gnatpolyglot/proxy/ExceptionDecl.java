//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionDecl extends TypeDecl {

    /**
     * Enumeration value to represent this exception's kind in the proxy.
     *
     * <p>The value must be unique in the proxy. The value `0` is reserved for anonymous exceptions.
     */
    @JsonProperty("enum_value")
    public final int enumValue;

    @JsonCreator
    public ExceptionDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "enum_value", required = true) int enumValue) {

        super(name, doc);
        this.enumValue = enumValue;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
