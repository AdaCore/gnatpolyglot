//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Transfer.RequiredOwner;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker.PointerTypeWorker;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;

public class ParameterGenerator {

    private StringBuilder getParamBuffer(String name) {
        return new StringBuilder("_").append(name).append("_data");
    }

    private StringBuilder getParamCopy(String name) {
        return new StringBuilder("__").append(name).append("_data");
    }

    private class PointerBufferWorker implements PointerTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String dataType;
        private FunctionDecl functionDecl;

        public PointerBufferWorker(Parameter param, FunctionDecl functionDecl) {
            this.param = param;
            this.functionDecl = functionDecl;
            this.argName = api.toLower(param.name);
            this.dataType = api.cTypename(param.type.referencedType());
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String pointerType(TypeExpr type) {
            return new StringBuilder(dataType)
                    .append(" ")
                    .append(getParamBuffer(argName))
                    .append(" = ")
                    .append(
                            CppGenerator.makeTernary(
                                    argName.concat(".get() == nullptr"),
                                    api.nullValue((PointerTypeExpr) type),
                                    argName.concat(".get()->data_()")))
                    .append(";\n")
                    .toString();
        }

        @Override
        public String refType(TypeExpr type) {
            StringBuilder builder = new StringBuilder(apply(type.referencedType()));
            if (!type.isConst()) {
                // When the type is a non cv-reference, create a copy of the internal data to
                // compare it after
                // the call as it may have been modified.
                builder.append(dataType)
                        .append(" ")
                        .append(getParamCopy(argName))
                        .append(" = ")
                        .append(getParamBuffer(argName))
                        .append(";\n");
            }
            return builder.toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            return new StringBuilder(api.cTypename(type.referencedType()))
                    .append(" ")
                    .append(getParamBuffer(argName))
                    .append(" = ")
                    .append("{")
                    .append(
                            CppGenerator.makeReinterpretCast(
                                    "void *", "&" + api.getCallbackName(param, functionDecl)))
                    .append(", ")
                    .append(CppGenerator.makeStaticCast("void *", "&" + argName))
                    .append(", nullptr};")
                    .toString();
        }

        @Override
        public String defaultCase() {
            return "";
        }
    }

    private class ConversionWorker implements CppTypeWorker<String> {

        private Parameter param;
        private String argName;
        private FunctionDecl functionDecl;

        public ConversionWorker(Parameter param, FunctionDecl functionDecl) {
            this.param = param;
            this.functionDecl = functionDecl;
            this.argName = api.toLower(param.name);
        }

        @Override
        public String numberType(TypeExpr type) {
            return CppGenerator.makeStaticCast(api.cTypename(type), argName).toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return CppGenerator.getData(argName).toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return CppGenerator.getData(argName).toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            return getParamBuffer(argName).toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            throw new IllegalArgumentException("Unreachable: parameter of void type");
        }

        @Override
        public String refType(TypeExpr type) {
            if (type.isConst()) return apply(type.referencedType());
            return new CppTypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public String numberType(TypeExpr type) {
                    return new StringBuilder("&").append(argName).toString();
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return CppGenerator.getData(argName).toString();
                }

                @Override
                public String classType(TypeExpr type) {
                    return CppGenerator.getData(argName).toString();
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return new StringBuilder("&").append(getParamBuffer(argName)).toString();
                }

                @Override
                public String functionType(TypeExpr type) {
                    return pointerType(type);
                }

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }
            }.apply(type.referencedType());
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String functionType(TypeExpr type) {
            return getParamBuffer(argName).toString();
        }
    }

    private CppAPI api;

    public ParameterGenerator(CppAPI api) {
        this.api = api;
    }

    public String buildPointerBuffer(Parameter param, FunctionDecl functionDecl) {
        return new PointerBufferWorker(param, functionDecl).apply(param.type);
    }

    public String buildConversion(Parameter param, FunctionDecl functionDecl) {
        return new ConversionWorker(param, functionDecl).apply(param.type);
    }

    public String buildPointerValueUpdate(Parameter param) {
        return new PointerTypeWorker<String>() {

            @Override
            public ProxyContext getContext() {
                return api.getContext();
            }

            @Override
            public String pointerType(TypeExpr type) {
                return defaultCase();
            }

            private String objectRef(TypeExpr type) {
                if (type.isConst()) return defaultCase();
                TypeExpr pointedType = type.referencedType().pointedType();
                String argName = api.toLower(param.name);
                CharSequence dataName = getParamBuffer(argName);
                CharSequence copyName = getParamCopy(argName);
                String addressAccessor = getContext().isStringOrArray(pointedType) ? ".data" : "";
                return new StringBuilder("if (")
                        .append(dataName)
                        .append(addressAccessor)
                        .append(" != ")
                        .append(copyName)
                        .append(addressAccessor)
                        .append(") ")
                        .append(argName)
                        .append(".reset(")
                        .append(dataName)
                        .append(addressAccessor)
                        .append("== nullptr ? nullptr : new ")
                        .append(api.cppTypename(pointedType))
                        .append("(")
                        .append(dataName)
                        .append("), gnatpolyglot::memory_owner::LIBRARY);")
                        .toString();
            }

            private String functionRef(TypeExpr type) {
                if (type.isConst()) return defaultCase();
                String argName = api.toLower(param.name);
                return new StringBuilder("if (")
                        .append(getParamBuffer(argName))
                        .append(".data != ")
                        .append(getParamCopy(argName))
                        .append(".data) {")
                        .append(argName)
                        .append(" = ")
                        .append(
                                api.buildUpcallLambda(
                                        (FunctionTypeExpr) type.referencedType(),
                                        getParamBuffer(argName).toString()))
                        .append("; }")
                        .toString();
            }

            @Override
            public String refType(TypeExpr type) {
                return type.referencedType().isFunction() ? functionRef(type) : objectRef(type);
            }

            @Override
            public String defaultCase() {
                return "";
            }
        }.apply(param.type);
    }

    public String buildPointerOwnershipCheck(Parameter param) {
        return new PointerTypeWorker<String>() {

            @Override
            public ProxyContext getContext() {
                return api.getContext();
            }

            @Override
            public String pointerType(TypeExpr type) {
                if (param.transfer.required_owner == RequiredOwner.ANY) return defaultCase();
                String lower = api.toLower(param.name);
                return new StringBuilder("if (")
                        .append(lower)
                        .append(".get_owner() < gnatpolyglot::memory_owner::")
                        .append(param.transfer.required_owner.toString())
                        .append(") throw gnatpolyglot::ada::exceptions::ConstraintError(\"")
                        .append(lower)
                        .append(": owner should be ")
                        .append(param.transfer.required_owner)
                        .append("\");")
                        .toString();
            }

            @Override
            public String refType(TypeExpr type) {
                return apply(type.referencedType());
            }

            @Override
            public String defaultCase() {
                return "";
            }
        }.apply(param.type);
    }
}
