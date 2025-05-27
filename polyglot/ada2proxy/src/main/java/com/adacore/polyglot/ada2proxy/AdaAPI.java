package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.Expr;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.NameTypeExpr;
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
        }
        Libadalang.Symbol[] symbols = decl.pFullyQualifiedNameArray(false);
        return new FullyQualifiedName(
                Stream.of(symbols).map(s -> s.text).map(Name::fromLower).toList());
    }

    /** Create a {@link TypeExpr} to ``decl`` , or its parent if ``onlyParent`` is true. */
    public static NameTypeExpr makeTypeExpr(Libadalang.BasicDecl decl) {
        return makeProxyFullyQualifiedName(decl).asTypeExpr();
    }

    /** Return the native type corresponding to bTypeDecl, or null if the type is not native. */
    public static NativeType checkNativeType(Libadalang.BaseTypeDecl bTypeDecl) {
        if (bTypeDecl.isNone()) return NativeType.VOID;

        bTypeDecl = bTypeDecl.pRootType(Libadalang.AdaNode.NONE);

        if (bTypeDecl instanceof Libadalang.TypeDecl typeDecl) {
            if (typeDecl.equals(typeDecl.pBoolType())) return NativeType.BOOL;
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

    /** Build a string containing the parameter specifications of a subprogram. */
    public static String cInterfaceParameters(Subprogram subp) {
        return subp.parameters.stream()
                .map(
                        (p -> {
                            StringBuilder argBuilder = new StringBuilder();
                            argBuilder.append(p.name.toPascalWithUnderscore() + "_Arg");
                            argBuilder.append(": ");
                            // If the parameter has a Out mode, it is a reference and will be
                            // passed as an address.
                            if (p.isOutMode()) argBuilder.append("System.Address");
                            else argBuilder.append(cInterfaceTypename(p.getType()));
                            return argBuilder.toString();
                        }))
                .collect(Collectors.joining("; "));
    }

    /** Create a string that refers to the type pointed by typeExpr as an access type. */
    public static String asAccess(Libadalang.TypeExpr typeExpr) {
        Libadalang.BaseTypeDecl typeDecl = typeExpr.pDesignatedTypeDecl();
        if (typeDecl.pIsAccessType(Libadalang.AdaNode.NONE)) return typeExpr.getText();
        else return typeExpr.getText() + "_Access";
    }

    /**
     * Create an entity that is the conversion of a subprogram parameter, named `${name}_Arg` in the
     * C API, to the `type` Ada type.
     */
    private static String makeParamConversion(
            Name name, Libadalang.BaseTypeDecl type, boolean isOutMode) {
        type = (Libadalang.BaseTypeDecl) type.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        StringBuilder builder = new StringBuilder();
        String argName = name.toPascalWithUnderscore();
        builder.append(argName).append("_Value : ").append(type.pFullyQualifiedName());
        if (type.pIsRecordType(Libadalang.AdaNode.NONE) || isOutMode) {
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
        } else {
            // Otherwise, the type should convertible with a simple cast:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} := ${Type} (${Arg}_Arg);
            builder.append(" := ")
                    .append(type.pFullyQualifiedName())
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
                                    .map(p -> p.name.toPascalWithUnderscore() + "_Value")
                                    .collect(Collectors.joining(", ")))
                    .append(")");
        }
        return builder.toString();
    }

    /** Create the necessary declarations for the return statement. */
    public static String makeReturnDeclarations(Libadalang.BaseTypeDecl returnedType) {
        StringBuilder builder = new StringBuilder();
        returnedType =
                (Libadalang.BaseTypeDecl)
                        returnedType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)) {
            // When returning records, we need to convert an access to `System.Address`: declare a
            // converter.
            builder.append("package Return_Type_Converter is new")
                    .append(" System.Address_To_Access_Conversions(")
                    .append(returnedType.pFullyQualifiedName())
                    .append(");");
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
        if (returnedType.pIsScalarType(Libadalang.AdaNode.NONE)) {
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
                return "Interfaces.C.char_array";
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
        Libadalang.TypeDef typeDef =
                ((Libadalang.TypeDecl) bTypeDecl.pRootType(Libadalang.AdaNode.NONE)).fTypeDef();
        if (typeDef instanceof Libadalang.PrivateTypeDef
                || typeDef instanceof Libadalang.RecordTypeDef) return "System.Address";

        throw new UnsupportedOperationException("Type not supported");
    }
}
