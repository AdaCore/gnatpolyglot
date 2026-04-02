<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.ExceptionDecl
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@import com.adacore.gnatpolyglot.proxy2cpp.ExceptionCollector
@import java.util.List
@param CppAPI api
@param Proxy proxy
@param Boolean isSource

!{
    List<ExceptionDecl> exceptions = ExceptionCollector.getExceptions(proxy);
    String projectName = proxy.name.toLower();
}

@template.text_header()

@if(!isSource)
#ifndef GNATPOLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H
#define GNATPOLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H

namespace gnatpolyglot::exceptions::${projectName} {

void rethrow_if_exc();

} // namespace gnatpolyglot::exceptions::${projectName}

#endif /* ! GNATPOLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H */
@else

#include <gnatpolyglot.h>

#include "gnatpolyglot_ada_exceptions.h"
#include "gnatpolyglot_exceptions_${api.getProjectName().toLower()}.h"

@for(var inc : ExceptionCollector.getIncludes(proxy))
#include "${inc}"
@endfor

namespace gnatpolyglot::exceptions::${projectName} {

enum class exception_kind {
    anon_kind = 0
@for(var exc: exceptions)
    , ${api.exceptionEnumIdentifier(exc)} = ${String.valueOf(exc.enumValue)}
@endfor
};

void rethrow_if_exc() {
    kernel *k = gnatpolyglot_get_kernel();
    void *data = k->exc_info.exception_data;
    char *message =k->exc_info.message;
    if (data == nullptr)
        return;
    int kind = k->exc_info.exception_kind;
    if (kind < 0)
        gnatpolyglot::ada::exceptions::rethrow_standard_exception();
    k->exc_info.exception_data = nullptr;
    try {
        switch (kind) {
        @for(var exc : exceptions)
            case static_cast<int>(exception_kind::${api.exceptionEnumIdentifier(exc)}):
                throw ${api.exceptionFullyQualifiedName(exc)} (data, message);
        @endfor
            default:
                throw gnatpolyglot::ada::exceptions::AdaException(data, message);
        }
    } catch (const std::exception&) {
        k->exc_info.clear_exception(k);
        throw;
    }
}

} // namespace gnatpolyglot::exceptions::${projectName}
@endif
