package com.adacore.gnatpolyglot.proxy2java.codegen;

import java.util.List;

public class JavaGenerator {

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeCall(String name, List<? extends CharSequence> args) {
        return CGenerator.makeCall(name, args);
    }

    /** Generate a call to a function with the given name. */
    public static StringBuilder makeMethodCall(
            CharSequence object, String method, List<? extends CharSequence> args) {
        return new StringBuilder(object).append(".").append(CGenerator.makeCall(method, args));
    }

    public static StringBuilder makeNew(String typename, List<? extends CharSequence> args) {
        return new StringBuilder("new ").append(CGenerator.makeCall(typename, args));
    }

    public static StringBuilder makeGetData(CharSequence expr) {
        return new StringBuilder(expr).append(".getData()");
    }

    public static StringBuilder makeGetAddress(CharSequence expr) {
        return makeGetData(expr).append(".getAddress()");
    }

    public static StringBuilder makeTernary(
            CharSequence cond, CharSequence trueExpr, CharSequence falseExpr) {
        return new StringBuilder(cond)
                .append(" ? ")
                .append(trueExpr)
                .append(" : ")
                .append(falseExpr);
    }

    public static StringBuilder makeOptGetData(CharSequence expr) {
        return makeTernary(expr + " == null", "null", makeGetData(expr));
    }

    public static StringBuilder makeOptGetAddress(CharSequence expr) {
        return makeTernary(expr + " == null", "0L", makeGetAddress(expr));
    }

    public static StringBuilder makeObjectFromAddress(String typename, String addr, String owner) {
        return JavaGenerator.makeNew(
                typename,
                List.of(
                        JavaGenerator.makeNew(
                                "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                                List.of(addr, owner))));
    }
}
