package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.polyglot.proxy.Proxy;
import java.util.List;

public class AdaProxy implements AdaProxyObject {

    /** List of Ada packages to generate in the proxy. */
    public List<Package> packages;

    public AdaProxy(List<Package> declarations) {
        this.packages = declarations;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Proxy toPolyglotProxy() {
        return new Proxy(packages.stream().map(Package::toPolyglotProxy).toList());
    }
}
