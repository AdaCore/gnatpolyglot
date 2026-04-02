//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent enumeration type declarations. */
public class EnumerationDecl extends TypeDecl {
    /** List of all enumeration items. */
    @JsonProperty("items")
    public final List<EnumItem> items;

    @JsonCreator
    public EnumerationDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "items", required = true) List<EnumItem> items) {
        super(name, doc);
        this.items = items;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
