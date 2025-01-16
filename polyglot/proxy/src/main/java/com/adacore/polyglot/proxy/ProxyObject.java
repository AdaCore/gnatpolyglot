package com.adacore.polyglot.proxy;

public interface ProxyObject {
    public <T> T visit(ProxyVisitor<T> v);
}
