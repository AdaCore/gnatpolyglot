<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class.
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ClassDecl.Inheritability
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Name
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
    FunctionMembersEntry ptrArrayFunctions = api.getPtrArrayFunctions(classDecl);
    FunctionDecl cloneFunction = api.getCloneCtor(classDecl, RoleKind.ALLOC);
    FunctionDecl shadowCloneFunction = api.getCloneCtor(classDecl, RoleKind.SHADOW_ALLOC);
    String interfaces = "";
    if (cloneFunction != null) {
        interfaces = " implements Cloneable ";
    }
}

package ${api.packagePath(classDecl.name)};

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiFunction;

public ${api.getOverridability(classDecl)}
class ${className} extends ${api.parentClass(classDecl)}${interfaces}{

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
        String nativeFunc = "$%s".formatted(alloc.symbol);
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
        _setData(
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
        String nativeShadow = "$%s".formatted(shadow.symbol);
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

        if (_isShadow()) {
            addr = ${api.callJavaNative(shadow, nativeShadow)};
        } else {
           addr = ${api.callJavaNative(alloc, nativeFunc)};
        }

        _setData(
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

@if (members.copyFunction != null)
    !{
        Name copyFromName = Name.fromCamel("copyFrom");
        String name = members.memberFunctions.stream()
            .filter(m -> m.name.getLastName().equals(copyFromName))
            .filter(m -> m.type.equals(members.copyFunction.type))
            .findFirst()
            .map(_ -> members.copyFunction.name.getLastName().toCamel())
            .orElse(copyFromName.toCamel());
    }
    @template.function(api, members.copyFunction, functionName = name)
@endif

@if (cloneFunction != null)
    !{
        String nativeFunc = "$%s".formatted(cloneFunction.symbol);
    }

    <%-- The Object.clone function is protected by default. Make it public if possible --%>
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected Function<com.adacore.gnatpolyglot.runtime.PolyglotData, Long> _getClone() {
        return (addr) -> ${nativeFunc}(addr.getAddress());
    }
@endif

@if (shadowCloneFunction != null)
    !{
        String nativeFunc = "$%s".formatted(shadowCloneFunction.symbol);
    }

    protected BiFunction<
                    com.adacore.gnatpolyglot.runtime.PolyglotData,
                    com.adacore.gnatpolyglot.runtime.PolyglotObject,
                    Long>
            _getCloneShadow() {
        return (addr, obj) -> ${nativeFunc}(addr.getAddress(), (${className}) obj);
    }
@endif

    !{
        FunctionDecl freeFunc = members.freeFunction;
        String freeNativeFunc = "$%s".formatted(freeFunc.symbol);
    }

    private static native
    ${api.javaNativeTypename(freeFunc.type.returnType)} ${freeNativeFunc}(
        ${api.javaNativeParameters(freeFunc)}
    );

    @Override
    protected Consumer<com.adacore.gnatpolyglot.runtime.PolyglotData> _getFree() {
        return (addr) -> ${freeNativeFunc}(addr.getAddress());
    }

@if (arrayFunctions != null)
    @template.array_class(api, classDecl, arrayFunctions, false)
@endif

@if (ptrArrayFunctions != null)
    @template.array_class(api, classDecl, ptrArrayFunctions, true)
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

        try {
        @if (api.returnsVoid(method.functionType))
            ${api.callJavaDispatch(method)};
        @else
            ${api.javaReturnTypename(method.functionType.returnType)} valueReturned =
                ${api.callJavaDispatch(method)};
            ${api.makeJavaDispatchReturnStatement(method, "valueReturned")}
        @endif
        } catch (com.adacore.gnatpolyglot.runtime.ada2java.AdaException exc$e) {
            com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation.setExceptionData(
                exc$e._release().getAddress()
            );
            ${api.makeJavaDispatchDefaultReturnStatement(method.functionType)}
        } catch (java.lang.Throwable exc$e) {
            com.adacore.gnatpolyglot.runtime.PolyglotKernel.ExceptionInformation.setExceptionData(
                new com.adacore.gnatpolyglot.runtime.ada2java.AdaException(
                    new com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString(
                        "Uncaught host language exception"
                    )
                )._release().getAddress()
            );
            ${api.makeJavaDispatchDefaultReturnStatement(method.functionType)}
        }
    }
@endfor
@endif

    @Override
    protected boolean _isShadow() {
         return !this.getClass().equals(${className}.class);
    }

    <%-- (eng/libadalang/gnatpolyglot#31):
      -- References to non-final types are not yet supported
      --%>
    @if(classDecl.inheritability == Inheritability.FINAL)
    public static final class Ref extends
            com.adacore.gnatpolyglot.runtime.ObjectRef<${className}> {

        public Ref(${className} obj) {
            super(obj);
        }

        private Ref(long addr) {
            super(
                addr == 0L
                    ? null
                    : new ${className}(
                        new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                            addr, com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.LIBRARY
                        )
                    )
            );
        }

        @Override
        protected void update(com.adacore.gnatpolyglot.runtime.PolyglotData data) {
            com.adacore.gnatpolyglot.runtime.PolyglotData oldData =
                get().map(o -> o._getData()).orElse(null);
            if (!java.util.Objects.deepEquals(data, oldData)) {
                if (data == null) {
                    this.obj = null;
                } else {
                    data.setOwner(com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.LIBRARY);
                    this.obj = new ${className}(
                        (com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer) data
                    );
                }
            }
        }

    }
    @endif

    static {
        ${api.basePackage()}.Library.loadLibrary();
    }

}
