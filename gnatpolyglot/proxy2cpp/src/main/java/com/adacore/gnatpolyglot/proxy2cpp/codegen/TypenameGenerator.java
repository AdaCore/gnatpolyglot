//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp.codegen;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;
import com.adacore.gnatpolyglot.proxy2cpp.CppAPI;
import java.util.stream.Collectors;

public class TypenameGenerator {

    private String constness(TypeExpr type) {
        return type.isConst() ? "const " : "";
    }

    private class CppTypenameWorker implements TypeWorker<String> {

        private String prefix;

        public CppTypenameWorker(boolean addLeadingColons) {
            this.prefix = addLeadingColons ? "::" : "";
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr type) {
            TypeDecl typeDecl = getContext().getTypeDecl(type.getName());
            return api.nativeTypeName(((NativeTypeDecl) typeDecl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String charType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return type.getName().join(fqn -> api.lastNameToCppName(fqn), prefix, "::", "");
        }

        @Override
        public String stringType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String arrayType(TypeExpr type) {
            return new StringBuilder(prefix)
                    .append("gnatpolyglot::ada::arrays::polyglot_array<")
                    .append(cppTypename(type.elementType(), true))
                    .append(">")
                    .toString();
        }

        @Override
        public String classType(TypeExpr type) {
            return type.getName().join(fqn -> api.lastNameToCppName(fqn), prefix, "::", "");
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder =
                    new StringBuilder(prefix)
                            .append("gnatpolyglot::polyglot_ptr<")
                            .append(constness(type))
                            .append(cppTypename(type.pointedType(), true))
                            .append(">");
            return builder.toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String refType(TypeExpr type) {
            StringBuilder builder =
                    new StringBuilder(constness(type))
                            .append(cppTypename(type.referencedType(), true))
                            .append(" &");
            return builder.toString();
        }

        @Override
        public String functionType(TypeExpr type) {
            FunctionTypeExpr functionType = (FunctionTypeExpr) type;
            StringBuilder builder =
                    new StringBuilder("std::function<")
                            .append(api.cppReturnTypename(functionType.returnType))
                            .append("(");
            builder.append(
                    functionType.parameters.stream()
                            .map(p -> api.cppTypename(p.type))
                            .collect(Collectors.joining(", ")));
            return builder.append(")>").toString();
        }
    }

    private class CTypenameWorker implements TypeWorker<String> {

        private boolean internal;

        public CTypenameWorker(boolean internal) {
            this.internal = internal;
        }

        @Override
        public ProxyContext getContext() {
            return api.getContext();
        }

        private String getNativeTypename(TypeExpr type) {
            TypeDecl typeDecl = getContext().getTypeDecl(type.getName());
            return api.nativeTypeName(((NativeTypeDecl) typeDecl).nativeType);
        }

        @Override
        public String numberType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String charType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String boolType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String enumType(TypeExpr type) {
            return api.nativeTypeName(NativeType.SINT32);
        }

        @Override
        public String stringType(TypeExpr type) {
            return "gnatpolyglot::ada::strings::string_data";
        }

        @Override
        public String arrayType(TypeExpr type) {
            return "gnatpolyglot::ada::arrays::array_data";
        }

        @Override
        public String classType(TypeExpr type) {
            return "gnatpolyglot::data*";
        }

        @Override
        public String pointerType(TypeExpr type) {
            StringBuilder builder = new StringBuilder(constness(type));
            if (getContext().isStringOrArray(type.pointedType()))
                builder.append(cTypename(type.pointedType()));
            else builder.append("gnatpolyglot::data*");
            return builder.toString();
        }

        @Override
        public String voidType(TypeExpr type) {
            return getNativeTypename(type);
        }

        @Override
        public String refType(TypeExpr refType) {
            return new TypeWorker.SubreferenceTypeWorker<String>() {

                @Override
                public ProxyContext getContext() {
                    return api.getContext();
                }

                @Override
                public String numberType(TypeExpr type) {
                    return new StringBuilder(constness(refType))
                            .append(cTypename(type))
                            .append("*")
                            .toString();
                }

                @Override
                public String charType(TypeExpr type) {
                    return new StringBuilder(constness(refType))
                            .append(cTypename(type))
                            .append("*")
                            .toString();
                }

                @Override
                public String boolType(TypeExpr type) {
                    return new StringBuilder(constness(refType))
                            .append(cTypename(type))
                            .append("*")
                            .toString();
                }

                @Override
                public String enumType(TypeExpr type) {
                    return "void *";
                }

                @Override
                public String stringType(TypeExpr type) {
                    return cTypename(type);
                }

                @Override
                public String arrayType(TypeExpr type) {
                    return cTypename(type);
                }

                @Override
                public String classType(TypeExpr type) {
                    return constness(refType).concat(cTypename(type));
                }

                @Override
                public String pointerType(TypeExpr type) {
                    if (refType.isConst()) return cTypename(type);
                    else if (getContext().isStringOrArray(type.pointedType()))
                        return constness(refType).concat("void *");
                    else return constness(refType).concat("gnatpolyglot::data **");
                }

                @Override
                public String functionType(TypeExpr type) {
                    return cTypename(type) + "*";
                }
            }.apply(refType.referencedType());
        }

        @Override
        public String functionType(TypeExpr type) {
            return "gnatpolyglot::callback_data";
        }
    }

    private CppAPI api;

    public TypenameGenerator(CppAPI api) {
        this.api = api;
    }

    public String cppTypename(TypeExpr type, boolean addLeadingColons) {
        return new CppTypenameWorker(addLeadingColons).apply(type);
    }

    public String cppReturnTypename(TypeExpr type) {
        return new CppTypenameWorker(true) {
            @Override
            public String refType(TypeExpr type) {
                // When returning a reference to a non-native type, we cannot allocate a new proxy
                // object and return a real C++ reference to it. Instead, return a `view` to the
                // returned pointer that acts as a reference and that won't free the underlying
                // pointer when destroyed.
                if (!getContext().isNativeScalar(type.referencedType())) {
                    return new StringBuilder(type.isConst() ? "const " : "")
                            .append(cppTypename(type.referencedType(), true))
                            .append("::view")
                            .toString();
                }
                return cppTypename(type, true);
            }
        }.apply(type);
    }

    public String cTypename(TypeExpr type) {
        return new CTypenameWorker(false).apply(type);
    }
}
