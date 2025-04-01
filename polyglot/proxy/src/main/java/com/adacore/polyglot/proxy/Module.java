package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent modules in the Proxy IR */
public class Module implements ProxyObject {
    /** Name of the module. */
    @JsonProperty("name")
    public final FullyQualifiedName name;

    /** List of declarations contained in the module. */
    @JsonProperty("declarations")
    public final List<Declaration> declarations;

    @JsonCreator
    public Module(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "declarations", required = true) List<Declaration> declarations) {
        this.name = name;
        this.declarations = declarations;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
