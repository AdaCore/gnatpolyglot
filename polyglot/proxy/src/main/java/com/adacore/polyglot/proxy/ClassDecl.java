package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent class types declaration. */
public class ClassDecl extends TypeDecl {

    public enum Inheritability {
        @JsonProperty("final")
        FINAL,
        @JsonProperty("inheritable")
        INHERITABLE,
        @JsonProperty("virtual")
        VIRTUAL,
    }

    /** Parent class type. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("parent")
    public final FullyQualifiedName parent;

    /** Size in bits of the type. */
    @JsonProperty("size")
    public final int size;

    /** Whether this class can be derived. */
    @JsonProperty("inheritability")
    public final Inheritability inheritability;

    /** List of all fields contained by this class. */
    @JsonProperty("fields")
    public final List<Field> fields;

    /** vtable. */
    @JsonProperty("vtable")
    public final List<VTableEntry> vtable;

    @JsonCreator
    public ClassDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "parent") FullyQualifiedName parent,
            @JsonProperty(value = "size", required = true) int size,
            @JsonProperty(value = "inheritability", required = true) Inheritability inheritability,
            @JsonProperty(value = "fields", required = true) List<Field> fields,
            @JsonProperty(value = "vtable") List<VTableEntry> vtable) {
        super(name, doc);
        this.parent = parent;
        this.size = size;
        this.inheritability = inheritability;
        this.fields = fields;
        this.vtable = vtable;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
