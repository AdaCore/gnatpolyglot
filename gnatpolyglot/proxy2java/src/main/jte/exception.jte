<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate an exception.
--%>
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import java.util.List

@param JavaAPI api
@param ExceptionDecl exceptionDecl

!{
    FunctionMembersEntry members = api.getMembers(exceptionDecl);
    String excName = exceptionDecl.name.getLastName().toPascal();
}

package ${api.packagePath(exceptionDecl.name)};

public class ${excName} extends com.adacore.gnatpolyglot.runtime.ada2java.AdaException {

@for (var alloc : members.allocFunctions)
    !{
        String nativeFunc = "$%s".formatted(alloc.symbol);
    }

    private static native
    ${api.javaNativeTypename(alloc.type.returnType)} ${nativeFunc}(
        ${api.javaNativeParameters(alloc)}
    );

    public ${excName} (${api.javaParameters(alloc)}) {
        super(
            (com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer) null
        @for (var param : alloc.type.parameters)
            , ${param.name.toCamel()}
        @endfor
        );
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

    public ${excName} (
        com.adacore.gnatpolyglot.runtime.PolyglotData data
    @if (!alloc.type.parameters.isEmpty())
        , ${api.javaParameters(alloc)}
    @endif
    ) {
        super(
            data
        @for (var param : alloc.type.parameters)
            , ${param.name.toCamel()}
        @endfor
        );
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

@endfor

}
