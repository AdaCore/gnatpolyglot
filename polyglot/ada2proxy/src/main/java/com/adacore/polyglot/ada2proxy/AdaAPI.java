package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import com.adacore.libadalang.Libadalang.Expr;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.TypeExpr;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI {

    /** Create a {@link FullyQualifiedName} to decl, or its parent if ``onlyParent`` is true. */
    public static FullyQualifiedName makeProxyFullyQualifiedName(Libadalang.BasicDecl decl) {
        if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            NativeType nativeType = checkNativeType(typeDecl);
            if (nativeType != null) return nativeType.typeExpr.name;
            // We must not show class wide types in the proxy.
            decl = typeDecl.pSpecificType();
        }
        Libadalang.Symbol[] symbols = decl.pFullyQualifiedNameArray(false);
        return new FullyQualifiedName(
                Stream.of(symbols).map(s -> s.text).map(Name::fromLower).toList());
    }

    /** Create a {@link TypeExpr} to ``decl``. */
    public static TypeExpr makeTypeExpr(Libadalang.BasicDecl decl) {
        if (decl instanceof Libadalang.TypeDecl typeDecl) {
            if (typeDecl.fTypeDef() instanceof Libadalang.ArrayTypeDef arrayTypeDef) {
                if (isStringType(typeDecl)) return NativeType.STRING.typeExpr;
                return makeTypeExpr(arrayTypeDef.fComponentType().fTypeExpr()).makeArray();
            }
        }
        return makeProxyFullyQualifiedName(decl).asTypeExpr();
    }

    public static boolean isStringType(Libadalang.BaseTypeDecl bTypeDecl) {
        if (bTypeDecl instanceof Libadalang.TypeDecl typeDecl
                && typeDecl.fTypeDef() instanceof Libadalang.ArrayTypeDef arrayTypeDef) {
            return arrayTypeDef
                    .fComponentType()
                    .fTypeExpr()
                    .pDesignatedTypeDecl()
                    .equals(typeDecl.pStdCharType());
        }
        return bTypeDecl.equals(bTypeDecl.pStdStringType());
    }

    /** Return the native type corresponding to bTypeDecl, or null if the type is not native. */
    public static NativeType checkNativeType(Libadalang.BaseTypeDecl bTypeDecl) {
        if (bTypeDecl.isNone()) return NativeType.VOID;

        bTypeDecl = bTypeDecl.pRootType(Libadalang.AdaNode.NONE);

        if (bTypeDecl instanceof Libadalang.TypeDecl typeDecl) {
            if (typeDecl.equals(typeDecl.pBoolType())) return NativeType.BOOL;
            if (isStringType(typeDecl)) return NativeType.STRING;
            if (typeDecl.equals(typeDecl.pStdCharType())) return NativeType.UINT8;
            if (typeDecl.pIsIntType(Libadalang.AdaNode.NONE)) {
                // Compute the number of required bits to hold the values of the type and
                // find the smallest type able to hold it.
                if (typeDecl.fTypeDef() instanceof Libadalang.SignedIntTypeDef signed) {
                    Expr expr = signed.fRange().fRange();
                    if (expr instanceof Libadalang.BinOp binop) {
                        int bitLength =
                                Math.max(
                                                binop.fLeft().pEvalAsInt().bitLength(),
                                                binop.fRight().pEvalAsInt().bitLength())
                                        + 1;
                        if (bitLength <= 8) return NativeType.SINT8;
                        if (bitLength <= 16) return NativeType.SINT16;
                        if (bitLength <= 32) return NativeType.SINT32;
                        if (bitLength <= 64) return NativeType.SINT64;
                        if (bitLength <= 128) return NativeType.SINT128;
                    } else {
                        throw new IllegalArgumentException(
                                "Illegal integer type definition: " + expr.getImage());
                    }
                }
            }
            if (typeDecl.fTypeDef() instanceof Libadalang.ModIntTypeDef unsigned) {
                int bitLength = unsigned.fExpr().pEvalAsInt().bitLength();
                if (bitLength <= 8) return NativeType.UINT8;
                if (bitLength <= 16) return NativeType.UINT16;
                if (bitLength <= 32) return NativeType.UINT32;
                if (bitLength <= 64) return NativeType.UINT64;
                if (bitLength <= 128) return NativeType.UINT128;
            }
        }

        return null;
    }

    /** Create a {@link TypeExpr} to the type referenced by typeExpr */
    public static TypeExpr makeTypeExpr(Libadalang.TypeExpr typeExpr) {
        if (typeExpr.isNone()) return NativeType.VOID.typeExpr;
        return makeTypeExpr(typeExpr.pDesignatedTypeDecl());
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file.
     */
    public static String getProxyInterfaces(List<Package> packages) {
        return packages.stream()
                .flatMap(
                        p -> {
                            return Stream.of(
                                    AdaAPI.toAdaFilename(p, "-proxy.ads").toString(),
                                    AdaAPI.toAdaFilename(p, "-proxy.adb").toString());
                        })
                .map(s -> "\"%s\"".formatted(s))
                .collect(Collectors.joining(", "));
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file and the original files binded from the library for aggregate libraries.
     */
    public static String getAggregateInterfaces(List<Package> packages) {
        return packages.stream()
                .flatMap(
                        p -> {
                            return Stream.of(
                                    AdaAPI.toAdaFilename(p, ".ads").toString(),
                                    AdaAPI.toAdaFilename(p, ".adb").toString(),
                                    AdaAPI.toAdaFilename(p, "-proxy.ads").toString(),
                                    AdaAPI.toAdaFilename(p, "-proxy.adb").toString());
                        })
                .map(s -> "\"%s\"".formatted(s))
                .collect(Collectors.joining(", "));
    }

    /** Return name with the correct Ada syntax with ``_Proxy`` as a suffix. */
    public static String proxyName(Name name) {
        return name.toPascalWithUnderscore() + "_Proxy";
    }

    /**
     * Return name with the correct Ada syntax with ``_Proxy`` as a suffix and the parameter type
     * names to avoid conflicts with duplicated subprograms.
     */
    public static String proxyName(Subprogram subp) {
        StringBuilder builder = new StringBuilder(proxyName(subp.name));
        for (var p : subp.parameters) {
            builder.append("_").append(p.getType().pRelativeNameText().toString());
        }

        return builder.toString();
    }

    /** Return the typename of the parameter */
    private static String cInterfaceParamTypename(SubpParam p) {
        // If the parameter has a Out mode, it is a reference and will be passed as an address.
        if (p.isOutMode() && !p.getType().pIsArrayType(Libadalang.AdaNode.NONE))
            return "System.Address";
        else return cInterfaceTypename(p.getType());
    }

    /** Build a string containing the parameter specifications of a subprogram. */
    public static String cInterfaceParameters(Subprogram subp) {
        return subp.parameters.stream()
                .map(
                        (p -> {
                            StringBuilder argBuilder = new StringBuilder();
                            argBuilder
                                    .append(p.name.toPascalWithUnderscore())
                                    .append("_Arg")
                                    .append(": ")
                                    .append(cInterfaceParamTypename(p));
                            return argBuilder.toString();
                        }))
                .collect(Collectors.joining("; "));
    }

    /** Build a string containing the parameter specifications of the constructor of a record. */
    public static String cInterfaceParameters(Record rec, FunctionDecl function) {
        return function.type.parameters.stream()
                .map(
                        p -> {
                            // Get the component corresponding to the constuctor's argument.
                            Component component = rec.getComponent(p.name);
                            StringBuilder argBuilder = new StringBuilder();
                            argBuilder
                                    .append(p.name.toPascalWithUnderscore())
                                    .append("_Arg : ")
                                    .append(cInterfaceTypename(component.getType()));
                            return argBuilder.toString();
                        })
                .collect(Collectors.joining("; "));
    }

    /** Create a string that refers to the type pointed by typeExpr as an access type. */
    public static String asAccess(Libadalang.BaseTypeDecl typeDecl) {
        if (typeDecl.pIsAccessType(Libadalang.AdaNode.NONE))
            return typeDecl.pRelativeName().getText();
        else return typeDecl.pRelativeName().getText() + "_Access";
    }

    /**
     * Create an entity that is the conversion of a subprogram parameter, named `${name}_Arg` in the
     * C API, to the `type` Ada type.
     */
    public static String makeParamConversion(
            Name name, Libadalang.BaseTypeDecl type, boolean isOutMode) {
        type = (Libadalang.BaseTypeDecl) type.pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        String valueVarTypename = type.pFullyQualifiedName();

        StringBuilder builder = new StringBuilder();
        String argName = name.toPascalWithUnderscore();
        // Class wide types need a pointer conversion function.
        if (type instanceof Libadalang.ClasswideTypeDecl || type.pIsAbstractType()) {
            Libadalang.BaseTypeDecl specificType = type.pSpecificType();
            String accessType =
                    name.toPascalWithUnderscore().concat("_").concat(asAccess(specificType));
            builder.append("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(type.pFullyQualifiedName())
                    .append("; function ")
                    .append(argName)
                    .append("_Converter is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(accessType)
                    .append(");\n")
                    .append(name.toPascalWithUnderscore())
                    .append("_Access : ")
                    .append(accessType)
                    .append(":= ")
                    .append(argName)
                    .append("_Converter (")
                    .append(argName)
                    .append("_Arg);");
        }
        // Begin the declaration of the value.
        builder.append(argName).append("_Value : ").append(valueVarTypename);
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // If the type is an array, generate the following:
            // .. code::
            //     ${Arg}_Value : ${Type}
            //       (${indexType} ({Arg}_Arg.First) .. ${indexType} ({Arg}_Arg.Last))
            //       with Address => ${Arg}_Arg;
            //     pragma Import (Ada, ${Arg}_Value);
            //
            // If the array type is not unconstrained, the bounds will not be generated.
            Libadalang.ArrayTypeDef typeDef =
                    (Libadalang.ArrayTypeDef) ((Libadalang.TypeDecl) type).fTypeDef();
            if (typeDef.fIndices() instanceof Libadalang.UnconstrainedArrayIndices indices) {
                Libadalang.UnconstrainedArrayIndex index =
                        (Libadalang.UnconstrainedArrayIndex) indices.fTypes().getChild(0);
                BaseTypeDecl indexType = index.fSubtypeName().pNameDesignatedType();
                builder.append(" (")
                        .append(indexType.pFullyQualifiedName())
                        .append(" (")
                        .append(argName)
                        .append("_Arg.First) .. ")
                        .append(indexType.pFullyQualifiedName())
                        .append(" (")
                        .append(argName)
                        .append("_Arg.Last))");
            }
            builder.append(" with Address => ")
                    .append(argName)
                    .append("_Arg.Data; pragma Import (Ada, ")
                    .append(argName)
                    .append("_Value)");
        } else if (type instanceof Libadalang.ClasswideTypeDecl || type.pIsAbstractType()) {
            builder.append(" renames ").append(argName).append("_Access.all");
        } else if (type.pIsRecordType(Libadalang.AdaNode.NONE) || isOutMode) {
            // If the type is a record or when the parameter uses an ``out`` mode, generate the
            // following:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} with Address => ${Arg}_Arg;
            //     pragma Import (Ada, ${Arg}_Value);
            //
            // The pragma is used to avoid calling the default initializer of the subparam's type,
            // which would overwrite the argument's data.
            builder.append(" with Address => ")
                    .append(argName)
                    .append("_Arg; pragma Import (Ada, ")
                    .append(argName)
                    .append("_Value)");
        } else if (type.equals(type.pBoolType())) {
            // Boolean types do not exist in the Interfaces.C package: they are instead binded as
            // Ints.
            builder.append(" := ").append(argName).append("_Arg /= 0");
        } else {
            // Otherwise, the type should convertible with a simple cast:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} := ${Type} (${Arg}_Arg);
            builder.append(" := ")
                    .append(valueVarTypename)
                    .append(" (")
                    .append(argName)
                    .append("_Arg)");
        }
        return builder.toString();
    }

    /**
     * Create an entity that is the conversion of a C interface type parameter that represent the
     * new value of the setter for ``component`` into the actual Ada type.
     */
    public static String makeParamConversion(Component component) {
        return makeParamConversion(component.name, component.getType(), false);
    }

    /**
     * Create an entity that is the conversion from a C interface type parameter into the actual Ada
     * type.
     */
    public static String makeParamConversion(SubpParam param) {
        return makeParamConversion(param.name, param.getType(), param.isOutMode());
    }

    /** Create a string to call a function from the proxy. */
    public static String call(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.getOriginName());

        if (!subp.parameters.isEmpty()) {
            builder.append(" (")
                    .append(
                            subp.parameters.stream()
                                    .map(p -> p.name.toPascalWithUnderscore().concat("_Value"))
                                    .collect(Collectors.joining(", ")))
                    .append(")");
        }
        return builder.toString();
    }

    /**
     * Create a call to the subprogram for when a shadow type does not override the subprogram.
     *
     * <p>Parameters with the same type as the first (controlling) parameter will be cast to the
     * non-shadow type.
     */
    public static String callParent(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        Libadalang.BaseTypeDecl controllingType = subp.parameters.get(0).getType();
        builder.append(subp.getOriginName()).append("(");
        for (int i = 0; i < subp.parameters.size(); i++) {
            SubpParam param = subp.parameters.get(i);
            if (i != 0) builder.append(", ");
            boolean needsCast = param.getType().equals(controllingType);
            if (needsCast) builder.append(controllingType.pFullyQualifiedName()).append(" (");
            builder.append(param.name.toPascalWithUnderscore());
            if (needsCast) builder.append(")");
        }
        builder.append(")");
        return builder.toString();
    }

    /** Create the necessary declarations for the return statement. */
    public static String makeReturnDeclarations(Libadalang.BaseTypeDecl returnedType) {
        StringBuilder builder = new StringBuilder();
        returnedType =
                (Libadalang.BaseTypeDecl)
                        returnedType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        String typename = returnedType.pFullyQualifiedName();
        if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)) {
            // When returning records, we need to convert an access to `System.Address`: declare a
            // converter.
            builder.append("package Return_Type_Converter is new")
                    .append(" System.Address_To_Access_Conversions(")
                    .append(typename)
                    .append(");");
        } else if (returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            String accessType = asAccess(returnedType);
            builder.append("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(typename)
                    .append(" with Size => Standard'Address_Size;\n")
                    .append("Returned_Array : ")
                    .append(accessType)
                    .append(";");
        }
        return builder.toString();
    }

    /**
     * Create a string of the value returned by functions, with the necessary cast to the C
     * interface type.
     */
    public static String makeReturnConversion(
            Libadalang.BaseTypeDecl returnedType, String returnedValue) {
        StringBuilder builder = new StringBuilder();
        returnedType =
                (Libadalang.BaseTypeDecl)
                        returnedType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        String typeName = returnedType.pFullyQualifiedName();
        if (returnedType.equals(returnedType.pBoolType())) {
            builder.append("return (if ").append(returnedValue).append(" then 1 else 0)");
        } else if (returnedType.pIsScalarType(Libadalang.AdaNode.NONE)) {
            // If the value is a scalar, simply cast to the C interface type.
            builder.append("return ")
                    .append(cInterfaceTypename(returnedType))
                    .append(" (")
                    .append(typeName)
                    .append("'(")
                    .append(returnedValue)
                    .append("))");
        } else if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)) {
            // If the type returned is an address (i.e. not a scalar), convert the returned value to
            // ``System.Address`` using the entity created by ``makeReturnTypeConverter``.
            //
            // TODO: For the moment, it considers that only value types are returned, and creates a
            // dynamically allocated copy of the value. When access types are supported, do not copy
            // the value to the heap.
            builder.append("return Return_Type_Converter.To_Address(new ")
                    .append(typeName)
                    .append("'(")
                    .append(returnedValue)
                    .append("))");
        } else if (returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // If the return type is an array, generate the following:
            // :: code:
            //    declare
            //       type ${accessType} is access all ${typeName}
            //          with Size => Standard'Address_Size;
            //       Returned_Array : ${accessType} :=
            //          new ${typeName}'(${returnedValue});
            //    begin
            //    return (First => Returned_Array'First,
            //            Last  => Returned_Array'Last,
            //            Data  => Returned_Array.all'Address);
            //    end
            builder.append("Returned_Array := new ")
                    .append(typeName)
                    .append("'(")
                    .append(returnedValue)
                    .append(");\n")
                    .append("return (First => Interfaces.C.Int (Returned_Array.all'First),")
                    .append("Last => Interfaces.C.Int (Returned_Array.all'Last), ")
                    .append("Data => Returned_Array.all'Address)");
        }
        return builder.toString();
    }

    /** Create a string of the return statement for value returned by component getter functions. */
    public static String makeGetterReturnConversion(String componentAccess) {
        StringBuilder builder = new StringBuilder("return ");
        builder.append(componentAccess).append("'Address");
        return builder.toString();
    }

    /** Return the file name of a module with a given extension. */
    public static Path toAdaFilename(Package pack, String suffix) {
        StringBuilder builder = new StringBuilder();
        builder.append(pack.getFullyQualifiedName().toLowerCase().replace(".", "-"));
        if (suffix != null) builder.append(suffix);
        return Path.of(builder.toString());
    }

    /** Return the C Interface typename of a native type. */
    public static String cInterfaceNativeTypename(NativeType nativeType) {
        switch (nativeType) {
            case BOOL:
                return "Interfaces.C.int";
            case FLOAT128:
                return "Interfaces.C.long_double";
            case FLOAT32:
                return "Interfaces.C.C_float";
            case FLOAT64:
                return "Interfaces.C.double";
            case UINT8:
            case SINT8:
                return "Interfaces.C.char";
            case UINT16:
            case SINT16:
                return "Interfaces.C.short";
            case UINT32:
            case SINT32:
                return "Interfaces.C.int";
            case UINT64:
            case SINT64:
                return "Interfaces.C.long";
            case UINT128:
            case SINT128:
                return "Interfaces.C.long";
            case STRING:
                return "Polyglot.Ada.Strings.Polyglot_String";
            case VOID:
                throw new IllegalArgumentException("Ada has no ``void`` type.");
            default:
                throw new UnsupportedOperationException(
                        nativeType.toString() + " is not handled yet");
        }
    }

    /** Return the C Interface typename of a type. */
    public static String cInterfaceTypename(Libadalang.BaseTypeDecl bTypeDecl) {
        NativeType nativeType = checkNativeType(bTypeDecl);
        if (nativeType != null) return cInterfaceNativeTypename(nativeType);
        if (bTypeDecl instanceof Libadalang.ClasswideTypeDecl) return "System.Address";
        Libadalang.TypeDef typeDef =
                ((Libadalang.TypeDecl) bTypeDecl.pRootType(Libadalang.AdaNode.NONE)).fTypeDef();
        if (typeDef instanceof Libadalang.PrivateTypeDef
                || typeDef instanceof Libadalang.RecordTypeDef) return "System.Address";
        if (typeDef instanceof Libadalang.ArrayTypeDef) return "Polyglot.Ada.Arrays.Polyglot_Array";

        throw new UnsupportedOperationException("Type not supported");
    }
}
