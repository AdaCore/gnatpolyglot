package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Reference to a type or module with a possible suffix. */
public class Reference implements ProxyObject {
    /** Represent the different kind of element that can be referenced. */
    public enum ReferenceKind {
        @JsonProperty("module")
        MODULE,
        @JsonProperty("class")
        CLASS,
        @JsonProperty("scalar")
        SCALAR,
    }

    /** Name of the refered type or module. */
    @JsonProperty("name")
    public final Name name;

    /** The kind of the refered instance. */
    @JsonProperty("kind")
    public final ReferenceKind kind;

    /** The suffix of the reference, if any. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("suffix")
    public final Reference suffix;

    /** If the refered instance is a {@link ReferenceKind#CLASS}, whether the type is a pointer. */
    @JsonProperty("is_pointer")
    public final boolean isPointer;

    /** If the refered instance is a {@link ReferenceKind#CLASS}, whether the type is constant. */
    @JsonProperty("is_const")
    public final boolean isConst;

    /**
     * If the refered instance is a pointer to a {@link ReferenceKind#CLASS}, whether it can be
     * null.
     */
    @JsonProperty("is_nonnull")
    public final boolean isNonNull;

    @JsonCreator
    public Reference(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "kind", required = true) ReferenceKind kind,
            @JsonProperty(value = "suffix") Reference suffix,
            @JsonProperty(value = "is_pointer") boolean isPointer,
            @JsonProperty(value = "is_const") boolean isConst,
            @JsonProperty(value = "is_nonnull") boolean isNonNull) {
        this.name = name;
        this.kind = kind;
        this.suffix = suffix;
        this.isPointer = isPointer;
        this.isConst = isConst;
        this.isNonNull = isNonNull;
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
