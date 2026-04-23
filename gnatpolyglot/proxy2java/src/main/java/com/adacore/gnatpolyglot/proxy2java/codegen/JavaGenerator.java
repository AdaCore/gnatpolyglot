package com.adacore.gnatpolyglot.proxy2java.codegen;

import java.util.List;

public class JavaGenerator {

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeCall(String name, List<? extends CharSequence> args) {
        return CGenerator.makeCall(name, args);
    }

    public static StringBuilder makeNew(String typename, List<? extends CharSequence> args) {
        return new StringBuilder("new ").append(CGenerator.makeCall(typename, args));
    }
}
