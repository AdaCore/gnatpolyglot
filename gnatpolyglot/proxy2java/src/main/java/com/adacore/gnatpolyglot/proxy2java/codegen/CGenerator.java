package com.adacore.gnatpolyglot.proxy2java.codegen;

import java.util.List;
import java.util.stream.Collectors;

public class CGenerator {

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeCall(String name, List<? extends CharSequence> args) {
        return new StringBuilder(name)
                .append("(")
                .append(args.stream().collect(Collectors.joining(", ")))
                .append(")");
    }

    public static StringBuilder makeCast(CharSequence type, CharSequence expr) {
        return new StringBuilder("((").append(type).append(") ").append(expr).append(")");
    }
}
