package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.ArrayList;
import java.util.List;

public class ReturnConverter {

    /**
     * Type worker that creates the conversion of return values from Java native functions to the
     * user.
     */
    private class JavaReturnWorker implements JavaTypeWorker<String> {

        private FunctionDecl functionDecl;
        private String returnedValue;

        public String javaReturnType;

        public JavaReturnWorker(FunctionDecl functionDecl, String returnedValue) {
            this.functionDecl = functionDecl;
            this.returnedValue = returnedValue;

            this.javaReturnType = api.javaReturnTypename(functionDecl.type.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ").append(returnedValue).append(";").toString();
        }

        @Override
        public String classType(TypeExpr type) {
            List<CharSequence> args =
                    new ArrayList<>(
                            List.of(
                                    JavaGenerator.makeNew(
                                            "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                                            List.of(
                                                    returnedValue,
                                                    api.javaOwner(
                                                            functionDecl.type.returnOwner)))));
            // When a getter returns a reference, it implies that the returned object will reference
            // data from a parent structure. In case the parent would become unreferenced before its
            // field, the returned object must hold a reference to its eldest parent object in order
            // to avoid referencing freed memory.
            //
            // By default, `this.parent` is equal to `this`, so we do not need to check which object
            // to refer. This allows the value returned by `a.getB().getC()` to hold a reference to
            // `a`.
            if (api.isMethod(functionDecl)
                    && functionDecl.role.kind == RoleKind.GETTER
                    && functionDecl.type.returnOwner == Owner.STATIC
                    && functionDecl.type.returnType.isReference()) args.add("this.parent");
            return new StringBuilder("return ")
                    .append(JavaGenerator.makeNew(javaReturnType, args))
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "return;";
        }

        @Override
        public String refType(TypeExpr type) {
            return apply(type.referencedType());
        }
    }

    /** Type worker that creates the conversion of return values from bound functions to the JVM. */
    private class CReturnWorker implements JavaTypeWorker<String> {

        private FunctionDecl functionDecl;
        private String returnedValue;
        private String jniReturnType;

        public CReturnWorker(FunctionDecl functionDecl, String returnedValue) {
            this.functionDecl = functionDecl;
            this.returnedValue = returnedValue;

            this.jniReturnType = api.jniReturnTypename(functionDecl.type.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(jniReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(jniReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "return;";
        }

        @Override
        public String refType(TypeExpr type) {
            return new JavaTypeWorker.JavaSubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                @Override
                public String numberType(TypeExpr type) {
                    return new StringBuilder("return ")
                            .append(
                                    CGenerator.makeCast(
                                            jniReturnType, CGenerator.deref(returnedValue)))
                            .append(";")
                            .toString();
                }

                @Override
                public String classType(TypeExpr type) {
                    return CReturnWorker.this.classType(type);
                }
            }.apply(type.referencedType());
        }
    }

    private JavaAPI api;

    public ReturnConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the return statement to return the value from the native function to the user. */
    public String javaReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return new JavaReturnWorker(functionDecl, returnedValue)
                .apply(functionDecl.type.returnType);
    }

    /** Create the return statement to return the value from the bound function to the JVM. */
    public String cReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return new CReturnWorker(functionDecl, returnedValue).apply(functionDecl.type.returnType);
    }
}
