package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent modules in the Proxy IR */
public class Module implements ProxyObject {
    /** Name of the module. */
    @JsonProperty("name")
    public final Name name;

    /** List of declarations contained in the module. */
    @JsonProperty("declarations")
    public final List<Declaration> declarations;

    /** Parent module. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("parent")
    public final Reference parent;

    @JsonCreator
    public Module(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "declarations", required = true) List<Declaration> declarations,
            @JsonProperty(value = "parent") Reference parent) {
        this.name = name;
        this.declarations = declarations;
        this.parent = parent;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
