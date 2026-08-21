<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
--%>

@import com.adacore.gnatpolyglot.NativeType
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy.ArrayTypeExpr
@import com.adacore.gnatpolyglot.proxy2cpp.CppAPI
@param CppAPI api
@param FunctionTypeExpr functionType
@param String callbackName
@param boolean isDispatch = false
@param String callee = null

!{
    String calleeType;
    if (isDispatch) {
        callee = "__self->" + callee;
        calleeType =  api.cppTypename(functionType.parameters.get(0).type.referencedType());
    } else {
        callee = "(*__callback)";
        calleeType = api.cppTypename(functionType);
    }
}

static
${api.cTypename(functionType.returnType)}
${callbackName} (
    ${api.upcallParameters(functionType, isDispatch)}
) {
    <%-- When using dynamic dispatch, the first argument is the C++ object.
      -- The function called at the end is one of its method.
      -- When using callbacks, the first argument is a pointer to the called
      -- `std::function` of type `functionType`.
      --%>
    @if (isDispatch)
    ${calleeType} *__self = (${calleeType} *) _self;
    @else
    ${calleeType} *__callback = (${api.cppTypename(functionType)} *) _data.data;
    @endif
    @for(var param : functionType.parameters.stream().skip(isDispatch ? 1 : 0).toList())
    ${api.convertForUpcall(param)};
    @endfor
    try {
    @if(api.returnsVoid(functionType))
        ${api.makeUpcall(functionType, callee, isDispatch)};
    @else
        ${api.cppReturnTypename(functionType.returnType)} returned_value =
          ${api.makeUpcall(functionType, callee, isDispatch)};
        ${api.makeReturnFromDispatch(functionType, "returned_value")}
    @endif
    } catch (gnatpolyglot::ada::exceptions::AdaException &e) {
        gnatpolyglot_get_kernel()->exc_info.exception_data = e.release_();
        ${api.makeDispatchDefaultReturn(functionType)}
    } catch (...) {
        gnatpolyglot::ada::exceptions::AdaException error(
            "Uncaught host language exception"
        );
        gnatpolyglot_get_kernel()->exc_info.exception_data = error.release_();
        ${api.makeDispatchDefaultReturn(functionType)}
    }

    @for(var param : functionType.parameters.stream().skip(isDispatch ? 1 : 0).toList())
    ${api.checkDispatchPointerValue(param)};
    @endfor
}

