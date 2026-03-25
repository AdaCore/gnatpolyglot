package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;

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

            this.javaReturnType = api.javaTypename(functionDecl.type.returnType);
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
        public String voidType(TypeExpr type) {
            return "return;";
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

            this.jniReturnType = api.jniTypename(functionDecl.type.returnType);
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
        public String voidType(TypeExpr type) {
            return "return;";
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
