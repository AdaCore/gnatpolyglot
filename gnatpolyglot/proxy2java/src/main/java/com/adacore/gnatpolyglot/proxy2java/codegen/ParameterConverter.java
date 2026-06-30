//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Transfer.RequiredOwner;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.List;

public class ParameterConverter {

    /** Type worker that creates the conversion of Java value to call the java native functions. */
    private class JavaParamWorker implements JavaTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String valueName;
        private String valueTypename;

        public JavaParamWorker(Parameter param, boolean isFirstMethodParam) {
            this.param = param;
            this.argName = isFirstMethodParam ? "this" : api.javaArgName(param.name);
            this.valueName = api.javaValueName(param.name);
            this.valueTypename = api.javaNativeTypename(param.type);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(argName)
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(JavaGenerator.makeGetAddress(argName))
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            throw new UnsupportedOperationException("Unreachable");
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
                    return new StringBuilder(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(argName)
                            .append(".getBuffer()")
                            .toString();
                }

                @Override
                public String boolType(TypeExpr type) {
                    return numberType(type);
                }

                @Override
                public String enumType(TypeExpr type) {
                    return numberType(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    return JavaParamWorker.this.classType(type);
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return JavaParamWorker.this.arrayType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return new StringBuilder(makeObjectOwnershipCheck(param, argName))
                            .append(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(argName)
                            .toString();
                }
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(JavaGenerator.makeGetData(argName))
                    .toString();
        }

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(argName)
                    .append(".value")
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder =
                    new StringBuilder(makeObjectOwnershipCheck(param, argName))
                            .append(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ");
            if (getContext().isStringOrArray(type.pointedType())) {
                builder.append(JavaGenerator.makeOptGetData(argName));
            } else {
                builder.append(JavaGenerator.makeOptGetAddress(argName));
            }
            return builder.toString();
        }
    }

    /**
     * Type worker that creates the conversion of JNI values to call the C Symbol of bound
     * functions.
     */
    private class JNIParamWorker implements JavaTypeWorker<String> {

        private Parameter param;

        private String argName;
        private String valueName;
        private String valueTypename;

        public JNIParamWorker(Parameter param) {
            this.param = param;
            this.argName = api.jniArgName(param.name);
            this.valueName = api.jniValueName(param.name);
            this.valueTypename = api.cTypename(param.type);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CGenerator.makeCast(valueTypename, argName))
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CGenerator.makeCast(valueTypename, argName))
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            if (getContext().isStringOrArray(type.pointedType()))
                return arrayType(type.pointedType());
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CGenerator.makeCast(valueTypename, argName))
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public String refType(TypeExpr refType) {
            return new JavaTypeWorker.JavaSubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                private String makeScalarRefParam(TypeExpr type) {
                    String ptrTypename = api.cTypename(refType);
                    return new StringBuilder(ptrTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(
                                    CGenerator.makeCast(
                                            ptrTypename,
                                            CGenerator.makeJNICall(
                                                    "GetDirectBufferAddress", List.of(argName))))
                            .toString();
                }

                @Override
                public String numberType(TypeExpr type) {
                    return makeScalarRefParam(type);
                }

                @Override
                public String boolType(TypeExpr type) {
                    return makeScalarRefParam(type);
                }

                @Override
                public String enumType(TypeExpr type) {
                    return numberType(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    return new StringBuilder(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(CGenerator.makeCast(valueTypename, argName))
                            .toString();
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return JNIParamWorker.this.arrayType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    TypeExpr pointedType = type.pointedType();
                    String dataName = api.makeTemp(param.name, "data");
                    String bufferName = api.makeTemp(param.name, "buffer");
                    String bufferTypename = api.cTypename(pointedType);
                    CharSequence convertedData;
                    if (getContext().isStringOrArray(pointedType)) {
                        convertedData =
                                CGenerator.makeCall(
                                        "gnatpolyglot_proxy2java_to_array_data",
                                        List.of("env", dataName));
                    } else {
                        convertedData =
                                CGenerator.makeTernary(
                                        dataName + " != NULL",
                                        CGenerator.makeCast(
                                                bufferTypename + "*",
                                                CGenerator.makeJNICall(
                                                        "CallLongMethod",
                                                        "PolyglotData_getAddress_method(env)",
                                                        List.of(dataName))),
                                        "NULL");
                    }
                    // Get the PolyglotData of the referenced object.
                    return new StringBuilder("jobject ")
                            .append(dataName)
                            .append(" = ")
                            .append(
                                    CGenerator.makeJNICall(
                                            "CallObjectMethod",
                                            "ObjectRef_getData_method(env)",
                                            List.of(argName)))
                            .append(";\n")
                            // Create a buffer value holding the native data.
                            .append(bufferTypename)
                            .append(" ")
                            .append(bufferName)
                            .append(" = ")
                            .append(convertedData)
                            .append(";\n")
                            // The value passed to the symbol is the address to the buffer.
                            .append(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append("&")
                            .append(bufferName)
                            .toString();
                }
            }.apply(refType.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("struct array_data ")
                    .append(valueName)
                    .append(" = ")
                    .append(
                            CGenerator.makeCall(
                                    "gnatpolyglot_proxy2java_to_array_data",
                                    List.of("env", argName)))
                    .toString();
        }

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CGenerator.makeCast(valueTypename, argName))
                    .toString();
        }
    }

    private JavaAPI api;

    public ParameterConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the conversion of a Java parameter to call the Java native function. */
    public String javaParam(Parameter param, boolean isFirstMethodParam) {
        return new JavaParamWorker(param, isFirstMethodParam).apply(param.type);
    }

    /** Create the conversion of a JNI parameter in the C layer. */
    public String jniParam(Parameter param) {
        return new JNIParamWorker(param).apply(param.type);
    }

    /** Create the conversion of a JNI parameter in the C layer. */
    public String makeObjectOwnershipCheck(Parameter param, String argName) {
        if (param.transfer.required_owner == RequiredOwner.ANY) return "";

        String nullCheck;
        String getOwner;
        if (param.type.isReference() && !param.type.isConst()) {
            nullCheck = argName + ".get().isPresent()";
            getOwner = argName + ".get().get()._getOwner()";
        } else {
            nullCheck = argName + " != null";
            getOwner = argName + "._getOwner()";
        }
        String owner = api.javaOwner(param.transfer.required_owner);
        return new StringBuilder("if (")
                .append(nullCheck)
                .append("&& ")
                .append(JavaGenerator.makeMethodCall(owner, "compareTo", List.of(getOwner)))
                .append(" > 0)\n")
                .append("throw ")
                .append(
                        JavaGenerator.makeNew(
                                "IllegalArgumentException",
                                List.of("\"" + argName + ": owner should be \" + " + owner)))
                .append(";\n")
                .toString();
    }

    public String jniParamUpdate(Parameter param) {
        if (!param.type.isReference()
                || !param.type.referencedType().isPointer()
                || (param.type.isReference() && param.type.isConst())) return "";
        String jobjectConverter =
                api.getContext().isStringOrArray(param.type.referencedType().pointedType())
                        ? "gnatpolyglot_proxy2java_to_ArrayData"
                        : "gnatpolyglot_proxy2java_to_Pointer";
        // Convert the native data to the corresponding PolyglotData and call the update method
        // of ObjectRef.
        return CGenerator.makeJNICall(
                        "CallVoidMethod",
                        "ObjectRef_update_method(env)",
                        List.of(
                                api.jniArgName(param.name),
                                CGenerator.makeCall(
                                        jobjectConverter,
                                        List.of(
                                                "env",
                                                CGenerator.deref(api.jniValueName(param.name))))))
                .toString();
    }
}
