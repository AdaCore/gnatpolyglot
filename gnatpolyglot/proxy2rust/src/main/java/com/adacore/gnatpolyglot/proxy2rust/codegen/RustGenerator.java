//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust.codegen;

/**
 * Builds fragments of generated Rust source. Centralizing the {@code std::} paths and the raw
 * pointer idioms here keeps the rest of the backend free of hard-coded code strings and gives a
 * single place to update should the generated shape change.
 */
public final class RustGenerator {

    private RustGenerator() {}

    /** The runtime support crate that generated bindings depend on. */
    public static final String RUNTIME_CRATE = "gnatpolyglot_runtime";

    /** A fully-qualified path into the runtime crate. */
    public static String runtimePath(String item) {
        return RUNTIME_CRATE + "::" + item;
    }

    /** A raw pointer to {@code pointee}: {@code *const}/{@code *mut <pointee>}. */
    public static String rawPtr(boolean isConst, String pointee) {
        return (isConst ? "*const " : "*mut ") + pointee;
    }

    /** The raw C void pointer type, {@code *const}/{@code *mut std::ffi::c_void}. */
    public static String cVoidPtr(boolean isConst) {
        return rawPtr(isConst, "std::ffi::c_void");
    }

    /** A Rust {@code as} cast: {@code <expr> as <type>}. */
    public static String cast(String expr, String type) {
        return expr + " as " + type;
    }

    /** A borrow of {@code inner}: {@code &<inner>} when const, else {@code &mut <inner>}. */
    public static String reference(boolean isConst, String inner) {
        return (isConst ? "&" : "&mut ") + inner;
    }

    /** A dereference expression: {@code *(<expr>)}. */
    public static String deref(String expr) {
        return "*(" + expr + ")";
    }

    /** Access the opaque pointer of a wrapper value: {@code <expr>.0.as_ptr()}. */
    public static String asPtr(String expr) {
        return expr + ".0.as_ptr()";
    }

    /** A call into the generated {@code ffi} module: {@code ffi::<symbol>(<args>)}. */
    public static String ffiCall(String symbol, String args) {
        return "ffi::" + symbol + "(" + args + ")";
    }

    /** Construct a wrapper struct around a raw pointer. */
    public static String wrapPointer(String structName, String rawPointer) {
        return structName + "(" + nonNullNewUnchecked(cast(rawPointer, "*mut _")) + ")";
    }

    /** {@code std::ptr::NonNull::new_unchecked(<expr>)}. */
    public static String nonNullNewUnchecked(String expr) {
        return "std::ptr::NonNull::new_unchecked(" + expr + ")";
    }

    /** {@code std::mem::ManuallyDrop::new(<expr>)}, keeping a value alive past its scope. */
    public static String manuallyDropNew(String expr) {
        return "std::mem::ManuallyDrop::new(" + expr + ")";
    }

    /** The {@code std::mem::ManuallyDrop<T>} type for a non-owned value. */
    public static String manuallyDropType(String type) {
        return "std::mem::ManuallyDrop<" + type + ">";
    }

    /**
     * Reinterpret a value of {@code fromType} as {@code toType} via {@code std::mem::transmute}.
     */
    public static String transmute(String fromType, String toType, String expr) {
        return "std::mem::transmute::<" + fromType + ", " + toType + ">(" + expr + ")";
    }
}
