package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent function declarations. */
public class FunctionDecl extends Declaration {
    /** The role of the function. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("role")
    public final Role role;

    /** Corresponding C symbol the function should call. */
    @JsonProperty("symbol")
    public final String symbol;

    /** List of parameters taken by the function. */
    @JsonProperty("type")
    public final FunctionTypeExpr type;

    /** When attached to a type, whether this function is visible outside of the type's scope.. */
    @JsonProperty("is_visible")
    public final Boolean isVisible;

    /** When attached to a type, whether this function can be overriden.. */
    @JsonProperty("is_final")
    public final Boolean isFinal;

    /** When attached to a type, whether this function is static.. */
    @JsonProperty("is_static")
    public final Boolean isStatic;

    @JsonCreator
    public FunctionDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "role") Role role,
            @JsonProperty(value = "symbol", required = true) String symbol,
            @JsonProperty(value = "type", required = true) FunctionTypeExpr type,
            @JsonProperty(value = "is_visible") Boolean isVisible,
            @JsonProperty(value = "is_final") Boolean isFinal,
            @JsonProperty(value = "is_static") Boolean isStatic) {
        super(name, doc);
        this.role = role;
        this.symbol = symbol;
        this.type = type;

        this.isVisible = isVisible;
        this.isFinal = isFinal;
        this.isStatic = isStatic;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
