package com.adacore.polyglot;

import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;

/** Enumeration of common native types */
public enum NativeType {
    VOID,
    BOOL;

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
