package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;

public interface JavaTypeWorker<T> extends TypeWorker<T> {

    @Override
    default T charType(TypeExpr type) {
        return numberType(type);
    }

    @Override
    default T boolType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'boolType'");
    }

    @Override
    default T enumType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'enumType'");
    }

    @Override
    default T stringType(TypeExpr type) {
        return arrayType(type);
    }

    @Override
    default T pointerType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'pointerType'");
    }

    public interface JavaSubreferenceTypeWorker<T> extends TypeWorker.SubreferenceTypeWorker<T> {

        @Override
        default T charType(TypeExpr type) {
            return numberType(type);
        }

        @Override
        default T boolType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'boolType'");
        }

        @Override
        default T enumType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'enumType'");
        }

        @Override
        default T stringType(TypeExpr type) {
            return arrayType(type);
        }

        @Override
        default T pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'pointerType'");
        }
    }
}
