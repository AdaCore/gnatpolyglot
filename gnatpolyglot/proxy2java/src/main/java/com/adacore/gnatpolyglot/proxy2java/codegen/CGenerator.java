package com.adacore.gnatpolyglot.proxy2java.codegen;

import java.util.List;
import java.util.stream.Collectors;

public class CGenerator {

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeCall(CharSequence name, List<? extends CharSequence> args) {
        return new StringBuilder(name)
                .append("(")
                .append(args.stream().collect(Collectors.joining(", ")))
                .append(")");
    }

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeJNICall(
            CharSequence envMember, CharSequence methodID, List<? extends CharSequence> args) {
        return new StringBuilder("(*env)->")
                .append(envMember)
                .append("(env, ")
                .append(args.get(0))
                .append(", ")
                .append(methodID)
                .append(args.size() > 1 ? ", " : "")
                .append(args.stream().skip(1).collect(Collectors.joining(", ")))
                .append(")");
    }

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeJNICall(
            CharSequence envMember, List<? extends CharSequence> args) {
        return new StringBuilder("(*env)->")
                .append(envMember)
                .append("(env, ")
                .append(args.stream().collect(Collectors.joining(", ")))
                .append(")");
    }

    public static StringBuilder makeCast(CharSequence type, CharSequence expr) {
        return new StringBuilder("((").append(type).append(") ").append(expr).append(")");
    }

    public static StringBuilder deref(CharSequence returnedValue) {
        return new StringBuilder("(*").append(returnedValue).append(")");
    }
}
