package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.TypeExpr;

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

    public boolean isOutMode() {
        return origin.fMode() instanceof Libadalang.ModeOut
                || origin.fMode() instanceof Libadalang.ModeInOut;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Parameter toPolyglotProxy() {
        TypeExpr typeRef = AdaAPI.makeTypeExpr(origin.fTypeExpr());
        NativeType type = AdaAPI.checkNativeType(origin.pFormalType(Libadalang.AdaNode.NONE));
        boolean isConst = false;
        // If the parameter has the mode ``in`` or default, it is constant.
        if (origin.fMode() instanceof Libadalang.ModeIn
                || origin.fMode() instanceof Libadalang.ModeDefault) isConst = true;
        // If the parameter is not a scalar, or has ``out`` or ``in out`` mode, it must be a
        // reference.
        if (type == null || isOutMode()) typeRef = typeRef.makeReference(isConst);
        return new Parameter(name, typeRef, transfer);
    }
}
