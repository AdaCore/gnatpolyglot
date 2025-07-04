package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.Name;

public class Component implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.ComponentDecl origin;

    /** Name of the Ada component */
    public Name name;

    public Component(Libadalang.ComponentDecl origin, Name name) {
        this.origin = origin;
        this.name = name;
    }

    /** Return the type of the component. */
    public Libadalang.BaseTypeDecl getType() {
        return this.origin.pFormalType(Libadalang.AdaNode.NONE);
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getDoc() {
        return origin.pDoc();
    }
}
