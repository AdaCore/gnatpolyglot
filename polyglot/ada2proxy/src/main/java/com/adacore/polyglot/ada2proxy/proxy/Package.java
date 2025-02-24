package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import java.util.List;

public class Package implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.PackageDecl origin;

    /** Declarations contained in the package. */
    public List<AdaDeclaration> declarations;

    public Package(Libadalang.PackageDecl origin, List<AdaDeclaration> declarations) {
        this.origin = origin;
        this.declarations = declarations;
    }

    /** Return the fully qualified name of the package */
    public String getFullyQualifiedName() {
        return origin.pFullyQualifiedName();
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Module toPolyglotProxy() {
        return new Module(
                Name.fromPascalWithUnderscore(origin.pRelativeName().getText()),
                declarations.stream().map(AdaDeclaration::toPolyglotProxy).toList(),
                AdaAPI.makeReferenceTo(origin, true));
    }
}
