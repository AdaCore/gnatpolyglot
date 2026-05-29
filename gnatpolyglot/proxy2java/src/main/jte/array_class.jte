<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param ClassDecl classDecl
@param FunctionMembersEntry arrayFunctions

!{
    String className = classDecl.name.getLastName().toPascal();
}

    public static class Array extends
            com.adacore.gnatpolyglot.runtime.ada2java.PolyglotArray<${className}> {
        /**
         * Internal use only.
         *
         * <p> Create an array from an existing proxy data.
         */
        public Array(com.adacore.gnatpolyglot.runtime.ada2java.ArrayData data) {
            super(data);
        }

        /**
         * Internal use only.
         *
         * <p> Create an array from an existing proxy data and a parent object
         * that represents the root object that owns the memory of the array.
         */
        public Array(
            com.adacore.gnatpolyglot.runtime.ada2java.ArrayData data,
            com.adacore.gnatpolyglot.runtime.PolyglotObject parent
        ) {
            super(data, parent);
        }

    @for (var alloc : arrayFunctions.allocFunctions)
        !{
            String nativeFunc = "$%s".formatted(alloc.symbol);
        }

        private static native
        ${api.javaNativeTypename(alloc.type.returnType)} ${nativeFunc}(
            ${api.javaNativeParameters(alloc)}
        );

        public Array (${api.javaParameters(alloc)}) {
            super((com.adacore.gnatpolyglot.runtime.ada2java.ArrayData) null);
        @for (var param : alloc.type.parameters)
            ${api.makeJavaParamConversion(param, false)};
        @endfor
            ${api.javaNativeTypename(alloc.type.returnType)} data =
                ${api.callJavaNative(alloc, nativeFunc)};
            data.setOwner(
                com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER
            );
            setData(data);
        }
    @endfor

    @for (var method : arrayFunctions.memberFunctions)
        !{
            String functionName = method.name.getLastName().toCamel();
            String nativeFunc = "$%s".formatted(method.symbol);
            boolean firstMethodParam = true;
        }

        private static native
        ${api.javaNativeReturnTypename(method.type.returnType)} ${nativeFunc}(
            ${api.javaNativeParameters(method)}
        );

        @if (method.role.kind == RoleKind.GETTER)

            @Override
            public ${className} getUnslided(
                ${api.javaParameters(method)}
            ) {
            @for (var param : method.type.parameters)
                ${api.makeJavaParamConversion(param, firstMethodParam)};
                !{firstMethodParam = false;}
            @endfor
                ${api.javaNativeReturnTypename(method.type.returnType)}
                    valueReturned = ${api.callJavaNative(method, nativeFunc)};
                ${api.makeJavaReturnStatement(method, "valueReturned")}
            }

        @elseif (method.role.kind == RoleKind.SETTER)

            @Override
            public ${className} setUnslided(
                ${api.javaParameters(method)}
            ) {
            @for (var param : method.type.parameters)
                ${api.makeJavaParamConversion(param, firstMethodParam)};
                !{firstMethodParam = false;}
            @endfor
                ${className} previousValue =
                    getUnslided(${method.type.parameters.get(1).name.toCamel()});
                ${api.callJavaNative(method, nativeFunc)};
                return previousValue;
            }

        @endif
    @endfor

        !{
            FunctionDecl freeFunc = arrayFunctions.freeFunction;
            String freeNativeFunc = "$%s".formatted(freeFunc.symbol);
        }

        private static native
        ${api.javaNativeTypename(freeFunc.type.returnType)} ${freeNativeFunc}(
            ${api.javaNativeParameters(freeFunc)}
        );

        @Override
        protected Consumer<com.adacore.gnatpolyglot.runtime.PolyglotData> getFree() {
            return (addr) -> ${freeNativeFunc}(
               (com.adacore.gnatpolyglot.runtime.ada2java.ArrayData) addr
            );
        }

        @Override
        public java.util.List<${className}> subList(int fromIndex, int toIndex) {
            return new Array(
                new com.adacore.gnatpolyglot.runtime.ada2java.ArrayData(
                    getBegin(),
                    getEnd() - 1,
                    getData().addr,
                    com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.STATIC
                ),
                this.parent
            );
        }

    }

