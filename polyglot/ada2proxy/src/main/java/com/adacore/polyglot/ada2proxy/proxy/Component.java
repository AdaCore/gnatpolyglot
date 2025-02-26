package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.Field;
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

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Field toPolyglotProxy() {
        return new Field(name, origin.pDoc(), AdaAPI.makeReferenceTo(origin.pTypeExpression()));
    }
}
