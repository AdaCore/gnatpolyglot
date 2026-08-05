//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

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

    /**
     * Symbol of the identificator function. Used to instantiate a value of the correct type when
     * returning an object.
     *
     * <p>The function accepts as arguments the pointer to the data to identify, and a pointer to an
     * address buffer. The function returns an integer equal to the {@code id} field of the matched
     * ClassDecl, or -1 if the object is a shadow object. In the latter case, the buffer at the
     * given address is set to the back reference of the shadow object
     */
    @JsonProperty("identificator")
    public final String identificator;

    /** Value returned by the identificator function to identify this type. */
    @JsonProperty("id")
    public final Integer id;

    /**
     * Symbol of the function to set the owner of the back reference in shadow objects.
     *
     * <p>The function accepts as arguments the pointer to the data and a 0 (LIBRARY) or 1 (USER)
     * value integer. The function does not return a value.
     */
    @JsonProperty("owner_setter")
    public final String ownerSetter;

    /**
     * Symbol of the function to update the back reference in shadow objects. It is not necessary
     * that every printer uses this field unless the back reference dynamic type has any effect
     * (e.g: Java JNI GlobalRef vs WeakGlobalRef).
     *
     * <p>The function accepts as arguments the pointer tot the data and an address equal to the new
     * reference. The function returns the value of the old reference.
     */
    @JsonProperty("back_ref_updater")
    public final String backRefUpdater;

    @JsonCreator
    public ClassDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "parent") FullyQualifiedName parent,
            @JsonProperty(value = "size", required = true) int size,
            @JsonProperty(value = "inheritability", required = true) Inheritability inheritability,
            @JsonProperty(value = "fields", required = true) List<Field> fields,
            @JsonProperty(value = "vtable") List<VTableEntry> vtable,
            @JsonProperty(value = "identificator") String identificator,
            @JsonProperty(value = "id") Integer id,
            @JsonProperty(value = "ownerSetter") String ownserSetter,
            @JsonProperty(value = "back_ref_updater") String backRefUpdater) {
        super(name, doc);
        this.parent = parent;
        this.size = size;
        this.inheritability = inheritability;
        this.fields = fields;
        this.vtable = vtable;
        this.identificator = identificator;
        this.id = id;
        this.ownerSetter = ownserSetter;
        this.backRefUpdater = backRefUpdater;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
