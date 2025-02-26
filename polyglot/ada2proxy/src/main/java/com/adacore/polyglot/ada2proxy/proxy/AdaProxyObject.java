package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.polyglot.proxy.ProxyObject;

/** Interface to visit the Proxy. */
public interface AdaProxyObject {
    <T> T accept(AdaProxyVisitor<T> visitor);

    /** Convert the {@link AdaProxyObject} to the corresponding {@link ProxyObject}. */
    ProxyObject toPolyglotProxy();
}
