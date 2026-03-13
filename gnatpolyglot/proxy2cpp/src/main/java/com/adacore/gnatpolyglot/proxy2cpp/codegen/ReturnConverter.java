package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;
import java.util.List;

public class ReturnConverter {

    private class Worker implements CppTypeWorker<String> {

        private String returnedValue;
        private FunctionDecl functionDecl;
        private String returnTypename;

        public Worker(FunctionDecl functionDecl, String returnedValue) {
            this.functionDecl = functionDecl;
            this.returnedValue = returnedValue;
            this.returnTypename = api.cppTypename(functionDecl.type.returnType);
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        @Override
        public String numberType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeStaticCast(returnTypename, returnedValue))
                    .toString();
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeCall(returnTypename, List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return new StringBuilder("return ")
                    .append(CppGenerator.makeCall(returnTypename, List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String pointerType(TypeExpr type) {
            // Pointers to arrays are returned as polyglot_arrays. In order to check for null
            // pointers, we must access the `data` field of the struct.
            String arrayDataAccess = api.isStringOrArray(type.pointedType()) ? ".data" : "";
            return new StringBuilder("return ")
                    .append(
                            CppGenerator.makeCall(
                                    returnTypename,
                                    List.of(
                                            CppGenerator.makeTernary(
                                                    new StringBuilder(returnedValue)
                                                            .append(arrayDataAccess)
                                                            .append(" == nullptr"),
                                                    "nullptr",
                                                    CppGenerator.makeNew(
                                                            api.cppTypename(type.pointedType()),
                                                            List.of(returnedValue))),
                                            api.cppOwner(functionDecl.type.returnOwner))))
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return returnedValue;
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
                    return new StringBuilder("return *")
                            .append(
                                    CppGenerator.makeStaticCast(
                                            new StringBuilder(refType.isConst() ? "const " : "")
                                                    .append(api.cppTypename(type))
                                                    .append("*"),
                                            returnedValue))
                            .toString();
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return new StringBuilder("return ")
                            .append(CppGenerator.makeView(api.cppTypename(type), returnedValue))
                            .toString();
                }

                @Override
                public String classType(TypeExpr type) {
                    return new StringBuilder("return ")
                            .append(CppGenerator.makeView(api.cppTypename(type), returnedValue))
                            .toString();
                }

                @Override
                public String pointerType(TypeExpr type) {
                    throw new UnsupportedOperationException(
                            "References to pointers are not supported");
                }
            }.apply(refType.referencedType());
        }
    }

    private CppAPI api;

    public ReturnConverter(CppAPI api) {
        this.api = api;
    }

    public String build(FunctionDecl functionDecl, String returnedValue) {
        return new Worker(functionDecl, returnedValue).apply(functionDecl.type.returnType);
    }
}
