//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.codegen;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.util.List;

public class GetterReturnConverter {

    private class GetterWorker implements TypeWorker<String> {

        private String component;

        public GetterWorker(Libadalang.BaseTypeDecl returnedType, String component) {
            this.component = component;
        }

        private String makeGetAddress() {
            return new StringBuilder("return ").append(component).append("'Address").toString();
        }

        @Override
        public String intType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String boolType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String characterType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String enumType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String stringType(BaseTypeDecl type) {
            return arrayType(type);
        }

        @Override
        public String recordType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String taggedType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String privateType(BaseTypeDecl type) {
            return makeGetAddress();
        }

        @Override
        public String arrayType(BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(AdaGenerator.makePolyglotArray(component, false))
                    .toString();
        }

        @Override
        public String arrayAccessType(BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(
                                            type.pParentBasicDecl()
                                                    .pFullyQualifiedName()
                                                    .concat(".\"=\""),
                                            List.of(component, "null")),
                                    "(1, 0, System.Null_Address)",
                                    AdaGenerator.makePolyglotArray(component, true)))
                    .toString();
        }

        @Override
        public String accessType(BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(AdaGenerator.makeCall("Return_Type_Converter", List.of(component)))
                    .toString();
        }
    }

    private final AdaAPI api;

    public GetterReturnConverter(AdaAPI api) {
        this.api = api;
    }

    public String build(Libadalang.BaseTypeDecl returnedType, String component) {
        return new GetterWorker(returnedType, component).apply(returnedType);
    }
}
