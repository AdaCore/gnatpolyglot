package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;

public class TypenameGenerator {

    /** Type worker that creates the Java typename, generally exposed to the user. */
    private class JavaTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.javaPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
        }
    }

    /** Type worker that creates the Java native typename used to declare Java native functions. */
    private class JavaNativeTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.javaPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
        }
    }

    /** Type worker that creates the JNI typenames in the C layer. */
    private class JNITypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.jniPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
        }
    }

    /**
     * Type worker that creates the C typename, those are used in the calls to the C symbol of
     * functions.
     */
    private class CTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.cPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
        }
    }

    private JavaAPI api;

    public TypenameGenerator(JavaAPI api) {
        this.api = api;
    }

    /** Return the exposed Java typename of type. */
    public String javaTypename(TypeExpr type) {
        return new JavaTypenameWorker().apply(type);
    }

    /** Return the Java native typename of type. */
    public String javaNativeTypename(TypeExpr type) {
        return new JavaNativeTypenameWorker().apply(type);
    }

    /** Return the JNI typename of type. */
    public String jniTypename(TypeExpr type) {
        return new JNITypenameWorker().apply(type);
    }

    /** Return the C typename of type. */
    public String cTypename(TypeExpr type) {
        return new CTypenameWorker().apply(type);
    }
}
