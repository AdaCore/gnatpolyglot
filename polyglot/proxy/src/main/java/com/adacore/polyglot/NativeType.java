package com.adacore.polyglot;

import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.ProxyVisitor;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import com.adacore.polyglot.proxy.TypeDecl;

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

    /** Type to represent a native type declaration. */
    public static class NativeTypeDecl extends TypeDecl {
        private NativeTypeDecl(Name name) {
            super(name, null);
        }

        @Override
        public <T> T visit(ProxyVisitor<T> v) {
            return null;
        }
    }

    /** Default reference to a native type. */
    public final Reference reference;

    /** Declaration object of the native type. */
    public final NativeTypeDecl declaration;

    private NativeType() {
        this.reference =
                new Reference(
                        Name.fromLower(this.toString().toLowerCase()),
                        ReferenceKind.SCALAR,
                        null,
                        false,
                        false,
                        false);
        this.declaration = new NativeTypeDecl(Name.fromLower(this.toString().toLowerCase()));
    }
}
