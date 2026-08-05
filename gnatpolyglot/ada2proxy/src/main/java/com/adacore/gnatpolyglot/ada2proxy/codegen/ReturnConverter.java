//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.codegen;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Callback;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.util.List;

public class ReturnConverter {

    private static final String RETURN_TYPE_CONVERTER = "Return_Type_Converter";

    private class PrepareWorker implements TypeWorker<String> {

        @Override
        public String intType(BaseTypeDecl type) {
            return "";
        }

        @Override
        public String boolType(BaseTypeDecl type) {
            return "";
        }

        @Override
        public String characterType(BaseTypeDecl type) {
            return "";
        }

        @Override
        public String enumType(BaseTypeDecl type) {
            return "";
        }

        @Override
        public String stringType(BaseTypeDecl type) {
            return arrayType(type);
        }

        @Override
        public String recordType(BaseTypeDecl type) {
            // Records are copied on the heap and the resulting access is converted to an Address.
            return AdaGenerator.uncheckedConverter(
                            RETURN_TYPE_CONVERTER,
                            type.pIsClasswide()
                                    ? api.getProxyClasswideAccessFullyQualifiedName(type)
                                    : api.getProxyAccessFullyQualifiedName(type),
                            "System.Address")
                    .append(";")
                    .toString();
        }

        @Override
        public String taggedType(BaseTypeDecl type) {
            return recordType(type);
        }

        @Override
        public String privateType(BaseTypeDecl type) {
            return recordType(type);
        }

        @Override
        public String arrayType(BaseTypeDecl type) {
            // Arrays are returned as a Polyglot_Array. They must first be copied on the heap, then
            // their address and bounds must be returned in the Polyglot_Array.
            // This variable will hold the heap copy.
            return new StringBuilder("Returned_Array : ")
                    .append(api.getProxyAccessFullyQualifiedName(type))
                    .append(";")
                    .toString();
        }

        @Override
        public String arrayAccessType(BaseTypeDecl type) {
            return "";
        }

        @Override
        public String accessType(BaseTypeDecl type) {
            // Access types are simply converter to addresses.
            return AdaGenerator.uncheckedConverter(
                            RETURN_TYPE_CONVERTER, type.pFullyQualifiedName(), "System.Address")
                    .append(";")
                    .toString();
        }

        @Override
        public String subpAccessType(BaseTypeDecl type) {
            // Create a converter function
            return accessType(type);
        }
    }

    private class BuildWorker implements TypeWorker<String> {

        private String returnedValue;
        private String typename;

        public BuildWorker(Libadalang.BaseTypeDecl returnedType, String returnedValue) {
            this.returnedValue = returnedValue;
            this.typename = returnedType.pFullyQualifiedName();
        }

        @Override
        public String intType(Libadalang.BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(
                            AdaGenerator.makeCall(
                                    api.cInterfaceTypename(type), List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String boolType(Libadalang.BaseTypeDecl type) {
            // Booleans are passed as integers in the proxy
            return new StringBuilder("return ")
                    .append(AdaGenerator.makeConditionalExpr(returnedValue, "1", "0"))
                    .toString();
        }

        @Override
        public String characterType(Libadalang.BaseTypeDecl type) {
            // We must first convert the charater to a Standard.Character and then call
            // `Interfaces.C.To_C`.
            return new StringBuilder("return ")
                    .append(
                            AdaGenerator.makeCall(
                                    "Interfaces.C.To_C",
                                    List.of(
                                            AdaGenerator.makeCast(
                                                    type.pStdCharType(), returnedValue))))
                    .toString();
        }

        @Override
        public String enumType(Libadalang.BaseTypeDecl type) {
            // Convert the enum value to its integer value
            return new StringBuilder("return ")
                    .append(typename)
                    .append("'Enum_Rep (")
                    .append(returnedValue)
                    .append(")")
                    .toString();
        }

        @Override
        public String stringType(Libadalang.BaseTypeDecl type) {
            // In ada, a string is an array of Character. The only difference is the intermediate C
            // type used by GNATpolyglot, which shares a common interface.
            return arrayType(type);
        }

        @Override
        public String recordType(Libadalang.BaseTypeDecl type) {
            // Convert the heap copy to an address.
            // If the value returned is a classwide type, and if it is a shadow object, change the
            // object ownership to USER.
            CharSequence heapCopy = AdaGenerator.makeHeapCopy(type, returnedValue);
            String heapCopyVar =
                    api.makeTemp(Name.fromPascalWithUnderscore(returnedValue), "Heap_Copy");
            StringBuilder builder = new StringBuilder("declare\n");
            boolean isClasswide = type.pIsClasswide();
            if (isClasswide) {
                builder.append(heapCopyVar)
                        .append(" : ")
                        .append(api.getProxyClasswideAccessFullyQualifiedName(type))
                        .append(" := ")
                        .append(heapCopy)
                        .append(";\n");
                // Use the variable as the heap copy to return instead of one created in the call to
                // the converter.
                heapCopy = heapCopyVar;
            }
            builder.append("begin\n");
            if (isClasswide) {
                builder.append(
                        api.changeShadowOwnership(heapCopyVar + ".all", "GNATpolyglot.Ada.USER"));
            }
            return builder.append("return ")
                    .append(AdaGenerator.makeCall(RETURN_TYPE_CONVERTER, List.of(heapCopy)))
                    .append(";\n")
                    .append("end")
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
            return new StringBuilder("Returned_Array := ")
                    .append(AdaGenerator.makeHeapCopy(type, returnedValue))
                    .append(";\n")
                    .append("return ")
                    .append(AdaGenerator.makePolyglotArray("Returned_Array", true))
                    .toString();
        }

        @Override
        public String arrayAccessType(Libadalang.BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(
                            AdaGenerator.makeConditionalExpr(
                                    AdaGenerator.makeCall(
                                            type.pParentBasicDecl()
                                                    .pFullyQualifiedName()
                                                    .concat(".\"=\""),
                                            List.of("null", returnedValue)),
                                    "(1, 0, System.Null_Address)",
                                    AdaGenerator.makePolyglotArray(returnedValue, true)))
                    .toString();
        }

        @Override
        public String accessType(Libadalang.BaseTypeDecl type) {
            return new StringBuilder("return ")
                    .append(AdaGenerator.makeCall(RETURN_TYPE_CONVERTER, List.of(returnedValue)))
                    .toString();
        }

        @Override
        public String subpAccessType(BaseTypeDecl type) {
            // Return the C ABI wrapper alongside the Ada subprogram.
            return new StringBuilder("return (")
                    .append(Callback.callbackCName(type))
                    .append("'Address")
                    .append(", ")
                    .append(AdaGenerator.makeCall(RETURN_TYPE_CONVERTER, List.of(returnedValue)))
                    .append(", System.Null_Address)")
                    .toString();
        }
    }

    private final AdaAPI api;

    public ReturnConverter(AdaAPI api) {
        this.api = api;
    }

    /**
     * Return a string that creates the necessary entities for converting a value of the given type
     * returned by a function.
     */
    public String prepareReturn(Libadalang.BaseTypeDecl returnedType) {
        return new PrepareWorker().apply(returnedType);
    }

    /**
     * Return a string that creates the return statement, converting the value held by
     * `returnedValue`. The method `prepareReturn` must be called prior to this one.
     */
    public String buildReturn(Libadalang.BaseTypeDecl returnedType, String returnedValue) {
        return new BuildWorker(returnedType, returnedValue).apply(returnedType);
    }
}
