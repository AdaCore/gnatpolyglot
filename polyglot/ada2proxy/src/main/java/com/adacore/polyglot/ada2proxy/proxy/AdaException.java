package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;

public class AdaException extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.ExceptionDecl origin;

    /** Enum value in the proxy. */
    private final int value;

    public AdaException(Name name, Libadalang.ExceptionDecl origin, int value) {
        super(name);
        this.origin = origin;
        this.value = value;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return origin.pDoc();
    }

    public int getValue() {
        return value;
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(origin)
                .getParentFullyQualifiedName()
                .append(name);
    }
}
