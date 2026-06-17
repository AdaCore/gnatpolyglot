//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.adacore.gnatpolyglot.NativeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represent enumeration type declarations. */
public class EnumerationDecl extends TypeDecl {
    /** List of all enumeration items. */
    @JsonProperty("items")
    public final List<EnumItem> items;

    @JsonCreator
    public EnumerationDecl(
            @JsonProperty(value = "name", required = true) FullyQualifiedName name,
            @JsonProperty(value = "doc", required = true) String doc,
            @JsonProperty(value = "items", required = true) List<EnumItem> items) {
        super(name, doc);
        this.items = items;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    /**
     * Return the signed native integer type used to represent this enumeration. The width is the
     * smallest that holds the largest item value, so language backends (e.g. proxy2cpp, proxy2rust)
     * agree on the ABI. This is the single source of truth for an enumeration's representation:
     * picking a wider type would corrupt adjacent memory when the enum is accessed through a
     * mutable reference across the FFI boundary. Assumes items are ordered by ascending value (as
     * produced by the scanner), so the last item carries the largest representation value.
     */
    public NativeType representationType() {
        long lastValue = items.getLast().value;
        if (lastValue < (1L << 8)) return NativeType.SINT8;
        if (lastValue < (1L << 16)) return NativeType.SINT16;
        if (lastValue < (1L << 32)) return NativeType.SINT32;
        return NativeType.SINT64;
    }
}
