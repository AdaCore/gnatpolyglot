package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import com.adacore.libadalang.Libadalang.Expr;
import com.adacore.polyglot.LanguageAPI;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.TypeExpr;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI extends LanguageAPI {

    private Name projectName;

    public AdaAPI(Name projectName) {
        this.projectName = projectName;
    }

    public Name getProjectName() {
        return projectName;
    }

    /** Return `name` as it is used for declaring arguments in the generated code. */
    public String argName(Name name) {
        return name.toPascalWithUnderscore() + "_Arg";
    }

    /** Return `name` as it is used for passing converted values to binded subprograms. */
    public String valueName(Name name) {
        return name.toPascalWithUnderscore() + "_Value";
    }

    /** Create a unique temporary name, in the form of `{name}_{suffix}_{unique_number}`. */
    public String makeTemp(Name name, String suffix) {
        return makeTempName(name.concat(Name.fromPascalWithUnderscore(suffix)))
                .toPascalWithUnderscore();
    }

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
    public String getProxyInterfaces(List<Package> packages) {
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
    public String getAggregateInterfaces(List<Package> packages) {
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
    public String proxyName(Name name) {
        return name.toPascalWithUnderscore() + "_Proxy";
    }

    /**
     * Return name with the correct Ada syntax with ``_Proxy`` as a suffix and the parameter type
     * names to avoid conflicts with duplicated subprograms.
     */
    /** Return name with the correct Ada syntax with ``_Proxy`` as a suffix. */
    public String proxyName(Subprogram subp) {
        StringBuilder builder = new StringBuilder(proxyName(subp.name));
        for (var p : subp.parameters) {
            builder.append("_").append(p.getType().pRelativeNameText().toString());
        }

        return builder.toString();
    }

    /** Return the typename of the parameter */
    private String cInterfaceParamTypename(SubpParam p) {
        // If the parameter has a Out mode, it is a reference and will be passed as an address.
        if (p.isOutMode() && !p.getType().pIsArrayType(Libadalang.AdaNode.NONE))
            return "System.Address";
        else return cInterfaceTypename(p.getType());
    }

    /** Build a string containing the parameter specifications of a subprogram. */
    public String cInterfaceParameters(Subprogram subp) {
        return subp.parameters.stream()
                .map(
                        (p -> {
                            StringBuilder argBuilder = new StringBuilder();
                            argBuilder
                                    .append(argName(p.name))
                                    .append(": ")
                                    .append(cInterfaceParamTypename(p));
                            return argBuilder.toString();
                        }))
                .collect(Collectors.joining("; "));
    }

    /**
     * Build a string containing the parameter specifications of the constructor of a record. If the
     * function's role is {@link RoleKind#SHADOW_ALLOC}, also add the self and vtable arguments
     */
    public String cInterfaceParameters(Record rec, FunctionDecl function) {
        StringBuilder res = new StringBuilder();
        res.append(
                function.type.parameters.stream()
                        .map(
                                p -> {
                                    // Get the component corresponding to the constuctor's argument.
                                    Component component = rec.getComponent(p.name);
                                    StringBuilder argBuilder = new StringBuilder();
                                    argBuilder.append(argName(p.name)).append(" : ");
                                    // When the component does not exist, it means we could be
                                    // dealing with either the ``vtable`` or ``self`` argument of
                                    // shadow types constructors which are non-null const void
                                    // pointers.
                                    if (component != null) {
                                        argBuilder.append(cInterfaceTypename(component.getType()));
                                    } else if (p.type.equals(
                                            NativeType.VOID.typeExpr.makePointer(true, true))) {
                                        argBuilder.append("System.Address");
                                    } else {
                                        throw new IllegalArgumentException(
                                                "Illegal constructor argument");
                                    }

                                    return argBuilder.toString();
                                })
                        .collect(Collectors.joining("; ")));
        if (function.role.kind == RoleKind.SHADOW_ALLOC) {
            res.append(!res.isEmpty() ? ";" : "")
                    .append("Self_Arg : System.Address; Vtable_Arg : System.Address");
        }
        return res.toString();
    }

    /** Create a string that refers to the type pointed by typeExpr as an access type. */
    public String asAccess(Libadalang.BaseTypeDecl typeDecl) {
        if (typeDecl.pIsAccessType(Libadalang.AdaNode.NONE))
            return typeDecl.pRelativeName().getText();
        else return typeDecl.pRelativeName().getText() + "_Access";
    }

    /**
     * Create an entity that is the conversion of a subprogram parameter, named `${name}_Arg` in the
     * C API, to the `type` Ada type.
     */
    public String makeParamConversion(Name name, Libadalang.BaseTypeDecl type, boolean isOutMode) {
        type = (Libadalang.BaseTypeDecl) type.pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        String valueVarTypename = type.pFullyQualifiedName();

        StringBuilder builder = new StringBuilder();
        String argName = argName(name);
        String tempVarValue = null;
        // Class wide types need a pointer conversion function.
        if (type instanceof Libadalang.ClasswideTypeDecl || type.pIsAbstractType()) {
            Libadalang.BaseTypeDecl specificType = type.pSpecificType();
            String accessType = makeTemp(name, asAccess(specificType));
            String converter = makeTemp(name, "Converter");
            tempVarValue = makeTemp(name, "Access");
            builder.append("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(type.pFullyQualifiedName())
                    .append("; function ")
                    .append(converter)
                    .append(" is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(accessType)
                    .append(");\n")
                    .append(tempVarValue)
                    .append(" : ")
                    .append(accessType)
                    .append(":= ")
                    .append(converter)
                    .append(" (")
                    .append(argName)
                    .append(")\n;");
        }
        // Begin the declaration of the value.
        builder.append(valueName(name)).append(" : ").append(valueVarTypename);
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
                        .append(".First) .. ")
                        .append(indexType.pFullyQualifiedName())
                        .append(" (")
                        .append(argName)
                        .append(".Last))");
            }
            builder.append(" with Address => ")
                    .append(argName)
                    .append(".Data; pragma Import (Ada, ")
                    .append(valueName(name))
                    .append(")");
        } else if (type instanceof Libadalang.ClasswideTypeDecl || type.pIsAbstractType()) {
            builder.append(" renames ").append(tempVarValue).append(".all");
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
                    .append("; pragma Import (Ada, ")
                    .append(valueName(name))
                    .append(")");
        } else if (type.equals(type.pBoolType())) {
            // Boolean types do not exist in the Interfaces.C package: they are instead binded as
            // Ints.
            builder.append(" := ").append(argName).append(" /= 0");
        } else if (type.pIsEnumType(Libadalang.AdaNode.NONE) && !type.equals(type.pStdCharType())) {
            builder.append(" := ")
                    .append(valueVarTypename)
                    .append("'Enum_Val (")
                    .append(argName)
                    .append(")");
        } else {
            // Otherwise, the type should convertible with a simple cast:
            // .. code::
            //
            //     ${Arg}_Value : ${Type} := ${Type} (${Arg}_Arg);
            builder.append(" := ")
                    .append(valueVarTypename)
                    .append(" (")
                    .append(argName)
                    .append(")");
        }
        return builder.toString();
    }

    /**
     * Create an entity that is the conversion of a C interface type parameter that represent the
     * new value of the setter for ``component`` into the actual Ada type.
     */
    public String makeParamConversion(Component component) {
        return makeParamConversion(component.name, component.getType(), false);
    }

    /**
     * Create an entity that is the conversion from a C interface type parameter into the actual Ada
     * type.
     */
    public String makeParamConversion(SubpParam param) {
        return makeParamConversion(param.name, param.getType(), param.isOutMode());
    }

    /** Create a string to call a function from the proxy. */
    public String call(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.getOriginName());

        if (!subp.parameters.isEmpty()) {
            builder.append(" (")
                    .append(
                            subp.parameters.stream()
                                    .map(p -> valueName(p.name))
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
    public String callParent(Subprogram subp) {
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
    public String makeReturnDeclarations(Libadalang.BaseTypeDecl returnedType) {
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
    public String makeReturnConversion(Libadalang.BaseTypeDecl returnedType, String returnedValue) {
        StringBuilder builder = new StringBuilder();
        returnedType =
                (Libadalang.BaseTypeDecl)
                        returnedType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        String typeName = returnedType.pFullyQualifiedName();
        if (returnedType.equals(returnedType.pBoolType())) {
            builder.append("return (if ").append(returnedValue).append(" then 1 else 0)");
        } else if (returnedType.pIsEnumType(Libadalang.AdaNode.NONE)
                && !returnedType.equals(returnedType.pStdCharType())) {
            builder.append("return ")
                    .append(typeName)
                    .append("'Enum_Rep (")
                    .append(returnedValue)
                    .append(")");
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
    public String makeGetterReturnConversion(String componentAccess) {
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
    public String cInterfaceNativeTypename(NativeType nativeType) {
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
    public String cInterfaceTypename(Libadalang.BaseTypeDecl bTypeDecl) {
        NativeType nativeType = checkNativeType(bTypeDecl);
        if (nativeType != null) return cInterfaceNativeTypename(nativeType);
        if (bTypeDecl instanceof Libadalang.ClasswideTypeDecl) return "System.Address";
        Libadalang.TypeDef typeDef =
                ((Libadalang.TypeDecl) bTypeDecl.pRootType(Libadalang.AdaNode.NONE)).fTypeDef();
        if (typeDef instanceof Libadalang.PrivateTypeDef
                || typeDef instanceof Libadalang.RecordTypeDef) return "System.Address";
        if (typeDef instanceof Libadalang.ArrayTypeDef) return "Polyglot.Ada.Arrays.Polyglot_Array";
        if (bTypeDecl.pIsEnumType(Libadalang.AdaNode.NONE)
                && !bTypeDecl.equals(bTypeDecl.pStdCharType()))
            return cInterfaceNativeTypename(NativeType.SINT32);

        throw new UnsupportedOperationException("Type not supported");
    }

    /**
     * Return a string of a type definition that corresponds to the subprogram's access type in the
     * C ABI.
     */
    /** */
    public String subprogramDispatchType(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.isProcedure() ? "procedure " : "function ")
                // There will always be at least two arguments here: the first argument will always
                // be the vtable, and the second argument the dispatching object (i.e the first
                // value of subp.parameters).
                .append("(VTable : System.Address; ")
                .append(cInterfaceParameters(subp))
                .append(")");
        if (!subp.isProcedure())
            builder.append("return ").append(cInterfaceTypename(subp.getReturnType()));
        return builder.toString();
    }

    /**
     * Create a declaration for the function overriding subp for the shadow type, replacing any
     * occurences of the controlling type with the shadow type.
     */
    public String makeShadowOverride(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.isProcedure() ? "procedure " : "function ")
                .append(subp.name.toPascalWithUnderscore());
        builder.append("(");

        SubpParam firstParam = subp.parameters.get(0);
        Libadalang.BaseTypeDecl controllingType = firstParam.getType();
        String shadowTypename = firstParam.getType().pRelativeName().getText() + "_Shadow";

        builder.append(firstParam.name.toPascalWithUnderscore()).append(" : ");
        if (firstParam.getMode() instanceof Libadalang.ModeInOut) builder.append("in out ");
        else if (firstParam.getMode() instanceof Libadalang.ModeOut) builder.append("out ");
        builder.append(shadowTypename);

        for (var param : subp.parameters.stream().skip(1).toList()) {
            builder.append("; ").append(param.name.toPascalWithUnderscore()).append(" : ");
            if (param.getMode() instanceof Libadalang.ModeInOut) builder.append("in out ");
            else if (param.getMode() instanceof Libadalang.ModeOut) builder.append("out ");
            if (param.getType().equals(controllingType)) builder.append(shadowTypename);
            else builder.append(param.getType().pFullyQualifiedName());
        }
        builder.append(")");
        if (!subp.isProcedure())
            builder.append("return ")
                    .append(
                            subp.getReturnType().equals(controllingType)
                                    ? shadowTypename
                                    : subp.getReturnType().pFullyQualifiedName());
        return builder.toString();
    }

    public String makeShadowReturnDecl(Libadalang.BaseTypeDecl returnedType) {
        StringBuilder builder = new StringBuilder();
        returnedType =
                (Libadalang.BaseTypeDecl)
                        returnedType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        String typename = returnedType.pFullyQualifiedName();
        if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)
                || returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // When returning records, we need to convert an Address to an access`: declare a
            // converter.
            builder.append("type ")
                    .append(asAccess(returnedType))
                    .append(" is access all ")
                    .append(typename)
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n");
            builder.append("function Converter is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(asAccess(returnedType))
                    .append(");\n")
                    // The returned value will be located on the heap and will be deallocated at
                    // some point.
                    .append("procedure Free is new Ada.Unchecked_Deallocation (")
                    .append(typename)
                    .append(", ")
                    .append(asAccess(returnedType))
                    .append(");\n");
            // The value will be received as an address or a Polyglot_Array. In order to be able to
            // free it, it needs to be stored in an access variable.
            builder.append("Returned_Access : ")
                    .append(asAccess(returnedType))
                    .append(" := ")
                    .append("Converter (Returned_Value");
            // Get the data of the Polyglot_Array or String when the return type is an array.
            if (returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) builder.append(".Data");
            builder.append(");");
        }

        return builder.toString();
    }

    /** Return the return statement of shadow dispatching functions. */
    public String makeShadowReturn(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        Libadalang.BaseTypeDecl returnedType =
                (Libadalang.BaseTypeDecl)
                        subp.getReturnType().pMostVisiblePart(Libadalang.AdaNode.NONE, false);
        String typename = returnedType.pFullyQualifiedName();
        builder.append("return Result : ").append(typename).append(" := ");
        if (returnedType.pIsRecordType(Libadalang.AdaNode.NONE)
                || returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // Values are returned on the heap from the target language. However, the parent of the
            // shadow function does not expect an acess or an address, but a value type instead. We
            // need to copy the returned value to the stack and free its heap counterpart.
            builder.append("Returned_Access.all")
                    .append(" do\n")
                    .append("Free (Returned_Access);\n")
                    .append("end return");
        } else {
            builder.append(returnedType.pFullyQualifiedName()).append(" (Returned_Value)");
        }

        return builder.toString();
    }

    /**
     * Return a string of the argument for param when calling an extern subprogram from a vtable.
     */
    public String makeDispatchedArgument(SubpParam param) {
        StringBuilder builder = new StringBuilder();

        NativeType nativeType = checkNativeType(param.getType());
        if (param.getType().pIsRecordType(Libadalang.AdaNode.NONE)
                || (param.getType().pIsScalarType(Libadalang.AdaNode.NONE) && param.isOutMode())) {
            builder.append(param.name.toPascalWithUnderscore()).append("'Address");
        } else if (param.getType().pIsScalarType(Libadalang.AdaNode.NONE)) {
            builder.append(cInterfaceNativeTypename(nativeType))
                    .append(" (")
                    .append(param.name.toPascalWithUnderscore())
                    .append(")");
        } else if (param.getType().pIsArrayType(Libadalang.AdaNode.NONE)) {
            builder.append("(First => Interfaces.C.int (")
                    .append(param.name.toPascalWithUnderscore())
                    .append("'First),")
                    .append(" Last => Interfaces.C.int (")
                    .append(param.name.toPascalWithUnderscore())
                    .append("'Last),")
                    .append(" Data => ")
                    .append(param.name.toPascalWithUnderscore())
                    .append("'Address)");
            ;
        }

        return builder.toString();
    }

    public String makeDefaultReturn(Subprogram subp) {
        BaseTypeDecl returnType = subp.getReturnType();
        if (returnType.equals(returnType.pStdCharType()))
            return "return Interfaces.C.To_C ( Character'Val(0))";
        if (returnType.pIsScalarType(Libadalang.AdaNode.NONE)) return "return 0";
        if (returnType.pIsRecordType(Libadalang.AdaNode.NONE) || returnType.pIsPrivate())
            return "return System.Null_Address";
        if (returnType.pIsArrayType(Libadalang.AdaNode.NONE))
            return " return(0, 0, System.Null_Address)";
        return "return (others => <>)";
    }
}
