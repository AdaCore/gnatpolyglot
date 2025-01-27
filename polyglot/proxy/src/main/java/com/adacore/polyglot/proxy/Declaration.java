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
})
public abstract class Declaration implements ProxyObject {
    /** Name of the declaration. */
    @JsonProperty("name")
    public final Name name;

    /** Documentation of the declaration. */
    @JsonProperty("doc")
    public final String doc;

    public Declaration(Name name, String doc) {
        this.name = name;
        this.doc = doc;
    }
}
