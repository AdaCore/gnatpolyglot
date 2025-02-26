package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.EnumItem;
import com.adacore.polyglot.proxy.Name;

public class EnumLiteral implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.EnumLiteralDecl origin;

    /** Name of the enumeration item. */
    public Name name;

    /** Integer value of the enumeration item. */
    public int value;

    public EnumLiteral(Libadalang.EnumLiteralDecl origin, Name name, int value) {
        this.origin = origin;
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public EnumItem toPolyglotProxy() {
        return new EnumItem(name, value, origin.pDoc());
    }
}
