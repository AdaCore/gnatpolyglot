package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.Name;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    public Record(Libadalang.TypeDecl origin, Name name) {
        super(name);
        this.origin = origin;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public ClassDecl toPolyglotProxy() {
        // TODO: Support record types
        throw new UnsupportedOperationException("Record types are not yet supported");
    }
}
