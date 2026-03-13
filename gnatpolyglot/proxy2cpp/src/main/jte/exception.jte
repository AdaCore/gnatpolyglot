<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@param CppAPI api
@param ExceptionDecl exception
@param Boolean isSource

!{
    FunctionMembersEntry members = api.getMembers(exception);
    String excName = exception.name.getLastName().toPascal();
}

@if(!isSource)
    class ${excName} : public gnatpolyglot::ada::exceptions::AdaException {
    public:
    @for(var alloc : members.allocFunctions)
        ${excName}(${api.cppParameters(alloc)});
        ${excName}(
            void *data
        @if(!alloc.type.parameters.isEmpty())
            , ${api.cppParameters(alloc)}
        @endif
        );
    @endfor
    };
@else
    @for(var alloc : members.allocFunctions)
        extern "C" void *${alloc.symbol}
            (${api.cParameters(alloc)});
        ${excName}::${excName}(${api.cppParameters(alloc)})
            : gnatpolyglot::ada::exceptions::AdaException(${api.callCSymbol(alloc)})
        {}

        ${excName}::${excName}(
            void *data
        @if(!alloc.type.parameters.isEmpty())
            , ${api.cppParameters(alloc)}
        @endif
        )
            : gnatpolyglot::ada::exceptions::AdaException(
                data
                @for(var param : alloc.type.parameters)
                    , ${param.name.toLower()}
                @endfor
            )
        {}
    @endfor
@endif
