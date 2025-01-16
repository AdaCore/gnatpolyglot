package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Describe the role of a function. */
public class Role implements ProxyObject {

    /** Represent the different roles a function can occupy. */
    public enum RoleKind {
        /** The function should be a member of {@link Role#type}. */
        @JsonProperty("method")
        METHOD,
        /** The function returns the value of {@link Role#field}. */
        @JsonProperty("getter")
        GETTER,
        /** The function sets the value of {@link Role#field}. */
        @JsonProperty("setter")
        SETTER,
        /** The function allocates a new instance of type {@link Role#type}. */
        @JsonProperty("alloc")
        ALLOC,
        /** The function initializes the field of an instance of type {@link Role#type}. */
        @JsonProperty("construct")
        CONSTRUCT,
        /** The function frees an instance of type {@link Role#type}. */
        @JsonProperty("free")
        FREE,
        /** The function destructs an instance of type {@link Role#type}. */
        @JsonProperty("destruct")
        DESTRUCT,
    }

    /** The kind of role. */
    @JsonProperty("kind")
    public final RoleKind kind;

    /** The type the function should be a member of. */
    @JsonProperty("type")
    public final Reference type;

    /**
     * When the role is {@link RoleKind#GETTER} or {@link RoleKind#SETTER}, the field of type it is
     * attached to.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("field")
    public final Name field;

    @JsonCreator
    public Role(
            @JsonProperty(value = "kind", required = true) RoleKind kind,
            @JsonProperty(value = "type", required = true) Reference type,
            @JsonProperty(value = "field") Name field) {
        this.kind = kind;
        this.type = type;
        this.field = field;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
