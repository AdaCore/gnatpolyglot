package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent a function parameter. */
public class Parameter implements ProxyObject {
    /** The name of the parameter. */
    @JsonProperty("name")
    public final Name name;

    /** The type of the parameter. */
    @JsonProperty("type")
    public final Reference type;

    /** Transfer informations. */
    @JsonProperty("transfer")
    public final Transfer transfer;

    @JsonCreator
    public Parameter(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "type", required = true) Reference type,
            @JsonProperty(value = "transfer", required = true) Transfer transfer) {
        this.name = name;
        this.type = type;
        this.transfer = transfer;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
