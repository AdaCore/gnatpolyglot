//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Transfer;
import com.adacore.libadalang.Libadalang;

public class SubpParam implements AdaProxyObject {

    public enum Mode {
        IN,
        OUT,
        INOUT,
    }

    /** Origin node in the LAL tree. */
    private final Libadalang.BaseFormalParamDecl origin;

    /** Name of the Subprogram parameter. */
    public Name name;

    /** Parameter ownership transfer information. */
    public Transfer transfer;

    /**
     * Type to use instead of the origin's type.
     *
     * <p>When a primitive is duplicated to be available on derived types, we need to override the
     * type of parameter to the derived type instead of the base type.
     */
    private Libadalang.BaseTypeDecl type;

    public SubpParam(
            Libadalang.BaseFormalParamDecl origin,
            Name name,
            Transfer transfer,
            Libadalang.BaseTypeDecl type) {
        this.origin = origin;
        this.name = name;
        this.transfer = transfer;
        this.type = type;
    }

    public Libadalang.BaseFormalParamDecl getOrigin() {
        return origin;
    }

    public Libadalang.BaseTypeDecl getType() {
        return type;
    }

    public void setType(Libadalang.BaseTypeDecl type) {
        this.type = type;
    }

    /** Return whether {@code param} is an {@code out} or {@code in out} parameter. */
    public static boolean isOutMode(Libadalang.BaseFormalParamDecl param) {
        return getMode(param) == Mode.OUT || getMode(param) == Mode.INOUT;
    }

    /**
     * Instance variant of {@link
     * #isOutMode(com.adacore.libadalang.Libadalang.BaseFormalParamDecl)}.
     */
    public boolean isOutMode() {
        return getMode() == Mode.OUT || getMode() == Mode.INOUT;
    }

    /** Return the {@link Mode} of the given parameter. */
    public static Mode getMode(Libadalang.BaseFormalParamDecl param) {
        if (param instanceof Libadalang.ParamSpec p) {
            return switch (p.fMode()) {
                case Libadalang.ModeOut m -> Mode.OUT;
                case Libadalang.ModeInOut m -> Mode.INOUT;
                default -> Mode.IN;
            };
        }
        return Mode.IN;
    }

    /**
     * Instance variant of {@link #getMode(com.adacore.libadalang.Libadalang.BaseFormalParamDecl)}.
     */
    public Mode getMode() {
        return getMode(origin);
    }

    /** Return whether {@code paramDecl} is aliased */
    public static boolean isAliased(Libadalang.BaseFormalParamDecl paramDecl) {
        return paramDecl instanceof Libadalang.ParamSpec param && param.fHasAliased().pAsBool();
    }

    /**
     * Instance variant of {@link
     * #isAliased(com.adacore.libadalang.Libadalang.BaseFormalParamDecl)}.
     */
    public boolean isAliased() {
        return isAliased(origin);
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
