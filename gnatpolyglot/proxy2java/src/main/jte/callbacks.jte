<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class for the Callbacks.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import com.adacore.gnatpolyglot.proxy2java.CallbackCollector
@import java.util.List

@param JavaAPI api
@param Proxy proxy

!{
    List<FunctionTypeExpr> callbacks = CallbackCollector.getCallbacks(proxy);
}

@template.text_header()

package ${api.basePackage()};

import java.util.function.Consumer;

/**
 * Class that holds the necessary functions to perform callbacks.
 */
public class Callbacks {

@for (var callback : callbacks)

    /** Functional interface of type ${api.getJavaGenericCallback(callback)} */
    public static interface ${api.getJavaCallbackTypename(callback)}
            extends ${api.getJavaGenericCallback(callback)} {
        class Ref extends ${api.getJavaGenericCallback(callback.makeReference(false))} {}

    !{ FunctionMembersEntry arrayFunctions = api.getArrayFunctions(callback); }
    @if (arrayFunctions != null)
        @template.array_class(api, api.getJavaCallbackTypename(callback), arrayFunctions, false)
    @endif
    }

    @template.callback_down(
        api,
        callback,
        api.getJavaDownCallbackName(callback),
        api.getJavaLambdaName(callback)
    )

    @template.callback(
        api,
        callback,
        api.getJavaUpCallbackName(callback),
        false
    )

@endfor

    static {
        ${api.basePackage()}.Library.loadLibrary();
    }
}
