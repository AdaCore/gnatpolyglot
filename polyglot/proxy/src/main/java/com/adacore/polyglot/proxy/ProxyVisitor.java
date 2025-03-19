package com.adacore.polyglot.proxy;

/** Interface to visit the Proxy. */
public interface ProxyVisitor<T> {
    public abstract T visit(Proxy proxy);

    public abstract T visit(Module module);

    public abstract T visit(FunctionDecl functionDecl);

    public abstract T visit(ClassDecl classDecl);

    public abstract T visit(EnumerationDecl enumerationDecl);

    public abstract T visit(Role role);

    public abstract T visit(Field field);

    public abstract T visit(EnumItem enumItem);

    public abstract T visit(Reference reference);

    public abstract T visit(Transfer transfer);

    public abstract T visit(Parameter parameter);

    public abstract T visit(FullyQualifiedName name);
}
