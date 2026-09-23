<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>
@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.Module
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@param CppAPI api
@param Module module

@template.text_header()

@for (var include : api.getIncludes(module, false))
#include "${include}"
@endfor

#include <cstdint>
#include <functional>
#include <gnatpolyglot.h>
#include <gnatpolyglot_ada_arrays.h>
#include <gnatpolyglot_ada_exceptions.h>

#include "gnatpolyglot_exceptions_${api.getProjectName().toLower()}.h"

namespace ${api.namespacePath(module)} {

@for (var decl : module.declarations)
    !{FunctionDecl functionDecl = (FunctionDecl) decl;}
    @if(functionDecl.role.kind == RoleKind.FREE)
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template<>
        ${api.cppTypename(functionDecl.role.type)}::~polyglot_array() {
            ${functionDecl.symbol}(this->_data);
        }

    @elseif(functionDecl.role.kind == RoleKind.ALLOC)
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template<>
        ${api.cppTypename(functionDecl.role.type)}::polyglot_array
            (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)}
            : _data(${api.callCSymbol(functionDecl)}) { }
    @else
        extern "C" ${api.cTypename(functionDecl.type.returnType)} ${functionDecl.symbol}
            (${api.cParameters(functionDecl)});
        template <>
        ${api.cppReturnTypename(functionDecl.type.returnType)}
        ${api.functionDefinitionName(functionDecl)}
            (${api.cppParameters(functionDecl)}) ${api.getConstMethod(functionDecl)} {
        @for(var param : functionDecl.type.parameters)
            ${api.verifyOwnership(param)}
            ${api.createPointerBuffer(param, functionDecl)}
        @endfor
        @if (api.returnsVoid(functionDecl.type))
            ${api.callCSymbol(functionDecl)};
        @else
            ${api.cTypename(functionDecl.type.returnType)} returned_value =
                ${api.callCSymbol(functionDecl)};
            ${api.makeReturnStatement(functionDecl, "returned_value")};
        @endif
        }
    @endif

    @if (functionDecl.role.kind == RoleKind.GETTER &&
            functionDecl.role.type.elementType().isFunction())
        template <>
        void ${api.cppTypename(functionDecl.role.type)}::set(
                std::int32_t, ${api.cppTypename(functionDecl.type.returnType.makeReference(true))}) {
            throw gnatpolyglot::ada::exceptions::ConstraintError("functions cannot be escaped.");
        }
    @endif
@endfor

} // namespace ${api.namespacePath(module)}

