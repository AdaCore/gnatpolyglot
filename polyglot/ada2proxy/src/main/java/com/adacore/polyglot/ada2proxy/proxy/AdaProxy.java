package com.adacore.polyglot.ada2proxy.proxy;

import java.util.List;

public class AdaProxy implements AdaProxyObject {

    /** List of Ada packages to generate in the proxy. */
    public List<Package> packages;

    /**
     * List of all Array types used.
     *
     * <p>In the visited code, there may be multiple occurences of different array with the same
     * component type. In the proxy, these will all result in the same type. Generating functions
     * for each Ada arrray type could result in multiple definitions of the same function in the
     * resulting proxy. This is the resulting list of all unique array types that will be in the
     * proxy for which the functions will be generated.
     */
    public List<Array> arrayTypes;

    public AdaProxy(List<Package> declarations, List<Array> arrayTypes) {
        this.packages = declarations;
        this.arrayTypes = arrayTypes;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
