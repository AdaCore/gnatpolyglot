//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.List;

public class UpcallReturnConverter {

    private class JavaReturnWorker implements JavaTypeWorker<String> {

        private String returnedValue;
        private FunctionTypeExpr functionType;

        public JavaReturnWorker(FunctionTypeExpr functionType, String returnedValue) {
            this.functionType = functionType;
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
                    .append(JavaGenerator.makeMethodCall(returnedValue, "_release", List.of()))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(
                            JavaGenerator.makeMethodCall(
                                    JavaGenerator.makeMethodCall(
                                            returnedValue, "_release", List.of()),
                                    "getAddress",
                                    List.of()))
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

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder("return ").append(returnedValue).append(".value;").toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            boolean isArray = getContext().isStringOrArray(type.pointedType());
            // Lambda that sets the owner of the value to LIBRARY and returns the data.
            // This avoids the garbage collector from freeing the underlying data before it is
            // copied by the upcalling function.
            String lambda =
                    "$value -> {"
                            + JavaGenerator.makeMethodCall(
                                    "$value", "_setOwner", List.of(api.javaOwner(Owner.LIBRARY)))
                            + "; return "
                            + JavaGenerator.makeGetData("$value")
                            + (isArray ? "" : ".getAddress()")
                            + ";}";
            String defaultValue = isArray ? "null" : "0L";
            String javaOwner = api.javaOwner(functionType.returnOwner);
            StringBuilder builder =
                    new StringBuilder("if (")
                            .append(
                                    JavaGenerator.makeMethodCall(
                                            returnedValue,
                                            "map",
                                            List.of("$value-> $value._getOwner() != " + javaOwner)))
                            .append(".orElse(false)) {")
                            .append("throw ")
                            .append(
                                    JavaGenerator.makeNew(
                                            "com.adacore.gnatpolyglot.runtime.ada2java.ConstraintError",
                                            List.of(
                                                    JavaGenerator.makeNew(
                                                            "com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString",
                                                            List.of(
                                                                    "\"return value owner should be"
                                                                            + " "
                                                                            + javaOwner
                                                                            + "\"")))))
                            .append(";}\n");
            builder.append("return ")
                    .append(JavaGenerator.makeMethodCall(returnedValue, "map", List.of(lambda)));
            if (type.isNonNull()) {
                builder.append(
                        ".orElseThrow(() -> "
                                + JavaGenerator.makeNew(
                                        "com.adacore.gnatpolyglot.runtime.ada2java.ConstraintError",
                                        List.of(
                                                JavaGenerator.makeNew(
                                                        "com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString",
                                                        List.of(
                                                                "\"returning null value is not"
                                                                        + " allowed\""))))
                                + ")");
            } else {
                builder.append(".orElse(").append(defaultValue).append(")");
            }
            return builder.append(";").toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Returning references to function types in upcalls is not supported");
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

        @Override
        public String enumType(TypeExpr type) {
            return "return 0;";
        }

        @Override
        public String pointerType(TypeExpr type) {
            if (getContext().isStringOrArray(type.pointedType())) return "return null;";
            return "return 0L;";
        }

        @Override
        public String functionType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Returning references to function types in upcalls is not supported");
        }
    }

    private class CReturnWorker implements JavaTypeWorker<String> {

        private String returnedValue;
        private String cReturnType;

        public CReturnWorker(FunctionTypeExpr functionType, String returnedValue) {
            this.returnedValue = returnedValue;
            this.cReturnType = api.cTypename(functionType.returnType);
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
        public String enumType(TypeExpr type) {
            return numberType(type);
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

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder = new StringBuilder("return ");
            if (getContext().isStringOrArray(type.pointedType())) {
                builder.append(
                        CGenerator.makeCall(
                                "gnatpolyglot_proxy2java_to_array_data",
                                List.of("env", returnedValue)));
            } else {
                builder.append(CGenerator.makeCast(cReturnType, returnedValue));
            }
            return builder.append(";").toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Returning references to function types in upcalls is not supported");
        }
    }

    private JavaAPI api;

    public UpcallReturnConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the return statement to return the value from the native function to the user. */
    public String javaReturnStatement(FunctionTypeExpr functionType, String returnedValue) {
        return new JavaReturnWorker(functionType, returnedValue).apply(functionType.returnType);
    }

    /**
     * Create the return statement to return a default "null" value from the native function to the
     * user.
     */
    public String javaDefaultReturnStatement(FunctionTypeExpr functionType) {
        return new JavaDefaultReturnWorker().apply(functionType.returnType);
    }

    /** Create the return statement to return the value from the bound function to the JVM. */
    public String cReturnStatement(FunctionTypeExpr functionType, String returnedValue) {
        return new CReturnWorker(functionType, returnedValue).apply(functionType.returnType);
    }
}
