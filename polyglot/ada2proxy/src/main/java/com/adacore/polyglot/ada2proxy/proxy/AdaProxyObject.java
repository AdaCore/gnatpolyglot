package com.adacore.polyglot.ada2proxy.proxy;

/** Interface to visit the Proxy. */
public interface AdaProxyObject {
    <T> T accept(AdaProxyVisitor<T> visitor);
}
