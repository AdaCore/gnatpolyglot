//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.codegen;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.ada2proxy.proxy.SubpParam;
import com.adacore.libadalang.Libadalang;
import java.util.List;

public class ParamUpdater {

    private class Worker implements TypeWorker<String> {

        private SubpParam param;

        public Worker(SubpParam param) {
            this.param = param;
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String taggedType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String privateType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String arrayType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            if (!param.isOutMode()) return "";
            String polyglotArray = api.makeTemp(param.name, "Polyglot_Array");
            String valueArg = api.valueName(param.name);
            String eqFunction =
                    param.getType().pParentBasicDecl().pFullyQualifiedName().concat(".\"=\"");
            return new StringBuilder("declare\n")
                    .append(polyglotArray)
                    .append(" : ")
                    .append(api.cInterfaceTypename(param.getType().pAccessedType(param.getType())))
                    .append(" with Address => ")
                    .append(api.argName(param.name))
                    .append(";\n")
                    .append(AdaGenerator.makeImport(polyglotArray))
                    .append(";\n")
                    .append("begin\n")
                    .append(polyglotArray)
                    .append(".First := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(eqFunction, List.of(valueArg, "null")),
                                    "1",
                                    AdaGenerator.makeCall(
                                            "Interfaces.C.int",
                                            List.of(valueArg.concat(".all'First")))))
                    .append(";\n")
                    .append(polyglotArray)
                    .append(".Last := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(eqFunction, List.of(valueArg, "null")),
                                    "0",
                                    AdaGenerator.makeCall(
                                            "Interfaces.C.int",
                                            List.of(valueArg.concat(".all'Last")))))
                    .append(";\n")
                    .append(polyglotArray)
                    .append(".Data := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(eqFunction, List.of(valueArg, "null")),
                                    "System.Null_Address",
                                    valueArg.concat(".all'Address")))
                    .append(";\n")
                    .append("end;")
                    .toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            return "";
        }
    }

    private class DispatchWorker implements TypeWorker<String> {

        private SubpParam param;

        public DispatchWorker(SubpParam param) {
            this.param = param;
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String taggedType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String privateType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String arrayType(Libadalang.BaseTypeDecl type) {
            return "";
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            if (!param.isOutMode()) return "";
            String fatPtr = api.makeTemp(param.name, "Fat_Pointer");
            String tmpAccess = api.makeTemp(param.name, "Tmp_Access");
            String valueName = api.valueName(param.name);
            String argName = api.argName(param.name);
            return new StringBuilder("declare\n")
                    .append(tmpAccess)
                    .append(" : ")
                    .append(type.pFullyQualifiedName())
                    .append(";\n")
                    .append(fatPtr)
                    .append(" : GNATpolyglot.Ada.Arrays.Fat_Pointer := (")
                    .append(valueName)
                    .append(".Data, ")
                    .append(
                            AdaGenerator.makeCall(
                                    "System.Storage_Elements.\"-\"",
                                    List.of(
                                            valueName.concat(".Data"),
                                            type.pAccessedType(type)
                                                    .pFullyQualifiedName()
                                                    .concat("'Descriptor_Size / 8"))))
                    .append(");\n")
                    .append("for ")
                    .append(fatPtr)
                    .append("'Address use ")
                    .append(tmpAccess)
                    .append("'Address;\n")
                    .append("begin\n")
                    .append(argName)
                    .append(" := ")
                    .append(tmpAccess)
                    .append(";\n")
                    .append("end;")
                    .toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            if (!param.isOutMode()) return "";
            String converter = api.makeTemp(param.name, "Converter");
            String valueName = api.valueName(param.name);
            String argName = api.argName(param.name);
            return new StringBuilder("declare\n")
                    .append(
                            AdaGenerator.uncheckedConverter(
                                    converter, "System.Address", type.pFullyQualifiedName()))
                    .append(";\n")
                    .append("begin\n")
                    .append(argName)
                    .append(" := ")
                    .append(AdaGenerator.makeCall(converter, List.of(valueName)))
                    .append(";\n")
                    .append("end;")
                    .toString();
        }
    }

    private final AdaAPI api;

    public ParamUpdater(AdaAPI api) {
        this.api = api;
    }

    public String build(SubpParam param) {
        return new Worker(param).apply(param.getType());
    }

    public String buildDispatch(SubpParam param) {
        return new DispatchWorker(param).apply(param.getType());
    }
}
