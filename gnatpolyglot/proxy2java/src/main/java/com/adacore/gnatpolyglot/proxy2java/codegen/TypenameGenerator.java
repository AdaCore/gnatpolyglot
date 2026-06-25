package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;

public class TypenameGenerator {

    public static String nativeWrapperTypename(NativeType nativeType) {
        return switch (nativeType) {
            case BOOL -> "Boolean";
            case CHAR -> "Character";
            case FLOAT32 -> "Float";
            case FLOAT64 -> "Double";
            case UINT8, SINT8 -> "Byte";
            case UINT16, SINT16 -> "Short";
            case UINT32, SINT32 -> "Integer";
            case UINT64, SINT64 -> "Long";
            default -> throw new UnsupportedOperationException("Unreachable");
        };
    }

    public static String nativeArrayTypename(NativeType nativeType) {
        return switch (nativeType) {
            case BOOL -> "BooleanArray";
            case CHAR -> "CharacterArray";
            case FLOAT32 -> "FloatArray";
            case FLOAT64 -> "DoubleArray";
            case UINT8, SINT8 -> "ByteArray";
            case UINT16, SINT16 -> "ShortArray";
            case UINT32, SINT32 -> "IntegerArray";
            case UINT64, SINT64 -> "LongArray";
            default -> throw new UnsupportedOperationException("Unreachable");
        };
    }

    public static String nativeReferenceTypename(NativeType nativeType) {
        return switch (nativeType) {
            case BOOL -> "BooleanRef";
            case CHAR -> "CharacterRef";
            case FLOAT32 -> "FloatRef";
            case FLOAT64 -> "DoubleRef";
            case UINT8, SINT8 -> "ByteRef";
            case UINT16, SINT16 -> "ShortRef";
            case UINT32, SINT32 -> "IntegerRef";
            case UINT64, SINT64 -> "LongRef";
            default -> throw new UnsupportedOperationException("Unreachable");
        };
    }

    /** Type worker that creates the Java typename, generally exposed to the user. */
    private class JavaTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.javaPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            FullyQualifiedName name = type.getName();
            return new StringBuilder(api.basePackage())
                    .append(
                            name.getParentFullyQualifiedName()
                                    .join(n -> n.getLastName().toLower(), ".", ".", "."))
                    .append(name.getLastName().toPascal())
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            FullyQualifiedName name = type.getName();
            return new StringBuilder(api.basePackage())
                    .append(
                            name.getParentFullyQualifiedName()
                                    .join(n -> n.getLastName().toLower(), ".", ".", "."))
                    .append(name.getLastName().toPascal())
                    .toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
        }

        @Override
        public String refType(TypeExpr type) {
            return new JavaTypeWorker.JavaSubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                private String getNativeRefTypename(TypeExpr typeExpr) {
                    TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
                    return "com.adacore.gnatpolyglot.runtime."
                            + nativeReferenceTypename(NativeTypeDecl.class.cast(decl).nativeType);
                }

                @Override
                public String numberType(TypeExpr type) {
                    return getNativeRefTypename(type);
                }

                @Override
                public String charType(TypeExpr type) {
                    return getNativeRefTypename(type);
                }

                @Override
                public String boolType(TypeExpr type) {
                    return getNativeRefTypename(type);
                }

                @Override
                public String enumType(TypeExpr type) {
                    return JavaTypenameWorker.this.enumType(type).concat(".Ref");
                }

                @Override
                public String classType(TypeExpr type) {
                    return javaTypename(type);
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return javaTypename(type);
                }

                @Override
                public String stringType(TypeExpr type) {
                    return javaTypename(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return javaTypename(type).concat(".Ref");
                }
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            TypeExpr elementType = type.elementType();
            if (getContext().isNativeScalar(elementType)) {
                TypeDecl decl = getContext().getTypeDecl(type.getName());
                return "com.adacore.gnatpolyglot.runtime.ada2java."
                        + nativeArrayTypename(NativeTypeDecl.class.cast(decl).nativeType);
            }
            String javaTypename = javaTypename(elementType);
            if (type.elementType().isPointer()) return javaTypename.concat(".PtrArray");
            return javaTypename.concat(".Array");
        }

        @Override
        public String stringType(TypeExpr type) {
            return "com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString";
        }

        @Override
        public String pointerType(TypeExpr type) {
            return javaTypename(type.pointedType());
        }
    }

    /** Type worker that creates the Java native typename used to declare Java native functions. */
    private class JavaNativeTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.javaPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String classType(TypeExpr type) {
            return api.javaPrimitiveTypename(NativeType.UINT64);
        }

        @Override
        public String pointerType(TypeExpr type) {
            if (getContext().isStringOrArray(type.pointedType()))
                return arrayType(type.pointedType());
            return api.javaPrimitiveTypename(NativeType.UINT64);
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
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
                    return "java.nio.ByteBuffer";
                }

                @Override
                public String boolType(TypeExpr type) {
                    return "java.nio.ByteBuffer";
                }

                @Override
                public String enumType(TypeExpr type) {
                    return "java.nio.ByteBuffer";
                }

                @Override
                public String classType(TypeExpr type) {
                    return api.javaPrimitiveTypename(NativeType.UINT64);
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return javaNativeTypename(type);
                }

                @Override
                public String stringType(TypeExpr type) {
                    return javaNativeTypename(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return javaTypename(refType);
                }
            }.apply(refType.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "com.adacore.gnatpolyglot.runtime.ada2java.ArrayData";
        }

        @Override
        public String stringType(TypeExpr type) {
            return arrayType(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            EnumerationDecl enumDecl = (EnumerationDecl) getContext().getTypeDecl(type.getName());
            return api.javaPrimitiveTypename(enumDecl.representationType());
        }
    }

    /** Type worker that creates the JNI typenames in the C layer. */
    private class JNITypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.jniPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String classType(TypeExpr type) {
            return "jlong";
        }

        @Override
        public String pointerType(TypeExpr type) {
            if (getContext().isStringOrArray(type.pointedType()))
                return arrayType(type.pointedType());
            return "jlong";
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
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
                    return "jobject";
                }

                @Override
                public String boolType(TypeExpr type) {
                    return "jobject";
                }

                @Override
                public String enumType(TypeExpr type) {
                    return "jobject";
                }

                @Override
                public String classType(TypeExpr type) {
                    return "jlong";
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return "jobject";
                }

                @Override
                public String stringType(TypeExpr type) {
                    return "jobject";
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return "jobject";
                }
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            // Arrays are passed using the "ArrayData" jobject.
            return "jobject";
        }

        @Override
        public String stringType(TypeExpr type) {
            // String are passed using the "ArrayData" jobject.
            return "jobject";
        }

        @Override
        public String enumType(TypeExpr type) {
            EnumerationDecl enumDecl = (EnumerationDecl) getContext().getTypeDecl(type.getName());
            return api.jniPrimitiveTypename(enumDecl.representationType());
        }
    }

    /**
     * Type worker that creates the C typename, those are used in the calls to the C symbol of
     * functions.
     */
    private class CTypenameWorker implements JavaTypeWorker<String> {

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr typeExpr) {
            TypeDecl decl = getContext().getTypeDecl(typeExpr.getName());
            return api.cPrimitiveTypename(NativeTypeDecl.class.cast(decl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String classType(TypeExpr type) {
            return "void *";
        }

        @Override
        public String pointerType(TypeExpr type) {
            if (getContext().isStringOrArray(type.pointedType()))
                return arrayType(type.pointedType());
            return "void *";
        }

        @Override
        public String voidType(TypeExpr type) {
            return "void";
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
                    return cTypename(type).concat("*");
                }

                @Override
                public String boolType(TypeExpr type) {
                    return cTypename(type).concat("*");
                }

                @Override
                public String enumType(TypeExpr type) {
                    return cTypename(type).concat("*");
                }

                @Override
                public String classType(TypeExpr type) {
                    return "void *";
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return cTypename(type);
                }

                @Override
                public String stringType(TypeExpr type) {
                    return cTypename(type);
                }

                @Override
                public String pointerType(TypeExpr type) {
                    return cTypename(type.pointedType()) + "*";
                }
            }.apply(type.referencedType());
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "struct array_data";
        }

        @Override
        public String stringType(TypeExpr type) {
            return "struct array_data";
        }

        @Override
        public String enumType(TypeExpr type) {
            EnumerationDecl enumDecl = (EnumerationDecl) getContext().getTypeDecl(type.getName());
            return api.cPrimitiveTypename(enumDecl.representationType());
        }
    }

    private JavaAPI api;

    public TypenameGenerator(JavaAPI api) {
        this.api = api;
    }

    /** Return the exposed Java typename of type. */
    public String javaTypename(TypeExpr type) {
        return new JavaTypenameWorker().apply(type);
    }

    /** Return the Java native typename of type. */
    public String javaNativeTypename(TypeExpr type) {
        return new JavaNativeTypenameWorker().apply(type);
    }

    /** Return the JNI typename of type. */
    public String jniTypename(TypeExpr type) {
        return new JNITypenameWorker().apply(type);
    }

    /** Return the C typename of type. */
    public String cTypename(TypeExpr type) {
        return new CTypenameWorker().apply(type);
    }
}
