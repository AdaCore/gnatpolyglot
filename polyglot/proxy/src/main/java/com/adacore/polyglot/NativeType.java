package com.adacore.polyglot;

import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.NameTypeExpr;
import com.adacore.polyglot.proxy.ProxyVisitor;
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
        public final NativeType nativeType;

        private NativeTypeDecl(FullyQualifiedName name, NativeType nativeType) {
            super(name, null);
            this.nativeType = nativeType;
        }

        @Override
        public <T> T visit(ProxyVisitor<T> v) {
            return null;
        }
    }

    /** Default TypeExpr to a native type. */
    public final NameTypeExpr typeExpr;

    /** Declaration object of the native type. */
    public final NativeTypeDecl declaration;

    private NativeType() {
        this.typeExpr =
                new NameTypeExpr(
                        new FullyQualifiedName(Name.fromLower(this.toString().toLowerCase())));
        this.declaration =
                new NativeTypeDecl(
                        new FullyQualifiedName(Name.fromLower(this.toString().toLowerCase())),
                        this);
    }
}
