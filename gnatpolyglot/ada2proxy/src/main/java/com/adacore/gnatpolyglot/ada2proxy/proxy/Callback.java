//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.libadalang.Libadalang;

public class Callback extends AdaDeclaration {

    /** Type declaration of origin in the LAL tree. */
    public Libadalang.BaseTypeDecl origin;

    public Callback(Libadalang.BaseTypeDecl accessSubpType) {
        super(AdaAPI.getName(accessSubpType.pDefiningName()));
        this.origin = accessSubpType;
    }

    /** Return the SubpSpec of the access to subprogram type. */
    public static Libadalang.BaseSubpSpec getSpec(Libadalang.BaseTypeDecl type) {
        Libadalang.TypeDecl typeDecl = (Libadalang.TypeDecl) type.pRootType(type);
        Libadalang.AccessToSubpDef accessType = (Libadalang.AccessToSubpDef) typeDecl.fTypeDef();
        return accessType.fSubpSpec();
    }

    /** Instance variant of {@link #getSpec(Libadalang.BaseTypeDecl)}. */
    public Libadalang.BaseSubpSpec getSpec() {
        return getSpec(origin);
    }

    /** Return the name to use for the C ABI wrapper function, used for returning callbacks. */
    public static String callbackCName(Libadalang.BaseTypeDecl type) {
        StringBuilder builder =
                new StringBuilder(type.pRelativeName().getText() + "_Proxy_C_Callback");
        return builder.toString();
    }

    /** Return the name to use for the Ada wrapper function, used for receiving callbacks. */
    public static String callbackAdaName(Libadalang.BaseTypeDecl type) {
        StringBuilder builder =
                new StringBuilder(type.pRelativeName().getText() + "_Proxy_Ada_Callback");
        return builder.toString();
    }

    /**
     * Instance variant of {@link #callbackCName(com.adacore.libadalang.Libadalang.BaseTypeDecl)}.
     */
    public String callbackCName() {
        return callbackCName(origin);
    }

    /**
     * Instance variant of {@link #callbackAdaName(com.adacore.libadalang.Libadalang.BaseTypeDecl)}.
     */
    public String callbackAdaName() {
        return callbackAdaName(origin);
    }

    /** Return the fully qualified typename of the callback. */
    public String callbackTypename() {
        return origin.pFullyQualifiedName();
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return AdaAPI.getDoc(origin);
    }
}
