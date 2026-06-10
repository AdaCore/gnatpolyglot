package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.ArrayList;
import java.util.List;

public class ReturnConverter {

    /**
     * Type worker that creates the conversion of return values from Java native functions to the
     * user.
     */
    private class JavaReturnWorker implements JavaTypeWorker<String> {

        private FunctionDecl functionDecl;
        private String returnedValue;

        public String javaReturnType;

        public JavaReturnWorker(FunctionDecl functionDecl, String returnedValue) {
            this.functionDecl = functionDecl;
            this.returnedValue = returnedValue;

            this.javaReturnType = api.javaReturnTypename(functionDecl.type.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ").append(returnedValue).append(";").toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return new StringBuilder("return ").append(returnedValue).append(";").toString();
        }

        @Override
        public String classType(TypeExpr type) {
            List<CharSequence> args =
                    new ArrayList<>(
                            List.of(
                                    JavaGenerator.makeNew(
                                            "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                                            List.of(
                                                    returnedValue,
                                                    api.javaOwner(
                                                            functionDecl.type.returnOwner)))));
            // When a getter returns a reference, it implies that the returned object will reference
            // data from a parent structure. In case the parent would become unreferenced before its
            // field, the returned object must hold a reference to its eldest parent object in order
            // to avoid referencing freed memory.
            //
            // By default, `this.parent` is equal to `this`, so we do not need to check which object
            // to refer. This allows the value returned by `a.getB().getC()` to hold a reference to
            // `a`.
            if (api.isMethod(functionDecl)
                    && functionDecl.role.kind == RoleKind.GETTER
                    && functionDecl.type.returnOwner == Owner.STATIC
                    && functionDecl.type.returnType.isReference()) args.add("this.parent");
            return new StringBuilder("return ")
                    .append(JavaGenerator.makeNew(javaReturnType, args))
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "return;";
        }

        @Override
        public String refType(TypeExpr type) {
            return apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            // The JNI layer does not set the owner of the data in order to offload as much of the
            // work to the Java world, so we must set it here.
            return JavaGenerator.makeMethodCall(
                            returnedValue,
                            "setOwner",
                            List.of(api.javaOwner(functionDecl.type.returnOwner)))
                    .append(";\n")
                    .append("return ")
                    .append(JavaGenerator.makeNew(api.javaTypename(type), List.of(returnedValue)))
                    .append(";")
                    .toString();
        }

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(
                            JavaGenerator.makeMethodCall(
                                    javaReturnType + ".fromValue", "get", List.of(returnedValue)))
                    .append(";")
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder = new StringBuilder();
            CharSequence pointerData;
            String nullData;
            if (getContext().isStringOrArray(type.pointedType())) {
                pointerData = returnedValue;
                nullData = "null";
                // In the case of arrays, also set the data owner of the data, if any. The
                // ArrayData is created in the JNI layer without setting an owner.
                builder.append("if (")
                        .append(returnedValue)
                        .append(" != null) ")
                        .append(
                                JavaGenerator.makeMethodCall(
                                        returnedValue,
                                        "setOwner",
                                        List.of(api.javaOwner(functionDecl.type.returnOwner))))
                        .append(";\n");
            } else {
                nullData = "0L";
                pointerData =
                        JavaGenerator.makeNew(
                                "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                                List.of(
                                        returnedValue,
                                        api.javaOwner(functionDecl.type.returnOwner)));
            }

            // Create an Optional that contain the possibly null object.
            return builder.append("return ")
                    .append(
                            JavaGenerator.makeCall(
                                    "java.util.Optional.ofNullable",
                                    List.of(
                                            JavaGenerator.makeTernary(
                                                    returnedValue + " == " + nullData,
                                                    "null",
                                                    JavaGenerator.makeNew(
                                                            api.javaTypename(type.pointedType()),
                                                            List.of(pointerData))))))
                    .append(";")
                    .toString();
        }
    }

    /** Type worker that creates the conversion of return values from bound functions to the JVM. */
    private class CReturnWorker implements JavaTypeWorker<String> {

        private FunctionDecl functionDecl;
        private String returnedValue;
        private String jniReturnType;

        public CReturnWorker(FunctionDecl functionDecl, String returnedValue) {
            this.functionDecl = functionDecl;
            this.returnedValue = returnedValue;

            this.jniReturnType = api.jniReturnTypename(functionDecl.type.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(jniReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String boolType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(returnedValue)
                    .append(" != JNI_FALSE;")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(jniReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "return;";
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
                    return new StringBuilder("return ")
                            .append(
                                    CGenerator.makeCast(
                                            jniReturnType, CGenerator.deref(returnedValue)))
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
                public String classType(TypeExpr type) {
                    return CReturnWorker.this.classType(type);
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return CReturnWorker.this.arrayType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to pointers are not supported");
                }
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return CGenerator.makeCall(
                            "gnatpolyglot_proxy2java_to_ArrayData", List.of("env", returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String enumType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CGenerator.makeCast(jniReturnType, returnedValue))
                    .append(";")
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            return apply(type.pointedType());
        }
    }

    private JavaAPI api;

    public ReturnConverter(JavaAPI api) {
        this.api = api;
    }

    /** Create the return statement to return the value from the native function to the user. */
    public String javaReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return new JavaReturnWorker(functionDecl, returnedValue)
                .apply(functionDecl.type.returnType);
    }

    /** Create the return statement to return the value from the bound function to the JVM. */
    public String cReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return new CReturnWorker(functionDecl, returnedValue).apply(functionDecl.type.returnType);
    }
}
