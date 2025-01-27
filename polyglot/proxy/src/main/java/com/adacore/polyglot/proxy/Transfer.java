package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Contain information about memory ownership for function parameters. */
public class Transfer implements ProxyObject {

    public enum RequiredOwner {
        /** The owner of the memory must be the user. */
        @JsonProperty("user")
        USER,
        /** The owner of the memory must be the library. */
        @JsonProperty("library")
        LIBRARY,
        /** The owner of the memory can be anything. */
        @JsonProperty("any")
        ANY,
    }

    /** Required owner of the memory before passing the argument. */
    @JsonProperty("required_owner")
    public final RequiredOwner required_owner;

    @JsonCreator
    public Transfer(
            @JsonProperty(value = "required_owner", required = true) RequiredOwner required_owner) {
        this.required_owner = required_owner;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
