package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;

public class UnbindableDeclException extends RuntimeException {

    private Libadalang.BasicDecl decl;

    public UnbindableDeclException(Libadalang.BasicDecl decl, String message, Throwable cause) {
        super(message, cause);
        this.decl = decl;
    }

    public UnbindableDeclException(Libadalang.BasicDecl decl, String message) {
        super(message);
        this.decl = decl;
    }

    public UnbindableDeclException(Libadalang.BasicDecl decl, Throwable cause) {
        super(cause);
        this.decl = decl;
    }

    public Libadalang.BasicDecl getDecl() {
        return decl;
    }

    @Override
    public String toString() {
        String message = getMessage();
        if (message == null) return "UnbindableDeclException: (%s)".formatted(decl);
        return "UnbindableDeclException: (%s) %s".formatted(decl, message);
    }
}
