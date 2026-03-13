//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;
import java.util.List;

public class DispatchParameterConverter {

    public static String getConverterValue(String name) {
        return "_".concat(name);
    }

    public static String getBufferValue(String name) {
        return "__".concat(name);
    }

    private class ConversionWorker implements CppTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String cppTypename;
        private String valueName;

        public ConversionWorker(Parameter param) {
            this.param = param;
            this.cppTypename = api.cppTypename(param.type);
            this.argName = api.toLower(param.name);
            this.valueName = getConverterValue(argName);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder(cppTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CppGenerator.makeStaticCast(cppTypename, argName))
                    .toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder(api.cppReturnTypename(type.makeReference(false)))
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CppGenerator.makeView(cppTypename, argName))
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder(api.cppReturnTypename(type.makeReference(false)))
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CppGenerator.makeView(cppTypename, argName))
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            return new StringBuilder(cppTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(
                            CppGenerator.makeCall(
                                    cppTypename,
                                    List.of(
                                            CppGenerator.makeNew(
                                                    api.cppTypename(type.pointedType()),
                                                    List.of(argName)))))
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "";
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
                    return new StringBuilder(cppTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(CppGenerator.deref(argName))
                            .toString();
                }

                @Override
                public String arrayType(TypeExpr type) {
                    cppTypename = api.cppTypename(type);
                    return ConversionWorker.this.apply(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    cppTypename = api.cppTypename(type);
                    return ConversionWorker.this.apply(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    cppTypename = api.cppTypename(type);
                    TypeExpr pointedType = type.pointedType();
                    // If the pointed type is an array or string, the C parameter will have a
                    // `void *` type, while in reality it is a `polyglot_array*`. api.cTypename will
                    // only return the `polyglot_array` typename, so we must transform this to a
                    // pointer.
                    String bufferTypename =
                            api.cTypename(type).concat(api.isStringOrArray(pointedType) ? "*" : "");

                    String bufferName = getBufferValue(argName);
                    CharSequence bufferAccess = bufferName;
                    StringBuilder builder =
                            new StringBuilder(bufferTypename)
                                    .append(" ")
                                    .append(bufferName)
                                    .append(" = ");
                    if (api.isStringOrArray(pointedType)) {
                        builder.append(CppGenerator.makeStaticCast(bufferTypename, argName));
                        bufferAccess = CppGenerator.deref(bufferName);
                    } else {
                        builder.append(CppGenerator.deref(argName));
                    }
                    builder.append(";\n")
                            .append(cppTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(
                                    CppGenerator.makeCall(
                                            cppTypename,
                                            List.of(
                                                    CppGenerator.makeNew(
                                                            api.cppTypename(pointedType),
                                                            List.of(bufferAccess)))));

                    return builder.toString();
                }
            }.apply(refType.referencedType());
        }
    }

    CppAPI api;

    public DispatchParameterConverter(CppAPI api) {
        this.api = api;
    }

    public String buildConversion(Parameter param) {
        return new ConversionWorker(param).apply(param.type);
    }

    public String buildPointerValueUpdate(Parameter param) {
        StringBuilder builder = new StringBuilder();
        if (param.type.isReference() && param.type.referencedType().isPointer()) {
            PointerTypeExpr ptr = (PointerTypeExpr) param.type.referencedType();
            String argName = api.toLower(param.name);
            String dataName = getConverterValue(argName);
            builder.append("*");
            if (api.isStringOrArray(ptr.typeExpr)) builder.append("__");
            builder.append(argName)
                    .append(" = ")
                    .append(dataName)
                    .append(".get() == nullptr ? ")
                    .append(api.nullValue(ptr));
            builder.append(" : ").append(dataName).append("->data_();");
        }
        return builder.toString();
    }
}
