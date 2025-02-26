package com.adacore.polyglot;

import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;

/** Enumeration of common native types */
public enum NativeType {
    VOID,
    BOOL,
    STRING,
    SINT8,
    SINT16,
    SINT32,
    SINT64,
    SINT128,
    UINT8,
    UINT16,
    UINT32,
    UINT64,
    UINT128,
    FLOAT32,
    FLOAT64,
    FLOAT128;

    public final Reference reference;

    private NativeType() {
        this.reference =
                new Reference(
                        Name.fromLower(this.toString().toLowerCase()),
                        ReferenceKind.SCALAR,
                        null,
                        false,
                        false,
                        false);
    }
}
