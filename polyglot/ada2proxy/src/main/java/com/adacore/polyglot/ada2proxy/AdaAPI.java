package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI {

    /** Create a reference to decl, or its parent if ``onlyParent`` is true. */
    public static FullyQualifiedName makeProxyFullyQualifiedName(
            Libadalang.BasicDecl decl, Boolean onlyParent) {
        if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            NativeType nativeType = checkNativeType(typeDecl);
            if (nativeType != null) return nativeType.reference.name;
        }
        Libadalang.Symbol[] symbols = decl.pFullyQualifiedNameArray(false);
        if (onlyParent && symbols.length == 1) return null;
        return new FullyQualifiedName(
                Stream.of(onlyParent ? Arrays.copyOf(symbols, symbols.length - 1) : symbols)
                        .map(s -> s.text)
                        .map(Name::fromLower)
                        .toList());
    }

    /** Create a reference to decl, or its parent if ``onlyParent`` is true. */
    public static Reference makeReferenceTo(Libadalang.BasicDecl decl, Boolean onlyParent) {
        if (onlyParent && decl.pFullyQualifiedNameArray(false).length == 1) return null;
        return new Reference(
                makeProxyFullyQualifiedName(decl, onlyParent), false, false, false, false);
    }

    /** Return the native type corresponding to bTypeDecl, or null if the type is not native. */
    public static NativeType checkNativeType(Libadalang.BaseTypeDecl bTypeDecl) {
        if (bTypeDecl.isNone()) return NativeType.VOID;

        bTypeDecl = bTypeDecl.pRootType(Libadalang.AdaNode.NONE);

        if (bTypeDecl instanceof Libadalang.TypeDecl typeDecl)
            // If the BasicDecl was declared in the Standard Package, it is a builtin type.
            if (typeDecl.getUnit().equals(typeDecl.pStandardUnit())) {
                // TODO: Handle all builtin types

                // ``Standard.Boolean`` maps to BOOL.
                if (typeDecl.equals(typeDecl.pBoolType())) return NativeType.BOOL;
                else if (typeDecl.pIsIntType(Libadalang.AdaNode.NONE)) {
                    // Is there a way to know the max bounds of an integer type?
                    if (typeDecl.fTypeDef() instanceof Libadalang.SignedIntTypeDef)
                        return NativeType.SINT32;
                }
            }
        return null;
    }

    /** Create a reference to the type referenced by typeExpr */
    public static Reference makeReferenceTo(Libadalang.TypeExpr typeExpr) {
        if (typeExpr.isNone()) return NativeType.VOID.reference;
        return makeReferenceTo(typeExpr.pDesignatedTypeDecl(), false);
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file.
     */
    public static String makeInterfaces(List<Package> packages) {
        return packages.stream()
                .map(
                        p -> {
                            return "\"%s.proxy\""
                                    .formatted(p.getFullyQualifiedName().toLowerCase());
                        })
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
                            argBuilder.append(cInterfaceTypename(p.getFormalType()));
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
    private static String makeParamConversion(Name name, Libadalang.BaseTypeDecl type) {
        type = (Libadalang.BaseTypeDecl) type.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        StringBuilder builder = new StringBuilder();
        builder.append(name.toPascalWithUnderscore())
                .append("_Value : ")
                .append(type.pFullyQualifiedName());
        if (type.pIsRecordType(Libadalang.AdaNode.NONE)) {
            // If the type is a record, generated the following:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} with Address => ${Arg}_Arg;
            //     pragma Import (Ada, ${Arg}_Value);
            //
            // The pragma is used to avoid calling the default initializer of the subparam's type,
            // which would overwrite the argument's data.
            builder.append(" with Address => ")
                    .append(name.toPascalWithUnderscore())
                    .append("_Arg; pragma Import (Ada, ")
                    .append(name.toPascalWithUnderscore())
                    .append("_Value)");
        } else {
            // Otherwise, the type should convertible with a simple cast:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} := ${Type} (${Arg}_Arg);
            builder.append(" := ")
                    .append(type.pFullyQualifiedName())
                    .append(" (")
                    .append(name.toPascalWithUnderscore())
                    .append("_Arg)");
        }
        return builder.toString();
    }

    /**
     * Create an entity that is the conversion of a C interface type parameter that represent the
     * new value of the setter for ``component`` into the actual Ada type.
     */
    public static String makeParamConversion(Component component) {
        return makeParamConversion(component.name, component.getType());
    }

    /**
     * Create an entity that is the conversion from a C interface type parameter into the actual Ada
     * type.
     */
    public static String makeParamConversion(SubpParam param) {
        return makeParamConversion(param.name, param.getType());
    }

    /** Create a string to call a function from the proxy. */
    public static String call(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.name.toPascalWithUnderscore());
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

        if (returnedType.pIsScalarType(Libadalang.AdaNode.NONE)) {
            // If the value is a scalar, simply cast to the C interface type.
            builder.append(cInterfaceTypename(returnedType)).append(" (");
        } else {
            // If the type returned is an address (i.e. not a scalar), convert the returned value to
            // ``System.Address`` using the entity created by ``makeReturnTypeConverter``.
            //
            // TODO: For the moment, it considers that only value types are returned, and creates a
            // dynamically allocated copy of the value. When access types are supported, do not copy
            // the value to the heap.
            builder.append("Return_Type_Converter.To_Address(new ")
                    .append(returnedType.pFullyQualifiedName())
                    .append("'(");
        }
        // Build the call to the binded subprogram.
        builder.append(returnedValue);
        builder.append(")");
        if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)) builder.append(")");
        return builder.toString();
    }

    /**
     * Create a string to declare an entity to convert the value returned by the binded function if
     * necessary.
     */
    public static String makeReturnTypeConverter(Libadalang.BaseTypeDecl retType) {
        StringBuilder builder = new StringBuilder();
        retType =
                (Libadalang.BaseTypeDecl) retType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        if (retType.pIsRecordType(Libadalang.AdaNode.NONE)) {
            builder.append("package Return_Type_Converter is new")
                    .append(" System.Address_To_Access_Conversions(")
                    .append(retType.pFullyQualifiedName())
                    .append(");");
        }

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
