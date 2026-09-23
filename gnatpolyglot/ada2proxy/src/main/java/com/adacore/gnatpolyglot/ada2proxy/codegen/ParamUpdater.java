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
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.util.List;

public class ParamUpdater {

    private class Worker implements TypeWorker<String> {

        private boolean isOutMode;
        private Name name;

        public Worker(SubpParam param) {
            this.isOutMode = param.isOutMode();
            this.name = param.name;
        }

        public Worker(Libadalang.DefiningName paramName, Libadalang.BaseFormalParamDecl paramDecl) {
            this.isOutMode = SubpParam.isOutMode(paramDecl);
            this.name = AdaAPI.getName(paramName);
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
            if (!isOutMode) return "";
            String polyglotArray = api.makeTemp(name, "Polyglot_Array");
            String valueArg = api.valueName(name);
            String eqFunction = type.pParentBasicDecl().pFullyQualifiedName().concat(".\"=\"");
            return new StringBuilder("declare\n")
                    .append(polyglotArray)
                    .append(" : ")
                    .append(api.cInterfaceTypename(type.pAccessedType(type)))
                    .append(" with Address => ")
                    .append(api.argName(name))
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

        @Override
        public String subpAccessType(BaseTypeDecl type) {
            if (!isOutMode) return "";
            String subpName =
                    name.concat(Name.fromPascalWithUnderscore("Subprogram"))
                            .toPascalWithUnderscore();
            String bufferName = api.makeTemp(name, "Buffer");
            String valueName = api.valueName(name);
            String converter = api.makeTemp(name, "Converter");
            StringBuilder builder =
                    new StringBuilder("declare\n")
                            .append(
                                    AdaGenerator.uncheckedConverter(
                                            converter,
                                            type.pFullyQualifiedName(),
                                            "System.Address"))
                            .append(";\n")
                            .append(bufferName)
                            .append(" : ")
                            .append(api.cInterfaceTypename(type))
                            .append(" with Address => ")
                            .append(api.argName(name))
                            .append(";")
                            .append(AdaGenerator.makeImport(bufferName))
                            .append(";\n")
                            .append("begin\n")
                            .append("if ")
                            .append(valueName)
                            .append(" /= ")
                            .append(subpName)
                            .append("'Unrestricted_Access then\n")
                            .append(bufferName)
                            .append(" := (")
                            .append(Callback.callbackCFQN(type))
                            .append("'Address")
                            .append(", ")
                            .append(AdaGenerator.makeCall(converter, List.of(valueName)))
                            .append(", System.Null_Address)")
                            .append(";\n")
                            .append("end if;")
                            .append("end;");

            return builder.toString();
        }
    }

    private class UpcallWorker implements TypeWorker<String> {

        boolean isOutMode;
        Name name;

        public UpcallWorker(SubpParam param) {
            this.isOutMode = param.isOutMode();
            this.name = param.name;
        }

        public UpcallWorker(
                Libadalang.DefiningName paramName, Libadalang.BaseFormalParamDecl paramDecl) {
            this.isOutMode = SubpParam.isOutMode(paramDecl);
            this.name = AdaAPI.getName(paramName);
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
            if (!isOutMode) return "";
            String fatPtr = api.makeTemp(name, "Fat_Pointer");
            String tmpAccess = api.makeTemp(name, "Tmp_Access");
            String valueName = api.valueName(name);
            String argName = api.argName(name);
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
            if (!isOutMode) return "";
            String converter = api.makeTemp(name, "Converter");
            String valueName = api.valueName(name);
            String argName = api.argName(name);
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

    public String build(Libadalang.DefiningName paramName) {
        Libadalang.BaseFormalParamDecl paramDecl =
                (Libadalang.BaseFormalParamDecl) paramName.pBasicDecl();
        return new Worker(paramName, paramDecl).apply(paramDecl.pFormalType(paramDecl));
    }

    public String buildUpcall(SubpParam param) {
        return new UpcallWorker(param).apply(param.getType());
    }

    public String buildUpcall(Libadalang.DefiningName paramName) {
        Libadalang.BaseFormalParamDecl paramDecl =
                (Libadalang.BaseFormalParamDecl) paramName.pBasicDecl();
        return new UpcallWorker(paramName, paramDecl).apply(paramDecl.pFormalType(paramDecl));
    }
}
