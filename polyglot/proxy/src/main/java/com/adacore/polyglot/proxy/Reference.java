package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Reference to a type or module. */
public class Reference implements ProxyObject {

    /** The prefix of the reference, if any. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("name")
    public final FullyQualifiedName name;

    /** If the refered instance is a {@link ReferenceKind#CLASS}, whether the type is a pointer. */
    @JsonProperty("is_pointer")
    public final boolean isPointer;

    /** If the refered instance is a {@link ReferenceKind#CLASS}, whether the type is constant. */
    @JsonProperty("is_const")
    public final boolean isConst;

    /**
     * If the refered instance is a {@link ReferenceKind#CLASS}, whether the type is a reference.
     */
    @JsonProperty("is_reference")
    public final boolean isReference;

    /**
     * If the refered instance is a pointer to a {@link ReferenceKind#CLASS}, whether it can be
     * null.
     */
    @JsonProperty("is_nonnull")
    public final boolean isNonNull;

    @JsonCreator
    public Reference(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "is_pointer") boolean isPointer,
            @JsonProperty(value = "is_const") boolean isConst,
            @JsonProperty(value = "is_nonnull") boolean isNonNull,
            @JsonProperty(value = "is_reference") boolean isReference) {
        this.name = name;
        this.isPointer = isPointer;
        this.isConst = isConst;
        this.isNonNull = isNonNull;
        this.isReference = isReference;
    }

    /** Return a copy of the reference and modify its isPointer attribute. */
    public Reference withIsPointer(boolean newIsPointer) {
        return new Reference(
                this.name, newIsPointer, this.isConst, this.isNonNull, this.isReference);
    }

    /** Return a copy of the reference and change its isConst attribute. */
    public Reference withIsConst(boolean newIsConst) {
        return new Reference(
                this.name, this.isPointer, newIsConst, this.isNonNull, this.isReference);
    }

    /** Return a copy of the reference and modify isNonNull attribute. */
    public Reference withIsNonNull(boolean newIsNonNull) {
        return new Reference(
                this.name, this.isPointer, this.isConst, newIsNonNull, this.isReference);
    }

    /** Return a copy of the reference and modify isReference attribute. */
    public Reference withIsReference(boolean newIsReference) {
        return new Reference(
                this.name, this.isPointer, this.isConst, this.isNonNull, newIsReference);
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Reference other) {
            return name.equals(other.name)
                    && this.name.equals(other.name)
                    && this.isConst == other.isConst
                    && this.isPointer == other.isPointer
                    && this.isNonNull == other.isNonNull
                    && this.isReference == other.isReference;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + name.hashCode();
        hash = 31 * hash + Boolean.hashCode(isPointer);
        hash = 31 * hash + Boolean.hashCode(isConst);
        hash = 31 * hash + Boolean.hashCode(isNonNull);
        hash = 31 * hash + Boolean.hashCode(isReference);
        return hash;
    }
}
