package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent function declarations. */
public class FunctionDecl extends Declaration {

    public enum Visibility {
        @JsonProperty("public")
        PUBLIC,
        @JsonProperty("protected")
        PROTECTED,
    }

    public enum Overridability {
        @JsonProperty("final")
        FINAL,
        @JsonProperty("overridable")
        OVERRIDABLE,
        @JsonProperty("abstract")
        ABSTRACT,
    }

    public enum Staticness {
        @JsonProperty("static")
        STATIC,
        @JsonProperty("non_static")
        NON_STATIC,
    }

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

    /** When attached to a type, whether this function is visible outside of the type's scope. */
    @JsonProperty("is_visible")
    public final Visibility visibility;

    /** When attached to a type, whether this function can be overriden. */
    @JsonProperty("is_final")
    public final Overridability overridability;

    /** When attached to a type, whether this function is static. */
    @JsonProperty("is_static")
    public final Staticness staticness;

    @JsonCreator
    public FunctionDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "role") Role role,
            @JsonProperty(value = "symbol") String symbol,
            @JsonProperty(value = "type", required = true) FunctionTypeExpr type,
            @JsonProperty(value = "is_visible") Visibility visibility,
            @JsonProperty(value = "is_final") Overridability overridability,
            @JsonProperty(value = "is_static") Staticness staticness) {
        super(name, doc);
        this.role = role;
        this.symbol = symbol;
        this.type = type;

        this.visibility = visibility;
        this.overridability = overridability;
        this.staticness = staticness;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
