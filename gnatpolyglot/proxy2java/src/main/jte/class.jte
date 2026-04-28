<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param ClassDecl classDecl

!{
    FunctionMembersEntry members = api.getMembers(classDecl);
    String className = classDecl.name.getLastName().toPascal();
    FunctionMembersEntry arrayFunctions = api.getArrayFunctions(classDecl);
}

package ${api.packagePath(classDecl)};

import java.util.function.Consumer;

public ${api.getOverridability(classDecl)}
class ${className} extends ${api.parentClass(classDecl)}{

    /** Internal use only.
     *
     * <p> Create a ${className} from an existing proxy data.
     */
    public ${className} (com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer data) {
        super((com.adacore.gnatpolyglot.runtime.PolyglotData) data);
    }

    /** Internal use only.
     *
     * <p> Create a ${className} from an existing proxy data.
     */
    public ${className} (
        com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer data,
        com.adacore.gnatpolyglot.runtime.PolyglotObject parent
    ) {
        super((com.adacore.gnatpolyglot.runtime.PolyglotData) data, parent);
    }

@for (var alloc : members.allocFunctions)
    !{
        String nativeFunc = "$%s".formatted(alloc.name.getLastName().toCamel());
    }

    private static native
    ${api.javaNativeTypename(alloc.type.returnType)} ${nativeFunc}(
        ${api.javaNativeParameters(alloc)}
    );

    public ${className} (${api.javaParameters(alloc)}) {
        super((com.adacore.gnatpolyglot.runtime.PolyglotData) null);
    @for (var param : alloc.type.parameters)
        ${api.makeJavaParamConversion(param, false)};
    @endfor
        setData(
            new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                ${api.callJavaNative(alloc, nativeFunc)},
                com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER
            )
        );
    }
@endfor

@for (var method : members.memberFunctions)
    @template.function(api, method)
@endfor

    !{
        FunctionDecl freeFunc = members.freeFunction;
        String freeNativeFunc = "$%s".formatted(freeFunc.name.getLastName().toCamel());
    }

    private static native
    ${api.javaNativeTypename(freeFunc.type.returnType)} ${freeNativeFunc}(
        ${api.javaNativeParameters(freeFunc)}
    );

    @Override
    protected Consumer<com.adacore.gnatpolyglot.runtime.PolyglotData> getFree() {
        return (addr) -> ${freeNativeFunc}(addr.getAddress());
    }

@if (arrayFunctions != null)
    @template.array_class(api, classDecl, arrayFunctions)
@endif

    static {
        ${api.basePackage()}.Library.loadLibrary();
    }

}
