package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.Mode;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Transfer;

public class SubpParam implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.ParamSpec origin;

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

    public SubpParam(Libadalang.ParamSpec origin, Name name, Transfer transfer) {
        this.origin = origin;
        this.name = name;
        this.transfer = transfer;
        this.type = origin.pFormalType(Libadalang.AdaNode.NONE);
    }

    public Libadalang.BaseTypeDecl getType() {
        return type;
    }

    public void setType(Libadalang.BaseTypeDecl type) {
        this.type = type;
    }

    public boolean isOutMode() {
        return getMode() instanceof Libadalang.ModeOut || getMode() instanceof Libadalang.ModeInOut;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Mode getMode() {
        return origin.fMode();
    }
}
