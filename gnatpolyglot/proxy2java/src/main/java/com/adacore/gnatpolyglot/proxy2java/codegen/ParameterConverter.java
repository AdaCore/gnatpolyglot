package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;

public class ParameterConverter {

    /** Type worker that creates the conversion of Java value to call the java native functions. */
    private class JavaParamWorker implements JavaTypeWorker<String> {

        private Parameter param;
        private String argName;
        private String valueName;
        private String valueTypename;

        public JavaParamWorker(Parameter param) {
            this.param = param;
            this.argName = api.javaArgName(param.name);
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
        public String voidType(TypeExpr type) {
            throw new UnsupportedOperationException("Unreachable");
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
        public String voidType(TypeExpr type) {
            throw new UnsupportedOperationException("Unreachable");
        }
    }

    private JavaAPI api;

    public ParameterConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the conversion of a Java parameter to call the Java native function. */
    public String javaParam(Parameter param) {
        return new JavaParamWorker(param).apply(param.type);
    }

    /** Create the conversion of a JNI parameter in the C layer. */
    public String jniParam(Parameter param) {
        return new JNIParamWorker(param).apply(param.type);
    }
}
