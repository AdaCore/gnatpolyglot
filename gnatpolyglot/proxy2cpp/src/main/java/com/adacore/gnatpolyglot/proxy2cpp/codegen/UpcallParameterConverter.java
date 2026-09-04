//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;
import java.util.List;

public class UpcallParameterConverter {

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
            String bufferName = getBufferValue(argName);
            CharSequence objectConstruction =
                    api.instantiateObject(
                            type,
                            CppGenerator.makeConstCast(api.cTypename(type), argName).toString());
            // Build a polyglot_ptr, library owned: We can't know for sure if a view can hold the
            // value since the pointer may require identification.
            String ptrHolder = "___" + argName;
            String pointerTypename = api.cppTypename(type.makePointer(false, false));
            String referenceTypename = api.cppTypename(type.makeReference(false));
            return new StringBuilder(cppTypename)
                    .append(" *")
                    .append(ptrHolder)
                    .append(" = ")
                    .append(objectConstruction)
                    .append(";")
                    .append(pointerTypename)
                    .append(" ")
                    .append(bufferName)
                    .append(" = ")
                    .append(
                            CppGenerator.makeCall(
                                    pointerTypename,
                                    List.of(
                                            ptrHolder,
                                            CppGenerator.makeTernary(
                                                    ptrHolder + "->is_shadow()",
                                                    api.cppOwner(Owner.STATIC),
                                                    api.cppOwner(Owner.LIBRARY)))))
                    .append(";")
                    .append(referenceTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CppGenerator.deref(bufferName))
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            TypeExpr pointedType = type.pointedType();
            CharSequence objectConstruction = api.instantiateObject(pointedType, argName);
            return new StringBuilder(cppTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CppGenerator.makeCall(cppTypename, List.of(objectConstruction)))
                    .toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            return new StringBuilder(cppTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(api.buildUpcallLambda((FunctionTypeExpr) type, argName))
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
                    return ConversionWorker.this.apply(type.referencedType());
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
                            api.cTypename(type)
                                    .concat(getContext().isStringOrArray(pointedType) ? "*" : "");

                    String bufferName = getBufferValue(argName);
                    CharSequence bufferAccess = bufferName;
                    StringBuilder builder =
                            new StringBuilder(bufferTypename)
                                    .append(" ")
                                    .append(bufferName)
                                    .append(" = ");
                    if (getContext().isStringOrArray(pointedType)) {
                        builder.append(CppGenerator.makeStaticCast(bufferTypename, argName));
                        bufferAccess = CppGenerator.deref(bufferName);
                    } else {
                        builder.append(CppGenerator.deref(argName));
                    }
                    CharSequence objectConstruction =
                            api.instantiateObject(pointedType, bufferAccess.toString());
                    builder.append(";\n")
                            .append(cppTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(
                                    CppGenerator.makeCall(
                                            cppTypename, List.of(objectConstruction)));

                    return builder.toString();
                }

                @Override
                public String functionType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "Unreachable: non-cv function type references in upcalls are not"
                                    + " supported.");
                }
            }.apply(refType.referencedType());
        }
    }

    CppAPI api;

    public UpcallParameterConverter(CppAPI api) {
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
            if (api.getContext().isStringOrArray(ptr.typeExpr)) builder.append("__");
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
