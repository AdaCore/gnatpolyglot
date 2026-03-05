//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.codegen;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.Name;
import java.util.List;

public class ParamConverter {

    private class Worker implements TypeWorker<String> {
        private Name name;
        private boolean isOutMode;
        private boolean isAliased;

        private String argName;
        private String valueName;
        private String typename;

        public Worker(Name name, BaseTypeDecl type, boolean isOutMode, boolean isAliased) {
            this.name = name;
            this.isOutMode = isOutMode;
            this.isAliased = isAliased;

            // Common values
            this.argName = api.argName(name);
            this.valueName = api.valueName(name);
            this.typename = type.pFullyQualifiedName();
        }

        public String makeScalarOutParam() {
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(isAliased ? "aliased " : "")
                    .append(typename)
                    .append(" with Address => ")
                    .append(argName)
                    .append(";\n")
                    .append(AdaGenerator.makeImport(valueName))
                    .toString();
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            // A cast is enough:
            //
            // ${valueName} : ${typename} := ${typename} (${argName})
            if (isOutMode) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(isAliased ? "aliased " : "")
                    .append(typename)
                    .append(" := ")
                    .append(AdaGenerator.makeCast(type, argName))
                    .toString();
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            // Booleans are use C integers. Compare the value to 0
            //
            // ${valueName} : ${typename} := 0 /= ${argName}
            if (isOutMode) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" := 0 /= ")
                    .append(argName)
                    .toString();
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            // First, we need to convert the C char to an Ada character, and then cast it to the
            // expected type
            //
            // ${valueName} : ${typename} := ${typename} (Interfaces.C.To_Ada (${argName}))
            if (isOutMode) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" := ")
                    .append(
                            AdaGenerator.makeCast(
                                    type,
                                    AdaGenerator.makeCall("Interfaces.C.To_Ada", List.of(argName))))
                    .toString();
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            // Get the enum value from its integer repr
            //
            // ${valueName} : ${typename} := ${typename}'Enum_Val (${argName})
            if (isOutMode) return makeScalarOutParam();
            return new StringBuilder(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" := ")
                    .append(typename)
                    .append("'Enum_Val (")
                    .append(argName)
                    .append(")")
                    .toString();
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            // In ada, a string is an array of Character. The only difference is the intermediate C
            // type used by Polyglot, which shares a common interface.
            return arrayType(type);
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            // argName is an address to the record. We must create an access type for this record,
            // and then convert the address to the access type.
            //
            // ${accessType} is access all ${typename};
            // function ${converter} is Ada.Unchecked_Conversion (System.Address, ${accessType});
            // ${tempAccess} : ${accessType} := ${converter} (${argName});
            // ${valueName} : ${typename} renames ${tempAccess}.all
            String accessType = api.makeTemp(name, "Access_Type");
            String converter = api.makeTemp(name, "Converter");
            String tempAccess = api.makeTemp(name, "Access");
            return new StringBuilder("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(typename)
                    .append(";\n")
                    .append(
                            AdaGenerator.uncheckedConverter(
                                    converter, "System.Address", accessType))
                    .append(";\n")
                    .append(tempAccess)
                    .append(" : ")
                    .append(accessType)
                    .append(" := ")
                    .append(AdaGenerator.makeCall(converter, List.of(argName)))
                    .append(";\n")
                    .append(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" renames ")
                    .append(tempAccess)
                    .append(".all")
                    .toString();
        }

        @Override
        public String taggedType(Libadalang.BaseTypeDecl type) {
            // Tagged types conversion is the same as for records.
            return recordType(type);
        }

        @Override
        public String privateType(Libadalang.BaseTypeDecl type) {
            // Private types conversion is the same as for records.
            return recordType(type);
        }

        @Override
        public String arrayType(Libadalang.BaseTypeDecl type) {
            // Similarly to records, we create an access to the array from the address received in
            // the struct passed by argument.
            StringBuilder builder = new StringBuilder();
            String arrayType = typename;
            if (!type.pIsStaticallyConstrained()) {
                // If the array is not constrained, we need to create a constrained array type
                // derivation so that the bounds informations can be recreated.
                //
                // type ${arrayType} is new ${typename} (${bounds});
                arrayType = api.makeTemp(name, "Constrained_Array");
                builder.append("type ")
                        .append(arrayType)
                        .append(" is new ")
                        .append(typename)
                        .append(" (")
                        .append(AdaGenerator.createBoundCast(argName, type, true))
                        .append(" .. ")
                        .append(AdaGenerator.createBoundCast(argName, type, false))
                        .append(");\n");
            }
            String accessTypename = api.makeTemp(name, "Constrained_Array_Access");
            String tempAccess = api.makeTemp(name, "Value_Access");
            // Create the thin access type to the constrained array type
            builder.append("type ")
                    .append(accessTypename)
                    .append(" is access all ")
                    .append(arrayType)
                    .append(" with Size => Standard'Address_Size;\n");
            // Create an overlay of the pointer by using the address in the struct
            builder.append(tempAccess)
                    .append(" : ")
                    .append(accessTypename)
                    .append(" with Address => ")
                    .append(argName)
                    .append(".Data'Address;\n")
                    .append(AdaGenerator.makeImport(tempAccess))
                    .append(";\n");
            builder.append(valueName)
                    .append(" : ")
                    .append(arrayType)
                    .append(" renames ")
                    .append(tempAccess)
                    .append(".all");
            // The value created is still not of the required type. It is finally converter at the
            // call site to the binded function.
            // The array is converter using an Unrestricted_Access to the array after it was cast to
            // the correct type.
            return builder.toString();
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            StringBuilder builder = new StringBuilder();
            Libadalang.BaseTypeDecl arrayType = type.pAccessedType(type);
            String arrayTypename = arrayType.pFullyQualifiedName();
            String constrainedArray = arrayTypename;
            String arrayDataType = api.cInterfaceTypename(arrayType);
            if (isOutMode) {
                // The Polyglot array is passed by reference (System.Address). we need to create an
                // overlay from the given address. its values will be update after calling the bound
                // subprogram.
                String polyglotArrayValue = api.makeTemp(name, "Polyglot_Array");
                builder.append(polyglotArrayValue)
                        .append(" : ")
                        .append(arrayDataType)
                        .append(" with Address => ")
                        .append(argName)
                        .append(";\n")
                        .append(AdaGenerator.makeImport(polyglotArrayValue))
                        .append(";\n");
                argName = polyglotArrayValue;
            }
            if (!arrayType.pIsStaticallyConstrained()) {
                // If the array is not constrained, we need to create a constrained array type
                // derivation so that the bounds informations can be recreated.
                constrainedArray = api.makeTemp(name, "Constrained_Array");
                builder.append("type ")
                        .append(constrainedArray)
                        .append(" is new ")
                        .append(arrayTypename)
                        .append(" (")
                        .append(AdaGenerator.createBoundCast(argName, arrayType, true))
                        .append(" .. ")
                        .append(AdaGenerator.createBoundCast(argName, arrayType, false))
                        .append(");\n");
            }
            String tempAccess = api.makeTemp(name, "Value_Access");
            String accessTypename = api.makeTemp(name, "Constrained_Array_Access");
            // Create an access type to the constrained array.
            builder.append("type ")
                    .append(accessTypename)
                    .append(" is access all ")
                    .append(constrainedArray)
                    .append(" with Size => Standard'Address_Size;\n");
            // Create an overlay on top of the address inside the argument.
            builder.append(tempAccess)
                    .append(" : ")
                    .append(accessTypename)
                    .append(" with Address => ")
                    .append(argName)
                    .append(".Data'Address;\n")
                    .append(AdaGenerator.makeImport(tempAccess))
                    .append(";\n");

            builder.append(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" := ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    tempAccess.concat(" = null"),
                                    "null",
                                    AdaGenerator.makeCast(arrayType, tempAccess.concat(".all"))
                                            .append("'Unrestricted_Access")));
            return builder.toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            if (isOutMode) return makeScalarOutParam();
            String converter = api.makeTemp(name, "Converter");
            return new StringBuilder(
                            AdaGenerator.uncheckedConverter(converter, "System.Address", typename))
                    .append(";\n")
                    .append(valueName)
                    .append(" : ")
                    .append(typename)
                    .append(" := ")
                    .append(AdaGenerator.makeCall(converter, List.of(argName)))
                    .toString();
        }
    }

    private final AdaAPI api;

    public ParamConverter(AdaAPI api) {
        this.api = api;
    }

    public String build(
            Name name, Libadalang.BaseTypeDecl type, boolean isOutMode, boolean isAliased) {
        return new Worker(name, type, isOutMode, isAliased).apply(type);
    }
}
