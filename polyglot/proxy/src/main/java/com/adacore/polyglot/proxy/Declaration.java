//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Abstract base class for declarations. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EnumerationDecl.class, name = "enum"),
    @JsonSubTypes.Type(value = FunctionDecl.class, name = "function"),
    @JsonSubTypes.Type(value = ClassDecl.class, name = "class"),
    @JsonSubTypes.Type(value = ExceptionDecl.class, name = "exception"),
})
public abstract class Declaration implements ProxyObject {
    /** Name of the declaration. */
    @JsonProperty("name")
    public final FullyQualifiedName name;

    /** Documentation of the declaration. */
    @JsonProperty("doc")
    public final String doc;

    public Declaration(FullyQualifiedName name, String doc) {
        this.name = name;
        this.doc = doc;
    }

    public Name getLastName() {
        return name.getLastName();
    }
}
