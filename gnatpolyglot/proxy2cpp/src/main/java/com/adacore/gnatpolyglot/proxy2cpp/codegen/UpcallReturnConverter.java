//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;

public class UpcallReturnConverter {

    private class ReturnWorker implements CppTypeWorker<String> {

        private FunctionTypeExpr functionType;
        private String returnedValue;

        public ReturnWorker(FunctionTypeExpr functionType, String returnedValue) {
            this.functionType = functionType;
            this.returnedValue = returnedValue;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeStaticCast(api.cTypename(type), returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(".release_();")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(".release_();")
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder =
                    new StringBuilder("if (").append(returnedValue).append(".get() == nullptr) {");
            if (type.isNonNull()) {
                builder.append(
                        "throw gnatpolyglot::ada::exceptions::ConstraintError(\"returning null"
                                + " value is not allowed\");");
            } else {
                builder.append(api.makeDispatchDefaultReturn(functionType));
            }
            return builder.append("} else if (")
                    .append(returnedValue)
                    .append(".get_owner() != ")
                    .append(api.cppOwner(functionType.returnOwner))
                    .append(") { ")
                    .append(
                            "throw gnatpolyglot::ada::exceptions::ConstraintError(\"return value"
                                    + " owner should be ")
                    .append(api.cppOwner(functionType.returnOwner))
                    .append("\");\n")
                    .append("} else { ")
                    .append(returnedValue)
                    .append(".set_owner(")
                    .append(api.cppOwner(Owner.LIBRARY))
                    .append(");\n")
                    .append("return ")
                    .append(returnedValue)
                    .append(".get()->data_() ;}")
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
        public String functionType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Unreachable: returning functions in dispatch is not permitted");
        }
    }

    private class DefaultReturnWorker implements CppTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return "return 0;";
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "return gnatpolyglot::ada::arrays::array_data{1, 0, nullptr};";
        }

        @Override
        public String stringType(TypeExpr type) {
            return "return gnatpolyglot::ada::strings::string_data{1, 0, nullptr};";
        }

        @Override
        public String classType(TypeExpr type) {
            return "return nullptr;";
        }

        @Override
        public String pointerType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(api.nullValue((PointerTypeExpr) type))
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
        public String functionType(TypeExpr type) {
            throw new UnsupportedOperationException(
                    "Unreachable: returning functions in dispatch is not permitted");
        }
    }

    CppAPI api;

    public UpcallReturnConverter(CppAPI api) {
        this.api = api;
    }

    public String buildReturn(FunctionTypeExpr functionType, String returnedValue) {
        return new ReturnWorker(functionType, returnedValue).apply(functionType.returnType);
    }

    public String buildDefaultReturn(FunctionTypeExpr functionType) {
        return new DefaultReturnWorker().apply(functionType.returnType);
    }
}
