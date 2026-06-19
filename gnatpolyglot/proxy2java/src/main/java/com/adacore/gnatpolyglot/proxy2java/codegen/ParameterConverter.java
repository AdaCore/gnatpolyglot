package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
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
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder(valueTypename)
                    .append(" ")
                    .append(valueName)
                    .append(" = ")
                    .append(argName)
                    .append(".getData()")
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
}
