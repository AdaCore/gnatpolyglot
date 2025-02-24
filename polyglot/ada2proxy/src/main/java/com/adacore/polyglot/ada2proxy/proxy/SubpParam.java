package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Parameter;
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

    public Libadalang.BaseTypeDecl getFormalType() {
        return origin.pFormalType(Libadalang.AdaNode.NONE);
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Parameter toPolyglotProxy() {
        return new Parameter(name, AdaAPI.makeReferenceTo(origin.fTypeExpr()), transfer);
    }
}
