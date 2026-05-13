<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ClassDecl.Inheritability
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import java.util.List

@param JavaAPI api
@param ClassDecl classDecl

!{
    FunctionMembersEntry members = api.getMembers(classDecl);
    String className = classDecl.name.getLastName().toPascal();
    FunctionMembersEntry arrayFunctions = api.getArrayFunctions(classDecl);
}

package ${api.packagePath(classDecl.name)};

import java.util.function.Consumer;

public ${api.getOverridability(classDecl)}
class ${className} extends ${api.parentClass(classDecl)}{

    /** Internal use only.
     *
     * <p> Create a ${className} from an existing proxy data.
     */
    public ${className} (com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer data) {
        super(data);
    }

    /** Internal use only.
     *
     * <p> Create a ${className} from an existing proxy data.
     */
    public ${className} (
        com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer data,
        com.adacore.gnatpolyglot.runtime.PolyglotObject parent
    ) {
        super(data, parent);
    }

!{
    // If the class is abstract, then it will not have any ALLOC functions. Instead,
    // use the SHADOW_ALLOC functions as if they were ALLOC functions.
    //
    List<FunctionDecl> allocFunctions = classDecl.inheritability == Inheritability.VIRTUAL
        ? members.allocFunctions
        : members.allocFunctions.stream().filter(a -> a.role.kind == RoleKind.ALLOC).toList();
}
@for (var alloc : allocFunctions)
    !{
        String nativeFunc = "$%s".formatted(alloc.name.getLastName().toCamel());
        FunctionDecl shadow = alloc.role.kind == RoleKind.SHADOW_ALLOC
            ? null
            : api.getMatchingShadowAlloc(alloc);
    }

    private static native
    ${api.javaNativeTypename(alloc.type.returnType)} ${nativeFunc}(
        ${api.javaNativeParameters(alloc)}
    );

    @if (shadow == null || alloc.role.kind == RoleKind.SHADOW_ALLOC)

    public ${className} (${api.javaParameters(alloc)}) {
        super((com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer) null);
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

    @else

    <%-- In java, it is possible to know whether a constructor was called from
      -- a child type's ctor by checking the type of the object. This allows to
      -- only generate one constructor that chooses which native function to
      -- call
      --%>

    !{
        String nativeShadow = "$%s".formatted(shadow.name.getLastName().toCamel());
    }

    private static native
    ${api.javaNativeTypename(shadow.type.returnType)} ${nativeShadow}(
        ${api.javaNativeParameters(shadow)}
    );

    public ${className} (${api.javaParameters(alloc)}) {
        super((com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer) null);
    @for (var param : alloc.type.parameters)
        ${api.makeJavaParamConversion(param, false)};
    @endfor

        long addr;

        if (this.getClass().equals(${className}.class)) {
            addr = ${api.callJavaNative(alloc, nativeFunc)};
        } else {
            addr = ${api.callJavaNative(shadow, nativeShadow)};
        }

        setData(
            new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                addr,
                com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER
            )
        );
    }

    @endif
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

@if(classDecl.inheritability != Inheritability.FINAL)
@for (var method : classDecl.vtable)
    !{
        String functionName = method.name.toCamel();
    }

    private static ${api.javaNativeReturnTypename(method.functionType.returnType)}
    dispatch$${functionName} (${className} _self${api.javaDispatchParameters(method.functionType)}) {
    @for (var param : method.functionType.parameters.stream().skip(1).toList())
        ${api.makeJavaDispatchParamConversion(param)}
    @endfor

    @if (api.returnsVoid(method.functionType))
        ${api.callJavaDispatch(method)};
    @else
        ${api.javaReturnTypename(method.functionType.returnType)} valueReturned =
            ${api.callJavaDispatch(method)};
        ${api.makeJavaDispatchReturnStatement(method, "valueReturned")}
    @endif
    }
@endfor
@endif

    static {
        ${api.basePackage()}.Library.loadLibrary();
    }

}
