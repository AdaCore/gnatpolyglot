package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.TypeWorker;

public interface JavaTypeWorker<T> extends TypeWorker<T> {

    @Override
    default T charType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'charType'");
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
        throw new UnsupportedOperationException("Unimplemented method 'stringType'");
    }

    @Override
    default T pointerType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'pointerType'");
    }

    @Override
    default T refType(TypeExpr type) {
        throw new UnsupportedOperationException("Unimplemented method 'refType'");
    }

    public interface JavaSubreferenceTypeWorker<T> extends TypeWorker.SubreferenceTypeWorker<T> {

        @Override
        default T numberType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'numberType'");
        }

        @Override
        default T charType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'charType'");
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
            throw new UnsupportedOperationException("Unimplemented method 'stringType'");
        }

        @Override
        default T pointerType(TypeExpr type) {
            throw new UnsupportedOperationException("Unimplemented method 'pointerType'");
        }
    }
}
