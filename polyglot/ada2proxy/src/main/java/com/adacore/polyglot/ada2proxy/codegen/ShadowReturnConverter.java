//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.codegen;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import java.util.List;

public class ShadowReturnConverter {

    private static final String RETURNED_VALUE = "Returned_Value";
    private static final String RETURNED_ACCESS = "Returned_Access";
    private static final String CONVERTER = "Converter";
    private static final String FREE_SUBP = "Free";

    private class PrepareWorker implements TypeWorker<String> {

        private String typename;

        public PrepareWorker(Libadalang.BaseTypeDecl returnedType) {
            this.typename = returnedType.pFullyQualifiedName();
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
            return arrayType(type);
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            String accessType = api.asAccess(type);
            return new StringBuilder("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(typename)
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n")
                    .append(
                            AdaGenerator.uncheckedConverter(
                                    CONVERTER, "System.Address", accessType))
                    .append(";\n")
                    .append(AdaGenerator.uncheckedDeallocation(FREE_SUBP, typename, accessType))
                    .append(";\n")
                    .append(RETURNED_ACCESS)
                    .append(" : ")
                    .append(accessType)
                    .append(" := ")
                    .append(AdaGenerator.makeCall(CONVERTER, List.of(RETURNED_VALUE)))
                    .append(";")
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
            String accessType = api.asAccess(type);
            return new StringBuilder("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(typename)
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n")
                    .append(
                            AdaGenerator.uncheckedConverter(
                                    CONVERTER, "System.Address", accessType))
                    .append(";\n")
                    .append(AdaGenerator.uncheckedDeallocation(FREE_SUBP, typename, accessType))
                    .append(";\n")
                    .append(RETURNED_ACCESS)
                    .append(" : ")
                    .append(accessType)
                    .append(" := ")
                    .append(
                            AdaGenerator.makeCall(
                                    CONVERTER, List.of(RETURNED_VALUE.concat(".Data"))))
                    .append(";")
                    .toString();
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            String accessType = api.asAccess(type);
            return new StringBuilder("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(type.pAccessedType(type).pFullyQualifiedName())
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n")
                    .append(
                            AdaGenerator.uncheckedConverter(
                                    CONVERTER, "System.Address", accessType))
                    .append(";\n")
                    .append(RETURNED_ACCESS)
                    .append(" : ")
                    .append(accessType)
                    .append(" := ")
                    .append(
                            AdaGenerator.makeCall(
                                    CONVERTER, List.of(RETURNED_VALUE.concat(".Data"))))
                    .append(";")
                    .toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            return AdaGenerator.uncheckedConverter(CONVERTER, "System.Address", typename)
                    .append(";\n")
                    .toString();
        }
    }

    private class BuildWorker implements TypeWorker<String> {

        private String typename;

        public BuildWorker(Subprogram subp) {
            this.typename = subp.getReturnType().pFullyQualifiedName();
        }

        private StringBuilder common() {
            return new StringBuilder("return Result : ").append(typename).append(" := ");
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            return common().append(AdaGenerator.makeCast(type, RETURNED_VALUE)).toString();
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            return common().append(RETURNED_VALUE).append(" /= 0").toString();
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            return common().append(
                            AdaGenerator.makeCast(
                                    type,
                                    AdaGenerator.makeCall(
                                            "Interfaces.C.To_Ada", List.of(RETURNED_VALUE))))
                    .toString();
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            return common().append(
                            AdaGenerator.makeCall(
                                    type.pFullyQualifiedName().concat("'Enum_Val"),
                                    List.of(RETURNED_VALUE)))
                    .toString();
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            return arrayType(type);
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            // Values are returned on the heap from the target language. However, the parent of the
            // shadow function does not expect an acess or an address, but a value type instead. We
            // need to copy the returned value to the stack and free its heap counterpart.
            return common().append(RETURNED_ACCESS)
                    .append(".all")
                    .append(" do\n")
                    .append(AdaGenerator.makeCall(FREE_SUBP, List.of(RETURNED_ACCESS)))
                    .append(";\n")
                    .append("end return")
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
            // Similarly to records, we need to free the array on the heap after copying it in the
            // returned value.
            return recordType(type);
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            return common().append(RETURNED_ACCESS).append(".all'Unchecked_Access").toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            return common().append(AdaGenerator.makeCall(CONVERTER, List.of(RETURNED_VALUE)))
                    .toString();
        }
    }

    private final AdaAPI api;

    public ShadowReturnConverter(AdaAPI api) {
        this.api = api;
    }

    /**
     * Return a string that creates the necessary entities for converting a value of the given type
     * returned by the overriding function.
     */
    public String prepareReturn(Libadalang.BaseTypeDecl returnedType) {
        return new PrepareWorker(returnedType).apply(returnedType);
    }

    /**
     * Return a string that creates the return statement when calling an override of `subp`. The
     * method `prepareReturn` must be called prior to this one.
     */
    public String buildReturn(Subprogram subp) {
        return new BuildWorker(subp).apply(subp.getReturnType());
    }
}
