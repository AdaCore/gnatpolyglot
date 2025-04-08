package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent class types declaration. */
public class ClassDecl extends TypeDecl {
    /** Parent class type. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("parent")
    public final FullyQualifiedName parent;

    /** Size in bits of the type. */
    @JsonProperty("size")
    public final int size;

    /** Whether this class can be derived. */
    @JsonProperty("is_final")
    public final boolean isFinal;

    /** List of all fields contained by this class. */
    @JsonProperty("fields")
    public final List<Field> fields;

    @JsonCreator
    public ClassDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "parent") FullyQualifiedName parent,
            @JsonProperty(value = "size", required = true) int size,
            @JsonProperty(value = "is_final", required = true) boolean isFinal,
            @JsonProperty(value = "fields", required = true) List<Field> fields) {
        super(name, doc);
        this.parent = parent;
        this.size = size;
        this.isFinal = isFinal;
        this.fields = fields;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
