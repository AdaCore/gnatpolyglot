//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;
import java.util.List;

public class ReturnConverter {

    private class Worker implements CppTypeWorker<String> {

        private String returnedValue;
        private String returnTypename;
        private Owner returnOwner;

        public Worker(FunctionDecl functionDecl, String returnedValue) {
            this.returnedValue = returnedValue;
            this.returnTypename = api.cppTypename(functionDecl.type.returnType);
            this.returnOwner = functionDecl.type.returnOwner;
        }

        public Worker(FunctionTypeExpr functionType, String returnedValue) {
            this.returnedValue = returnedValue;
            this.returnTypename = api.cppTypename(functionType.returnType);
            this.returnOwner = functionType.returnOwner;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeStaticCast(returnTypename, returnedValue))
                    .toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeCall(returnTypename, List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeCall(returnTypename, List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            // Pointers to arrays are returned as polyglot_arrays. In order to check for null
            // pointers, we must access the `data` field of the struct.
            String arrayDataAccess =
                    getContext().isStringOrArray(type.pointedType()) ? ".data" : "";
            return new StringBuilder("return ")
                    .append(
                            CppGenerator.makeCall(
                                    returnTypename,
                                    List.of(
                                            CppGenerator.makeTernary(
                                                    new StringBuilder(returnedValue)
                                                            .append(arrayDataAccess)
                                                            .append(" == nullptr"),
                                                    "nullptr",
                                                    CppGenerator.makeNew(
                                                            api.cppTypename(type.pointedType()),
                                                            List.of(returnedValue))),
                                            api.cppOwner(returnOwner))))
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return returnedValue;
        }

        @Override
        public String refType(TypeExpr refType) {
            return new CppTypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                @Override
                public String numberType(TypeExpr type) {
                    return new StringBuilder("return *")
                            .append(
                                    CppGenerator.makeStaticCast(
                                            new StringBuilder(refType.isConst() ? "const " : "")
                                                    .append(api.cppTypename(type))
                                                    .append("*"),
                                            returnedValue))
                            .toString();
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return new StringBuilder("return ")
                            .append(CppGenerator.makeView(api.cppTypename(type), returnedValue))
                            .toString();
                }

                @Override
                public String classType(TypeExpr type) {
                    return new StringBuilder("return ")
                            .append(CppGenerator.makeView(api.cppTypename(type), returnedValue))
                            .toString();
                }

                @Override
                public String pointerType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to pointers are not supported");
                }

                @Override
                public String functionType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to functions are not supported");
                }
            }.apply(refType.referencedType());
        }

        @Override
        public String functionType(TypeExpr type) {
            FunctionTypeExpr functionType = (FunctionTypeExpr) type;
            return new StringBuilder("return ")
                    .append(api.buildUpcallLambda(functionType, returnedValue))
                    .toString();
        }
    }

    private CppAPI api;

    public ReturnConverter(CppAPI api) {
        this.api = api;
    }

    public String build(FunctionDecl functionDecl, String returnedValue) {
        return new Worker(functionDecl, returnedValue).apply(functionDecl.type.returnType);
    }

    public String build(FunctionTypeExpr functionType, String returnedValue) {
        return new Worker(functionType, returnedValue).apply(functionType.returnType);
    }
}
