package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.polyglot.proxy.Declaration;
import com.adacore.polyglot.proxy.Name;

public abstract class AdaDeclaration implements AdaProxyObject {

    /** Name of the declaration. */
    public Name name;

    public AdaDeclaration(Name name) {
        this.name = name;
    }

    @Override
    public abstract Declaration toPolyglotProxy();
}
