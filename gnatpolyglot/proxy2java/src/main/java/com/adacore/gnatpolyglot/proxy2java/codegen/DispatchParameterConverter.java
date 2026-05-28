package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.List;

public class DispatchParameterConverter {

    private class JavaParamWorker implements JavaTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String valueName;
        private String valueTypename;

        public JavaParamWorker(Parameter param) {
            this.param = param;
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
            // point
            // to the stack.
            CharSequence pointer =
                    JavaGenerator.makeNew(
                            "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                            List.of(argName, api.javaOwner(Owner.STATIC)));
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(JavaGenerator.makeNew(valueTypename, List.of(pointer)))
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
            }.apply(type.referencedType());
        }

        @Override
        public String enumType(TypeExpr type) {
            return numberType(type);
        }
    }

    private JavaAPI api;

    public DispatchParameterConverter(JavaAPI api) {
        this.api = api;
    }

    public String javaParam(Parameter param) {
        return new JavaParamWorker(param).apply(param.type);
    }

    public String jniParam(Parameter param) {
        return new JNIParamWorker(param).apply(param.type);
    }
}
