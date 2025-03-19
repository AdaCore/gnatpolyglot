package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.Name;
import java.util.List;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    public Record(Libadalang.TypeDecl origin, Name name) {
        super(name);
        this.origin = origin;
    }

    /** Return the fully qualified name of the type. */
    public String getFullyQualifiedName() {
        return this.origin.pFullyQualifiedName();
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public ClassDecl toPolyglotProxy() {
        if (this.origin.fTypeDef() instanceof Libadalang.PrivateTypeDef def) {
            return new ClassDecl(
                    AdaAPI.makeProxyFullyQualifiedName(origin, false),
                    this.origin.pDoc(),
                    null,
                    8,
                    false,
                    List.of());
        }
        throw new UnsupportedOperationException(
                "Unsupported Ada type:" + this.origin.fTypeDef().getImage());
    }
}
