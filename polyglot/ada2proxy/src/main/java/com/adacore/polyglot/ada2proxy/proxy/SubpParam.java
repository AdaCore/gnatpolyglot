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

    public SubpParam(Libadalang.ParamSpec origin, Name name, Transfer transfer) {
        this.origin = origin;
        this.name = name;
        this.transfer = transfer;
    }

    public Libadalang.BaseTypeDecl getType() {
        return origin.pFormalType(Libadalang.AdaNode.NONE);
    }

    public Libadalang.TypeExpr getTypeExpr() {
        return origin.fTypeExpr();
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
