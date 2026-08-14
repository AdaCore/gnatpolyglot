<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
Template file to generate a function.
--%>

@import com.adacore.gnatpolyglot.proxy.ClassDecl
@import com.adacore.gnatpolyglot.proxy.FullyQualifiedName
@import com.adacore.gnatpolyglot.proxy.FunctionDecl
@import com.adacore.gnatpolyglot.proxy.FunctionDecl.Overridability
@import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr
@import com.adacore.gnatpolyglot.proxy.Role.RoleKind
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param FunctionDecl function

@if (function.overridability != Overridability.ABSTRACT)

!{
    // If the function has a role, use the class's jclass, otherwise use the package class
    FullyQualifiedName owningClass =
        function.role == null
            ? function.name.getParentFullyQualifiedName()
            : function.role.type.getName();
}

extern ${api.cTypename(function.type.returnType)}
${function.symbol}(${api.cParameters(function)});

JNIEXPORT ${api.jniReturnTypename(function.type.returnType)}
${api.jniName(function)} (${api.jniParameters(function)}) {
@if (function.role != null && function.role.kind == RoleKind.SHADOW_ALLOC)
<%-- Shadow objects must keep a reference to the Java VM in order to get a new
  -- JNIEnv for making upcalls to the JVM when dispatching.
  --%>
  JavaVM *_java_vm;
  (*env)->GetJavaVM(env, &_java_vm);
@endif
@for (var param : function.type.parameters)
    ${api.makeJNIParamConversion(param, function)};
@endfor
@if (api.returnsVoid(function.type))
    ${api.callCSymbol(function)};
@else
    ${api.cTypename(function.type.returnType)} value_returned =
        ${api.callCSymbol(function)};
    ${api.makeCReturnStatement(function.type, "value_returned")}
@endif
@for (var param : function.type.parameters)
    ${api.makeJNIParamUpdate(param)}
@endfor
}

@endif
