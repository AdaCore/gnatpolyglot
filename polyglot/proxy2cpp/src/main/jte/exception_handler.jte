<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.ExceptionDecl
@import com.adacore.polyglot.proxy.Proxy
@import com.adacore.polyglot.proxy2cpp.CppAPI
@import com.adacore.polyglot.proxy2cpp.ExceptionCollector
@import java.util.List
@param CppAPI api
@param Proxy proxy
@param Boolean isSource

!{
    List<ExceptionDecl> exceptions = ExceptionCollector.getExceptions(proxy);
    String projectName = proxy.name.toLower();
}

@if(!isSource)
#ifndef POLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H
#define POLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H

namespace polyglot::exceptions::${projectName} {

void rethrow_if_exc();

} // namespace polyglot::exceptions::${projectName}

#endif /* ! POLYGLOT_EXCEPTIONS_${projectName.toUpperCase()}_H */
@else

#include <polyglot.h>

#include "polyglot_ada_exceptions.h"
#include "polyglot_exceptions_${api.getProjectName().toLower()}.h"

@for(var inc : ExceptionCollector.getIncludes(proxy))
#include "${inc}"
@endfor

namespace polyglot::exceptions::${projectName} {

enum class exception_kind {
    anon_kind = 0
@for(var exc: exceptions)
    , ${api.exceptionEnumIdentifier(exc)} = ${String.valueOf(exc.enumValue)}
@endfor
};

void rethrow_if_exc() {
    kernel *k = polyglot_get_kernel();
    void *data = k->exc_info.exception_data;
    char *message =k->exc_info.message;
    if (data == nullptr)
        return;
    int kind = k->exc_info.exception_kind;
    if (kind < 0)
        polyglot::ada::exceptions::rethrow_standard_exception();
    k->exc_info.exception_data = nullptr;
    try {
        switch (kind) {
        @for(var exc : exceptions)
            case static_cast<int>(exception_kind::${api.exceptionEnumIdentifier(exc)}):
                throw ${api.exceptionFullyQualifiedName(exc)} (data, message);
        @endfor
            default:
                throw polyglot::ada::exceptions::AdaException(data, message);
        }
    } catch (const std::exception&) {
        k->exc_info.clear_exception(k);
        throw;
    }
}

} // namespace polyglot::exceptions::${projectName}
@endif
