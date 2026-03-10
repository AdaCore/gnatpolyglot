//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.codegen;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import java.util.List;

public class ShadowParamConverter {
    private class Worker implements TypeWorker<String> {

        private SubpParam param;

        private String valueName;
        private String cTypename;
        private String argName;

        public Worker(SubpParam param) {
            this.param = param;

            this.valueName = api.valueName(param.name);
            this.argName = api.argName(param.name);
            this.cTypename = api.cInterfaceParamTypename(param);
        }

        public String makeScalarOutParam() {
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(argName)
                    .append("'Address")
                    .toString();
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            if (param.isOutMode()) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(cTypename)
                    .append(" (")
                    .append(argName)
                    .append(")")
                    .toString();
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            if (param.isOutMode()) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(AdaGenerator.makeConditionalExpr(argName, "1", "0"))
                    .toString();
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            if (param.isOutMode()) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(cTypename)
                    .append(
                            AdaGenerator.makeCall(
                                    "Interfaces.C.To_C",
                                    List.of(AdaGenerator.makeCast(type.pStdCharType(), argName))))
                    .toString();
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            if (param.isOutMode()) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(type.pFullyQualifiedName())
                    .append("'Enum_Rep (")
                    .append(argName)
                    .append(")")
                    .toString();
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            // Strings are bound in the same way as arrays.
            return arrayType(type);
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            // Simply pass the address of the record.
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(argName)
                    .append("'Address")
                    .toString();
        }

        @Override
        public String taggedType(Libadalang.BaseTypeDecl type) {
            return recordType(type);
        }

        @Override
        public String privateType(Libadalang.BaseTypeDecl type) {
            return recordType(type);
        }

        @Override
        public String arrayType(Libadalang.BaseTypeDecl type) {
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(AdaGenerator.makePolyglotArray(argName, false))
                    .toString();
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(api.cInterfaceTypename(type))
                    .append(" := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(
                                            type.pParentBasicDecl()
                                                    .pFullyQualifiedName()
                                                    .concat(".\"=\""),
                                            List.of("null", argName)),
                                    "(1, 0, System.Null_Address)",
                                    AdaGenerator.makePolyglotArray(argName, true)))
                    .toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            String converter = api.makeTemp(param.name, "Converter");
            return AdaGenerator.uncheckedConverter(converter, type.pFullyQualifiedName(), cTypename)
                    .append(";\n")
                    .append(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(
                                            type.pParentBasicDecl()
                                                    .pFullyQualifiedName()
                                                    .concat(".\"=\""),
                                            List.of("null", argName)),
                                    "System.Null_Address",
                                    AdaGenerator.makeCall(converter, List.of(argName))))
                    .toString();
        }
    }

    private final AdaAPI api;

    public ShadowParamConverter(AdaAPI api) {
        this.api = api;
    }

    public String build(SubpParam param) {
        return new Worker(param).apply(param.getType());
    }
}
