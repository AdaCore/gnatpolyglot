//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.List;

public class UpcallParameterConverter {

    private class JavaParamWorker implements JavaTypeWorker<String> {

        private Parameter param;
        private String callbackName;
        private String argName;
        private String valueName;
        private String valueTypename;

        public JavaParamWorker(Parameter param, String callbackName) {
            this.param = param;
            this.callbackName = callbackName;
            this.argName = api.javaArgName(param.name);
            this.valueName = api.javaValueName(param.name);
            this.valueTypename = api.javaTypename(param.type);
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
                    .append(";")
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        public String arrayType(TypeExpr type) {
            // Create a new object with the STATIC ownership: the address received might
            // point to the stack.
            return new StringBuilder(argName)
                    .append(".")
                    .append(
                            JavaGenerator.makeCall(
                                    "setOwner", List.of(api.javaOwner(Owner.STATIC))))
                    .append(";\n")
                    .append(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(JavaGenerator.makeNew(valueTypename, List.of(argName)))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            // Create a new object with the STATIC ownership: the address received might
            // point to the stack.
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(
                            JavaGenerator.makeObjectFromAddress(
                                    valueTypename, argName, api.javaOwner(Owner.STATIC)))
                    .append(";")
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
                    // The JNI layer creates a new ByteBuffer from the address received. Its byte
                    // order is not yet set at this point in order to offload as much work to the
                    // Java world and avoid doing too many JNI calls. Set the correct byte order
                    // here and create the correct scalar reference.
                    return new StringBuilder(argName)
                            .append(".order(java.nio.ByteOrder.nativeOrder());\n")
                            .append(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(JavaGenerator.makeNew(valueTypename, List.of(argName)))
                            .append(";")
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
                public String arrayType(TypeExpr type) {
                    return JavaParamWorker.this.arrayType(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    return JavaParamWorker.this.classType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return new StringBuilder(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(argName)
                            .append(";")
                            .toString();
                }

                @Override
                public String functionType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to function types in upcalls are not supported");
                }
            }.apply(type.referencedType());
        }

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(valueTypename)
                    .append(".fromValue.")
                    .append(JavaGenerator.makeCall("get", List.of(argName)))
                    .append(";")
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            String nullData;
            CharSequence value;
            StringBuilder builder = new StringBuilder();
            if (getContext().isStringOrArray(type.pointedType())) {
                nullData = "null";
                value = JavaGenerator.makeNew(valueTypename, List.of(argName));
                builder.append("if (")
                        .append(argName)
                        .append(" != null)")
                        .append(
                                JavaGenerator.makeMethodCall(
                                        argName, "setOwner", List.of(api.javaOwner(Owner.STATIC))))
                        .append(";\n");
            } else {
                nullData = "0L";
                value =
                        JavaGenerator.makeObjectFromAddress(
                                valueTypename, argName, api.javaOwner(Owner.LIBRARY));
            }
            return builder.append(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(JavaGenerator.makeTernary(argName + " == " + nullData, "null", value))
                    .append(";")
                    .toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(api.buildCallbackLambda((FunctionTypeExpr) type, argName))
                    .append(";")
                    .toString();
        }
    }

    private class JNIParamWorker implements JavaTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String valueName;
        private String valueTypename;

        public JNIParamWorker(Parameter param) {
            this.param = param;
            this.argName = api.jniArgName(param.name);
            this.valueName = api.jniValueName(param.name);
            this.valueTypename = api.jniTypename(param.type);
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
                    .append(";")
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(
                            CGenerator.makeCall(
                                    "gnatpolyglot_proxy2java_to_ArrayData",
                                    List.of("env", argName)))
                    .append(";")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(CGenerator.makeCast(valueTypename, argName))
                    .append(";")
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
                            .append(
                                    CGenerator.makeJNICall(
                                            "NewDirectByteBuffer",
                                            List.of(
                                                    argName,
                                                    CGenerator.makeCall(
                                                            "sizeof",
                                                            List.of(CGenerator.deref(argName))))))
                            .append(";")
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
                public String arrayType(TypeExpr type) {
                    return JNIParamWorker.this.arrayType(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    return JNIParamWorker.this.classType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    CharSequence javaDataValue;
                    // Convert the native data given by argument to a PolyglotData Java object.
                    if (getContext().isStringOrArray(type.pointedType())) {
                        javaDataValue =
                                CGenerator.makeCall(
                                        "gnatpolyglot_proxy2java_to_ArrayData",
                                        List.of("env", CGenerator.deref(argName)));
                    } else {
                        javaDataValue = CGenerator.makeCast("jlong", CGenerator.deref(argName));
                    }
                    // Create a <T>.Ref Java object that will possibly be updated with a new value
                    // in the upcall to the JVM.
                    return new StringBuilder(valueTypename)
                            .append(" ")
                            .append(valueName)
                            .append(" = ")
                            .append(
                                    CGenerator.makeJNICall(
                                            "NewObject",
                                            List.of(
                                                    CGenerator.makeCall(
                                                            api.refClassFunctionName(
                                                                    type.pointedType()),
                                                            List.of("env")),
                                                    CGenerator.makeCall(
                                                            api.refCtorFunctionName(
                                                                    type.pointedType()),
                                                            List.of("env")),
                                                    javaDataValue)))
                            .append(";")
                            .toString();
                }

                @Override
                public String functionType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to function types in upcalls are not supported");
                }
            }.apply(type.referencedType());
        }

        @Override
        public String enumType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder =
                    new StringBuilder(valueTypename).append(" ").append(valueName).append(" = ");
            if (getContext().isStringOrArray(type.pointedType())) {
                builder.append(
                        CGenerator.makeCall(
                                "gnatpolyglot_proxy2java_to_ArrayData", List.of("env", argName)));
            } else {
                builder.append(CGenerator.makeCast(valueTypename, argName));
            }
            return builder.append(";").toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(
                            CGenerator.makeCall(
                                    "gnatpolyglot_proxy2java_to_CallbackData",
                                    List.of("env", argName)))
                    .append(";")
                    .toString();
        }
    }

    private JavaAPI api;

    public UpcallParameterConverter(JavaAPI api) {
        this.api = api;
    }

    public String javaParam(Parameter param, String callbackName) {
        return new JavaParamWorker(param, callbackName).apply(param.type);
    }

    public String jniParam(Parameter param) {
        return new JNIParamWorker(param).apply(param.type);
    }

    public String jniParamUpdate(Parameter param) {
        return new TypeWorker.PointerTypeWorker<String>() {

            @Override
            public ProxyContext getContext() {
                return api.getContext();
            }

            @Override
            public String pointerType(TypeExpr type) {
                return defaultCase();
            }

            @Override
            public String refType(TypeExpr type) {
                if (type.isConst()) return defaultCase();
                String cDataName = api.makeTemp(param.name, "cdata");
                String javaDataName = api.makeTemp(param.name, "jdata");

                String cTypename = api.cTypename(type.referencedType());

                // Get the PolyglotData of the ObjectRef created previously (held by the
                // jniValue variable).
                StringBuilder builder =
                        new StringBuilder()
                                .append("jobject ")
                                .append(javaDataName)
                                .append(" = ")
                                .append(
                                        CGenerator.makeJNICall(
                                                "CallObjectMethod",
                                                "ObjectRef_getData_method(env)",
                                                List.of(api.jniValueName(param.name))))
                                .append(";\n")
                                .append(cTypename)
                                .append(" ")
                                .append(cDataName)
                                .append(" = ");
                // Make a conversion of the PolyglotData to a native value.
                if (getContext().isStringOrArray(type.referencedType().pointedType())) {
                    // Calls to "gnatpolyglot_proxy2java_to_array_data" are NULL safe.
                    builder.append(
                            CGenerator.makeCall(
                                    "gnatpolyglot_proxy2java_to_array_data",
                                    List.of("env", javaDataName)));
                } else {
                    builder.append(
                            CGenerator.makeTernary(
                                    javaDataName + " == NULL",
                                    "NULL",
                                    CGenerator.makeCast(
                                            cTypename,
                                            CGenerator.makeJNICall(
                                                    "CallLongMethod",
                                                    "PolyglotData_getAddress_method(env)",
                                                    List.of(javaDataName)))));
                }

                // Overwrite the value in the address given in the parameter of the dispatch
                // function
                return builder.append(";\n")
                        .append(CGenerator.deref(api.jniArgName(param.name)))
                        .append(" = ")
                        .append(cDataName)
                        .append(";")
                        .toString();
            }

            @Override
            public String defaultCase() {
                return "";
            }
        }.apply(param.type);
    }
}
