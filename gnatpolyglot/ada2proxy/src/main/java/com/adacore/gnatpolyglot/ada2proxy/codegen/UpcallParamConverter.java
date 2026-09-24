//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.codegen;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Callback;
import com.adacore.gnatpolyglot.ada2proxy.proxy.SubpParam;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.libadalang.Libadalang;
import java.util.List;

public class UpcallParamConverter {
    private class Worker implements TypeWorker<String> {

        private String valueName;
        private String cTypename;
        private String argName;

        private boolean isOutMode;
        private Name name;

        public Worker(SubpParam param) {
            this.valueName = api.valueName(param.name);
            this.argName = api.argName(param.name);
            this.cTypename = api.cInterfaceParamTypename(param);

            this.isOutMode = param.isOutMode();
            this.name = param.name;
        }

        public Worker(Libadalang.BaseFormalParamDecl paramSpec, Libadalang.DefiningName name) {
            this.name = AdaAPI.getName(name);
            this.valueName = api.valueName(this.name);
            this.argName = api.argName(this.name);
            this.cTypename = api.cInterfaceParamTypename(paramSpec);

            this.isOutMode = SubpParam.isOutMode(paramSpec);
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
            if (isOutMode) return makeScalarOutParam();
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
            if (isOutMode) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append(AdaGenerator.makeConditionalExpr(argName, "1", "0"))
                    .toString();
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            if (isOutMode) return makeScalarOutParam();
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
            if (isOutMode) return makeScalarOutParam();
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
            String converter = api.makeTemp(name, "Converter");
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

        @Override
        public String subpAccessType(Libadalang.BaseTypeDecl type) {
            String converter = api.makeTemp(name, "Converter");
            return AdaGenerator.uncheckedConverter(
                            converter, type.pFullyQualifiedName(), "System.Address")
                    .append(";\n")
                    .append(valueName)
                    .append(" : ")
                    .append(cTypename)
                    .append(" := ")
                    .append("(")
                    .append(Callback.callbackCFQN(type))
                    .append("'Address")
                    .append(", ")
                    .append(AdaGenerator.makeCall(converter, List.of(argName)))
                    .append(", System.Null_Address)")
                    .toString();
        }
    }

    private final AdaAPI api;

    public UpcallParamConverter(AdaAPI api) {
        this.api = api;
    }

    public String build(SubpParam param) {
        return new Worker(param).apply(param.getType());
    }

    public String build(Libadalang.ParamSpec param, Libadalang.DefiningName name) {
        return new Worker(param, name).apply(param.pFormalType(param));
    }
}
