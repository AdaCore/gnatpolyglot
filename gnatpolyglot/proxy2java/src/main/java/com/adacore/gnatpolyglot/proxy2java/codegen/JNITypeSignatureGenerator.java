package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;

public class JNITypeSignatureGenerator {

    public class SignatureWorker implements JavaTypeWorker<String> {

        private boolean isReturn;

        public SignatureWorker(boolean isReturn) {
            this.isReturn = isReturn;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String nativeTypeSignature(NativeType nativeType) {
            return switch (nativeType) {
                case VOID -> "V";
                case BOOL -> "Z";
                case CHAR -> "C";
                case FLOAT32 -> "F";
                case FLOAT64 -> "D";
                case UINT8, SINT8 -> "B";
                case UINT16, SINT16 -> "S";
                case UINT32, SINT32 -> "I";
                case UINT64, SINT64 -> "J";
                default -> throw new UnsupportedOperationException("Unsupported native type");
            };
        }

        @Override
        public String numberType(TypeExpr type) {
            NativeTypeDecl decl = (NativeTypeDecl) getContext().getTypeDecl(type.getName());
            return nativeTypeSignature(decl.nativeType);
        }

        @Override
        public String boolType(TypeExpr type) {
            NativeTypeDecl decl = (NativeTypeDecl) getContext().getTypeDecl(type.getName());
            return nativeTypeSignature(decl.nativeType);
        }

        @Override
        public String enumType(TypeExpr type) {
            EnumerationDecl decl = (EnumerationDecl) getContext().getTypeDecl(type.getName());
            return nativeTypeSignature(decl.representationType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "L%s;".formatted(api.javaNativeTypename(type).replace(".", "/"));
        }

        @Override
        public String classType(TypeExpr type) {
            return nativeTypeSignature(NativeType.UINT64);
        }

        @Override
        public String pointerType(TypeExpr type) {
            return apply(type.pointedType());
        }

        @Override
        public String voidType(TypeExpr type) {
            return nativeTypeSignature(NativeType.VOID);
        }

        @Override
        public String refType(TypeExpr refType) {
            return new JavaTypeWorker.JavaSubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                @Override
                public String numberType(TypeExpr type) {
                    String javaNativeType = api.javaNativeTypename(refType);
                    return "L%s;".formatted(javaNativeType.replace(".", "/"));
                }

                @Override
                public String boolType(TypeExpr type) {
                    String javaNativeType = api.javaNativeTypename(refType);
                    return "L%s;".formatted(javaNativeType.replace(".", "/"));
                }

                @Override
                public String enumType(TypeExpr type) {
                    String javaNativeType = api.javaNativeTypename(refType);
                    return "L%s;".formatted(javaNativeType.replace(".", "/"));
                }

                @Override
                public String arrayType(TypeExpr type) {
                    String javaNativeType = api.javaNativeTypename(type);
                    return "L%s;".formatted(javaNativeType.replace(".", "/"));
                }

                @Override
                public String classType(TypeExpr type) {
                    return SignatureWorker.this.classType(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    String javaNativeType = api.javaTypename(type.referencedType());
                    return "L%s;".formatted(javaNativeType.replace(".", "/") + "$Ref");
                }
            }.apply(refType.referencedType());
        }
    }

    private JavaAPI api;

    public JNITypeSignatureGenerator(JavaAPI api) {
        this.api = api;
    }

    public String jniTypeSignature(TypeExpr type, boolean isReturn) {
        return new SignatureWorker(isReturn).apply(type);
    }
}
