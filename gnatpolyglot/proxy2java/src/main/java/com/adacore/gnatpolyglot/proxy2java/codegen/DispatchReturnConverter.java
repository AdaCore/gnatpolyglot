package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.VTableEntry;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.List;

public class DispatchReturnConverter {

    private class JavaReturnWorker implements JavaTypeWorker<String> {

        private String returnedValue;

        public JavaReturnWorker(VTableEntry method, String returnedValue) {
            this.returnedValue = returnedValue;
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
        public String boolType(TypeExpr type) {
            return new StringBuilder("return ").append(returnedValue).append(";").toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(".")
                    .append(JavaGenerator.makeCall("release", List.of()))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(".")
                    .append(JavaGenerator.makeCall("release", List.of()))
                    .append(".getAddress()")
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "";
        }

        @Override
        public String refType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Unreachable: returning references in dispatch is not permitted");
        }
    }

    private class JavaDefaultReturnWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return "return 0;";
        }

        @Override
        public String boolType(TypeExpr type) {
            return "return false;";
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "return null;";
        }

        @Override
        public String classType(TypeExpr type) {
            return "return 0;";
        }

        @Override
        public String voidType(TypeExpr type) {
            return "";
        }

        @Override
        public String refType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Unreachable: returning references in dispatch is not permitted");
        }
    }

    private class CReturnWorker implements JavaTypeWorker<String> {

        private String returnedValue;
        private String cReturnType;

        public CReturnWorker(VTableEntry method, String returnedValue) {
            this.returnedValue = returnedValue;
            this.cReturnType = api.cTypename(method.functionType.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(cReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(" != JNI_FALSE")
                    .append(";")
                    .toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(
                            CGenerator.makeCall(
                                    "gnatpolyglot_proxy2java_to_array_data",
                                    List.of("env", returnedValue)))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(cReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "";
        }

        @Override
        public String refType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Unreachable: returning references in dispatch is not permitted");
        }
    }

    private JavaAPI api;

    public DispatchReturnConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the return statement to return the value from the native function to the user. */
    public String javaReturnStatement(VTableEntry method, String returnedValue) {
        return new JavaReturnWorker(method, returnedValue).apply(method.functionType.returnType);
    }

    /**
     * Create the return statement to return a default "null" value from the native function to the
     * user.
     */
    public String javaDefaultReturnStatement(FunctionTypeExpr functionType) {
        return new JavaDefaultReturnWorker().apply(functionType.returnType);
    }

    /** Create the return statement to return the value from the bound function to the JVM. */
    public String cReturnStatement(VTableEntry method, String returnedValue) {
        return new CReturnWorker(method, returnedValue).apply(method.functionType.returnType);
    }
}
