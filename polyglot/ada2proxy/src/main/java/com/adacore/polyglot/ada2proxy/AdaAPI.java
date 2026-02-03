package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
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

    private final BindableDeclChecker declChecker = new BindableDeclChecker();

    public AdaAPI(Name projectName) {
        this.projectName = projectName;
    }

    public Name getProjectName() {
        return projectName;
    }

    public BindableDeclChecker getDeclChecker() {
        return declChecker;
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

    /**
     * Return the `name` as it should be in the proxy (i.e. turns operators to their predifined name
     * proxy).
     */
    public static Name functionProxyName(String name) {
        return switch (name) {
            case "\"+\"" -> Name.operatorPlus;
            case "\"-\"" -> Name.operatorMinus;
            case "\"*\"" -> Name.operatorMult;
            case "\"/\"" -> Name.operatorDiv;
            case "\"**\"" -> Name.operatorPow;
            case "\"mod\"" -> Name.operatorMod;
            case "\"rem\"" -> Name.operatorRem;
            case "\"abs\"" -> Name.operatorAbs;
            case "\"&\"" -> Name.operatorConcat;
            case "\"=\"" -> Name.operatorEq;
            case "\"/=\"" -> Name.operatorNe;
            case "\"<\"" -> Name.operatorLt;
            case "\"<=\"" -> Name.operatorLe;
            case "\">\"" -> Name.operatorGt;
            case "\">=\"" -> Name.operatorGe;
            case "\"and\"" -> Name.operatorBitAnd;
            case "\"or\"" -> Name.operatorBitOr;
            case "\"xor\"" -> Name.operatorBitXor;
            case "\"not\"" -> Name.operatorBitNot;
            case String s -> Name.fromLower(s);
        };
    }

    /** Create a {@link FullyQualifiedName} to ``dn``. */
    public static FullyQualifiedName makeProxyFullyQualifiedName(Libadalang.DefiningName dn) {
        Libadalang.Symbol[] symbols = dn.pFullyQualifiedNameArray();
        return new FullyQualifiedName(
                Stream.of(symbols).map(s -> s.text).map(s -> functionProxyName(s)).toList());
    }

    /** Create a {@link FullyQualifiedName} to ``decl``, or its parent if ``onlyParent`` is true. */
    public static FullyQualifiedName makeProxyFullyQualifiedName(Libadalang.BasicDecl decl) {
        if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            NativeType nativeType = checkNativeType(typeDecl);
            if (nativeType != null) return nativeType.typeExpr.name;
            // We must not show class wide types in the proxy.
            decl = typeDecl.pSpecificType();
        }
        Libadalang.Symbol[] symbols = decl.pFullyQualifiedNameArray(false);
        return new FullyQualifiedName(
                Stream.of(symbols).map(s -> s.text).map(s -> functionProxyName(s)).toList());
    }

    /** Create a {@link TypeExpr} to ``decl``. */
    public static TypeExpr makeTypeExpr(Libadalang.BasicDecl decl) {
        if (decl.isNone()) return NativeType.VOID.typeExpr;
        else if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            if (AdaTypeMatcher.isStringType(typeDecl)) return NativeType.STRING.typeExpr;
            else if (typeDecl.pIsArrayType(Libadalang.AdaNode.NONE)) {
                return makeTypeExpr(typeDecl.pCompType(false, Libadalang.AdaNode.NONE)).makeArray();
            } else if (typeDecl.pIsAccessType(Libadalang.AdaNode.NONE)) {
                Libadalang.TypeDecl rootType =
                        (Libadalang.TypeDecl) typeDecl.pRootType(Libadalang.AdaNode.NONE);
                Libadalang.AccessDef accessDef = (Libadalang.AccessDef) rootType.fTypeDef();
                boolean hasNonNull = accessDef.fHasNotNull().pAsBool();
                boolean hasConst = false;
                if (rootType.fTypeDef() instanceof Libadalang.TypeAccessDef access)
                    hasConst = access.fHasConstant().pAsBool();
                return makeTypeExpr(typeDecl.pAccessedType(Libadalang.AdaNode.NONE))
                        .makePointer(hasNonNull, hasConst);
            }
        }
        return makeProxyFullyQualifiedName(decl).asTypeExpr();
    }

    /** Return the native type corresponding to bTypeDecl, or null if the type is not native. */
    public static NativeType checkNativeType(Libadalang.BaseTypeDecl bTypeDecl) {
        if (bTypeDecl.isNone()) return NativeType.VOID;

        // Derivating from an enum (i.e. booleans) should not result in its root native type:
        //
        // package Test is
        //    type My_Boolean is new My_Boolean;
        //    procedure Print_My_Boolean (B: My_Boolean);
        // end Test;
        //
        // In this example, a separate enumeration should appear in the proxy, and we don't want to
        // bind Print_My_Boolean as `void print_my_boolean(bool)`. Since native type checks are done
        // on the root type in order to ensure that the correct size of integer is used, we check
        // that `bTypeDecl` not the `Standard.Boolean` type before getting its root type.
        //
        // This does not apply to other native types.
        if (bTypeDecl.pBaseSubtype(Libadalang.AdaNode.NONE).equals(bTypeDecl.pBoolType()))
            return NativeType.BOOL;

        bTypeDecl = bTypeDecl.pRootType(Libadalang.AdaNode.NONE);

        if (bTypeDecl instanceof Libadalang.TypeDecl typeDecl) {
            if (AdaTypeMatcher.isStringType(typeDecl)) return NativeType.STRING;
            if (AdaTypeMatcher.isCharacter(typeDecl)) return NativeType.UINT8;
            if (typeDecl.pIsIntType(Libadalang.AdaNode.NONE)) {
                // Compute the number of required bits to hold the values of the type and
                // find the smallest type able to hold it.
                try {
                    if (typeDecl.fTypeDef() instanceof Libadalang.SignedIntTypeDef) {
                        Libadalang.DiscreteRange range = typeDecl.pDiscreteRange();
                        int bitLength =
                                Math.max(
                                                range.lowBound.pEvalAsInt().bitLength(),
                                                range.highBound.pEvalAsInt().bitLength())
                                        + 1;
                        if (bitLength <= 8) return NativeType.SINT8;
                        if (bitLength <= 16) return NativeType.SINT16;
                        if (bitLength <= 32) return NativeType.SINT32;
                        if (bitLength <= 64) return NativeType.SINT64;
                        if (bitLength <= 128) return NativeType.SINT128;
                        throw new UnbindableDeclException(
                                typeDecl, "Unsupported integer size (%s)".formatted(bitLength));
                    }
                    if (typeDecl.fTypeDef() instanceof Libadalang.ModIntTypeDef) {
                        Libadalang.DiscreteRange range = typeDecl.pDiscreteRange();
                        int bitLength = range.highBound.pEvalAsInt().bitLength();
                        if (bitLength <= 8) return NativeType.UINT8;
                        if (bitLength <= 16) return NativeType.UINT16;
                        if (bitLength <= 32) return NativeType.UINT32;
                        if (bitLength <= 64) return NativeType.UINT64;
                        if (bitLength <= 128) return NativeType.UINT128;
                        throw new UnbindableDeclException(
                                typeDecl, "Unsupported integer size (%s)".formatted(bitLength));
                    }
                } catch (Libadalang.LangkitException e) {
                    throw new UnbindableDeclException(bTypeDecl, e.getLocalizedMessage());
                }
            }
            if (typeDecl.pIsFloatType(Libadalang.AdaList.NONE)) {
                try {
                    int digits = 0;
                    if (typeDecl.fTypeDef() instanceof Libadalang.FloatingPointDef floating) {
                        digits = floating.fNumDigits().pEvalAsInt().intValue();
                    }
                    if (digits >= 18) return NativeType.FLOAT128;
                    if (digits >= 11) return NativeType.FLOAT64;
                    return NativeType.FLOAT32;
                } catch (Libadalang.LangkitException e) {
                    throw new UnbindableDeclException(bTypeDecl, e.getLocalizedMessage());
                }
            }
        }

        return null;
    }

    /** Create a {@link TypeExpr} to the type referenced by typeExpr */
    public static TypeExpr makeTypeExpr(Libadalang.TypeExpr typeExpr) {
        if (typeExpr.isNone()) return NativeType.VOID.typeExpr;
        return makeTypeExpr(typeExpr.pDesignatedTypeDecl());
    }

    /** Create a proxy Name from an Ada defining name, using its canonical text */
    public static Name getName(Libadalang.DefiningName name) {
        return Name.fromLower(name.pCanonicalText().text);
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file.
     */
    public String getProxyInterfaces(List<Package> packages) {
        return packages.stream()
                .flatMap(
                        p -> {
                            return Stream.of(AdaAPI.toAdaFilename(p, ".ads").toString());
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
                                    AdaAPI.toAdaFilename(p, "-proxy.ads").toString());
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

    public static String getDisplayName(Libadalang.BasicDecl type) {
        if (type instanceof Libadalang.AnonymousTypeDecl anon) return anon.getImage();
        return type.pFullyQualifiedName();
    }

    public String getProxyAccessFullyQualifiedName(Libadalang.BaseTypeDecl type) {
        if (type.equals(type.pStdStringType())) return "Polyglot.Ada.Strings.String_Access";
        Libadalang.BasePackageDecl pack = (Libadalang.BasePackageDecl) type.pParentBasicDecl();
        if (Package.isAdaRuntimePackage(pack))
            return Package.getProxyUnitName(pack).concat(".").concat(asAccess(type));
        return pack.pFullyQualifiedName().concat(".Proxy.").concat(asAccess(type));
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

    public String createBoundCast(String data, Libadalang.BaseTypeDecl type, boolean isLowerBound) {
        StringBuilder builder = new StringBuilder();
        String indexTypeName = type.pIndexType(0, Libadalang.AdaNode.NONE).pFullyQualifiedName();
        builder.append("(if Polyglot.Ada.")
                .append(AdaTypeMatcher.isStringType(type) ? "Strings" : "Arrays")
                .append(".Length (")
                .append(data)
                .append(") > 0 then ")
                .append(indexTypeName)
                .append("(")
                .append(data)
                .append(isLowerBound ? ".First" : ".Last")
                .append(") else ")
                .append(indexTypeName)
                .append("'First")
                .append(isLowerBound ? " + 1)" : ")");
        return builder.toString();
    }

    /**
     * Create an entity that is the conversion of a subprogram parameter, named `${name}_Arg` in the
     * C API, to the `type` Ada type.
     */
    public String makeParamConversion(Name name, Libadalang.BaseTypeDecl type, boolean isOutMode) {
        String valueVarTypename = type.pFullyQualifiedName();

        StringBuilder builder = new StringBuilder();
        String argName = argName(name);
        String tempVarValue = null;
        String converter = null;
        // Class wide types need a pointer conversion function.
        if (AdaTypeMatcher.isBindedAsClass(type)) {
            String accessType = makeTemp(name, "Access_Type");
            converter = makeTemp(name, "Converter");
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
        } else if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            String arrayType = valueVarTypename;
            if (!type.pIsStaticallyConstrained()) {
                arrayType = makeTemp(name, "Constrained_Array");
                builder.append("type ")
                        .append(arrayType)
                        .append(" is new ")
                        .append(valueVarTypename)
                        .append(" (")
                        .append(createBoundCast(argName, type, true))
                        .append(" .. ")
                        .append(createBoundCast(argName, type, false))
                        .append(");\n");
                valueVarTypename = arrayType;
            }
            tempVarValue = makeTemp(name, "Value_Access");
            String accessTypename = makeTemp(name, "Constrained_Array_Access");
            builder.append("type ")
                    .append(accessTypename)
                    .append(" is access all ")
                    .append(arrayType)
                    .append(" with Size => Standard'Address_Size;\n");
            builder.append(tempVarValue)
                    .append(" : ")
                    .append(accessTypename)
                    .append(" with Address => ")
                    .append(argName)
                    .append(".Data'Address;\n")
                    .append("pragma Import(Ada, ")
                    .append(tempVarValue)
                    .append(");");
        } else if (AdaTypeMatcher.isArrayAccess(type)) {
            Libadalang.BaseTypeDecl arrayType = type.pAccessedType(Libadalang.AdaNode.NONE);
            String arrayTypename = arrayType.pFullyQualifiedName();
            String arrayDataType = cInterfaceTypename(arrayType);
            if (isOutMode) {
                String polyglotArrayValue = makeTemp(name, "Polyglot_Array");
                builder.append(polyglotArrayValue)
                        .append(" : ")
                        .append(arrayDataType)
                        .append(" with Address => ")
                        .append(argName)
                        .append("; pragma Import(Ada, ")
                        .append(polyglotArrayValue)
                        .append(");\n");
                argName = polyglotArrayValue;
            }
            if (!arrayType.pIsStaticallyConstrained()) {
                String constrainedArray = makeTemp(name, "Constrained_Array");
                builder.append("type ")
                        .append(constrainedArray)
                        .append(" is new ")
                        .append(arrayTypename)
                        .append(" (")
                        .append(createBoundCast(argName, arrayType, true))
                        .append(" .. ")
                        .append(createBoundCast(argName, arrayType, false))
                        .append(");\n");
                arrayTypename = constrainedArray;
            }
            tempVarValue = makeTemp(name, "Value_Access");
            String accessTypename = makeTemp(name, "Constrained_Array_Access");
            builder.append("type ")
                    .append(accessTypename)
                    .append(" is access all ")
                    .append(arrayTypename)
                    .append(" with Size => Standard'Address_Size;\n");
            builder.append(tempVarValue)
                    .append(" : ")
                    .append(accessTypename)
                    .append(" with Address => ")
                    .append(argName)
                    .append(".Data'Address;\n")
                    .append("pragma Import(Ada, ")
                    .append(tempVarValue)
                    .append(");");
        } else if (type.pIsAccessType(Libadalang.AdaNode.NONE)) {
            converter = makeTemp(name, "Converter");
            builder.append("function ")
                    .append(converter)
                    .append(" is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(valueVarTypename)
                    .append(");\n");
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
            builder.append(" renames ").append(tempVarValue).append(".all");
        } else if (AdaTypeMatcher.isBindedAsClass(type)) {
            builder.append(" renames ").append(tempVarValue).append(".all");
        } else if (AdaTypeMatcher.isArrayAccess(type)) {
            builder.append(" := ")
                    .append("(if ")
                    .append(tempVarValue)
                    .append(" = null then null else ")
                    .append(type.pAccessedType(Libadalang.AdaNode.NONE).pFullyQualifiedName())
                    .append(" (")
                    .append(tempVarValue)
                    .append(".all)'Unrestricted_Access)");
        } else if (isOutMode) {
            // If the parameter uses an ``out`` mode, generate the following:
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
        } else if (AdaTypeMatcher.isEnum(type)) {
            builder.append(" := ")
                    .append(valueVarTypename)
                    .append("'Enum_Val (")
                    .append(argName)
                    .append(")");
        } else if (type.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append(" := ").append(converter).append("(").append(argName).append(")");
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
        builder.append(subp.getOriginFullyQualifiedName());

        if (!subp.parameters.isEmpty()) {
            builder.append(" (")
                    .append(
                            subp.parameters.stream()
                                    .map(p -> getParamForCall(p))
                                    .collect(Collectors.joining(", ")))
                    .append(")");
        }
        return builder.toString();
    }

    public String getParamForCall(Component component) {
        Name name = component.name;
        BaseTypeDecl type = component.getType();
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            return "%s (%s)".formatted(type.pFullyQualifiedName(), valueName(name));
        }
        return valueName(name);
    }

    private String getParamForCall(SubpParam param) {
        Name name = param.name;
        BaseTypeDecl type = param.getType();
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            return "%s (%s)".formatted(type.pFullyQualifiedName(), valueName(name));
        }
        return valueName(name);
    }

    /** Create the necessary declarations for the return statement. */
    public String makeReturnDeclarations(Libadalang.BaseTypeDecl returnedType) {
        StringBuilder builder = new StringBuilder();
        String typename = returnedType.pFullyQualifiedName();
        if (AdaTypeMatcher.isBindedAsClass(returnedType)) {
            // When returning records, we need to convert an access to `System.Address`: declare a
            // converter.
            builder.append("package Return_Type_Converter is new")
                    .append(" System.Address_To_Access_Conversions(")
                    .append(typename)
                    .append(");");
        } else if (returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            builder.append("Returned_Array : ")
                    .append(getProxyAccessFullyQualifiedName(returnedType))
                    .append(";");
        } else if (returnedType.pIsAccessType(Libadalang.AdaNode.NONE)
                && !AdaTypeMatcher.isArrayAccess(returnedType)) {
            builder.append("function Return_Type_Converter is new")
                    .append(" Ada.Unchecked_Conversion (")
                    .append(typename)
                    .append(", System.Address);");
        }
        return builder.toString();
    }

    /**
     * Create a string of the value returned by functions, with the necessary cast to the C
     * interface type.
     */
    public String makeReturnConversion(Libadalang.BaseTypeDecl returnedType, String returnedValue) {
        StringBuilder builder = new StringBuilder();

        String typeName = returnedType.pFullyQualifiedName();
        if (returnedType.equals(returnedType.pBoolType())) {
            builder.append("return (if ").append(returnedValue).append(" then 1 else 0)");
        } else if (AdaTypeMatcher.isEnum(returnedType)) {
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
        } else if (AdaTypeMatcher.isBindedAsClass(returnedType)) {
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
        } else if (AdaTypeMatcher.isArrayAccess(returnedType)) {
            Libadalang.BaseTypeDecl indexType =
                    returnedType
                            .pAccessedType(Libadalang.AdaNode.NONE)
                            .pIndexType(0, Libadalang.AdaNode.NONE);
            builder.append("if ")
                    .append(returnedType.pParentBasicDecl().pFullyQualifiedName())
                    .append(".\"=\" (")
                    .append(returnedValue)
                    .append(", null) then\n")
                    .append("return (First => Interfaces.C.int (")
                    .append(indexType.pFullyQualifiedName())
                    .append("'First + 1), Last => Interfaces.C.int (")
                    .append(indexType.pFullyQualifiedName())
                    .append("'First), Data => System.Null_Address);\n")
                    .append("else\n")
                    .append("return (First => Interfaces.C.Int (")
                    .append(returnedValue)
                    .append(".all'First),")
                    .append("Last => Interfaces.C.Int (")
                    .append(returnedValue)
                    .append(".all'Last), ")
                    .append("Data => ")
                    .append(returnedValue)
                    .append(".all'Address);\n")
                    .append("end if");

        } else if (returnedType.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append("return Return_Type_Converter (").append(returnedValue).append(")");
        }
        return builder.toString();
    }

    /** Return the type that must be used when returning from a getter. */
    public String getterReturnTypename(Libadalang.BaseTypeDecl type) {
        if (AdaTypeMatcher.isArrayAccess(type))
            return cInterfaceTypename(type.pAccessedType(Libadalang.AdaNode.NONE));
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) return "Polyglot.Ada.Arrays.Polyglot_Array";
        return "System.Address";
    }

    /** Create a string of the return statement for value returned by component getter functions. */
    public String makeGetterReturnConversion(String componentAccess, Libadalang.BaseTypeDecl type) {
        StringBuilder builder = new StringBuilder("return ");
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // When returning references to arrays, returning a Polyglot_Array is
            // still necesssary.
            builder.append("(First => Interfaces.C.Int (")
                    .append(componentAccess)
                    .append("'First),")
                    .append("Last => Interfaces.C.Int (")
                    .append(componentAccess)
                    .append("'Last), ")
                    .append("Data => ")
                    .append(componentAccess)
                    .append("'Address)");
        } else if (AdaTypeMatcher.isArrayAccess(type)) {
            builder.append("(if ")
                    .append(type.pParentBasicDecl().pFullyQualifiedName())
                    .append(".\"=\" (")
                    .append(componentAccess)
                    .append(", null) then (1, 0, System.Null_Address) else ")
                    .append("(First => Interfaces.C.int (")
                    .append(componentAccess)
                    .append(".all'First),")
                    .append(" Last => Interfaces.C.int (")
                    .append(componentAccess)
                    .append(".all'Last),")
                    .append(" Data => ")
                    .append(componentAccess)
                    .append(".all'Address))");
        } else if (type.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append("Access_Converter (").append(componentAccess).append(")");
        } else {
            // Otherwise, just get the address.
            builder.append(componentAccess).append("'Address");
        }
        return builder.toString();
    }

    /** Return the file name of a module with a given extension. */
    public static Path toAdaFilename(Package pack, String suffix) {
        StringBuilder builder = new StringBuilder();
        builder.append(pack.getProxyUnitName().toLowerCase().replace(".", "-"));
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
    public String cInterfaceTypename(Libadalang.BaseTypeDecl typeDecl) {
        NativeType nativeType = checkNativeType(typeDecl);
        if (nativeType != null) return cInterfaceNativeTypename(nativeType);
        if (AdaTypeMatcher.isArrayAccess(typeDecl))
            return cInterfaceTypename(typeDecl.pAccessedType(Libadalang.AdaNode.NONE));
        if (typeDecl.pIsArrayType(Libadalang.AdaNode.NONE))
            return "Polyglot.Ada.Arrays.Polyglot_Array";
        if (AdaTypeMatcher.isReturnedAsAddress(typeDecl)) return "System.Address";
        if (AdaTypeMatcher.isEnum(typeDecl)) return cInterfaceNativeTypename(NativeType.SINT32);

        throw new UnsupportedOperationException(typeDecl + " is not supported");
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
                .append("(")
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
    public String makeShadowOverride(Subprogram subp, String shadowTypename) {
        StringBuilder builder = new StringBuilder();
        builder.append(subp.isProcedure() ? "procedure " : "function ")
                .append(subp.getOriginName());
        builder.append("(");

        SubpParam firstParam = subp.parameters.get(0);
        Libadalang.BaseTypeDecl controllingType = firstParam.getType();

        builder.append(argName(firstParam.name)).append(" : ");
        if (firstParam.getMode() == SubpParam.Mode.INOUT) builder.append("in out ");
        else if (firstParam.getMode() == SubpParam.Mode.OUT) builder.append("out ");
        builder.append(shadowTypename);

        for (var param : subp.parameters.stream().skip(1).toList()) {
            builder.append("; ").append(argName(param.name)).append(" : ");
            if (param.getMode() == SubpParam.Mode.INOUT) builder.append("in out ");
            else if (param.getMode() == SubpParam.Mode.OUT) builder.append("out ");
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
        String typename =
                AdaTypeMatcher.isNonClassWideTagged(returnedType)
                        ? returnedType.pRelativeName().getText() + "_Shadow"
                        : returnedType.pFullyQualifiedName();
        String accessType =
                AdaTypeMatcher.isNonClassWideTagged(returnedType)
                        ? typename + "_Shadow"
                        : asAccess(returnedType);
        if (AdaTypeMatcher.isBindedAsClass(returnedType)
                || returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // When returning records, we need to convert an Address to an access`: declare a
            // converter.
            builder.append("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(typename)
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n");
            builder.append("function Converter is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(accessType)
                    .append(");\n")
                    // The returned value will be located on the heap and will be deallocated at
                    // some point.
                    .append("procedure Free is new Ada.Unchecked_Deallocation (")
                    .append(typename)
                    .append(", ")
                    .append(accessType)
                    .append(");\n");
            // The value will be received as an address or a Polyglot_Array. In order to be able to
            // free it, it needs to be stored in an access variable.
            builder.append("Returned_Access : ")
                    .append(accessType)
                    .append(" := ")
                    .append("Converter (Returned_Value");
            // Get the data of the Polyglot_Array or String when the return type is an array.
            if (returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) builder.append(".Data");
            builder.append(");");
        } else if (AdaTypeMatcher.isArrayAccess(returnedType)) {
            builder.append("type ")
                    .append(accessType)
                    .append(" is access all ")
                    .append(
                            returnedType
                                    .pAccessedType(Libadalang.AdaNode.NONE)
                                    .pFullyQualifiedName())
                    .append(" with Size => Standard'Address_Size")
                    .append(";\n");
            builder.append("function Converter is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(accessType)
                    .append(");\n")
                    .append("Returned_Access : ")
                    .append(accessType)
                    .append(" := ")
                    .append("Converter (Returned_Value.Data);\n");

        } else if (returnedType.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append("function Converter is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(typename)
                    .append(");\n");
        }

        return builder.toString();
    }

    /** Return the return statement of shadow dispatching functions. */
    public String makeShadowReturn(Subprogram subp) {
        StringBuilder builder = new StringBuilder();
        Libadalang.BaseTypeDecl returnedType = subp.getReturnType();
        String typename =
                AdaTypeMatcher.isNonClassWideTagged(returnedType)
                        ? returnedType.pRelativeName().getText() + "_Shadow"
                        : returnedType.pFullyQualifiedName();
        builder.append("return Result : ").append(typename).append(" := ");
        if (AdaTypeMatcher.isBindedAsClass(returnedType)
                || returnedType.pIsArrayType(Libadalang.AdaNode.NONE)) {
            // Values are returned on the heap from the target language. However, the parent of the
            // shadow function does not expect an acess or an address, but a value type instead. We
            // need to copy the returned value to the stack and free its heap counterpart.
            builder.append("Returned_Access.all")
                    .append(" do\n")
                    .append("Free (Returned_Access);\n")
                    .append("end return");
        } else if (AdaTypeMatcher.isArrayAccess(returnedType)) {
            builder.append("Returned_Access.all'Unchecked_Access");
        } else if (returnedType.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append("Converter (Returned_Value)");
        } else if (returnedType.pIsEnumType(Libadalang.AdaNode.NONE)) {
            builder.append(returnedType.pFullyQualifiedName()).append("'Enum_Val (Returned_Value)");
        } else {
            builder.append(returnedType.pFullyQualifiedName()).append(" (Returned_Value)");
        }

        return builder.toString();
    }

    /**
     * Return a string of the argument for param when calling an extern subprogram from a vtable.
     */
    public String makeShadowParamConversion(SubpParam param) {
        StringBuilder builder = new StringBuilder();

        BaseTypeDecl type = param.getType();
        NativeType nativeType = checkNativeType(type);
        builder.append(valueName(param.name)).append(" : ");
        String argName = argName(param.name);
        if (AdaTypeMatcher.isBindedAsClass(type)
                || (type.pIsScalarType(Libadalang.AdaNode.NONE) && param.isOutMode())) {
            builder.append(cInterfaceParamTypename(param))
                    .append(" := ")
                    .append(argName)
                    .append("'Address");
        } else if (type.pIsEnumType(Libadalang.AdaNode.NONE)) {
            builder.append(cInterfaceParamTypename(param))
                    .append(" := ")
                    .append(type.pFullyQualifiedName())
                    .append("'Enum_Rep (")
                    .append(argName)
                    .append(")");
        } else if (type.pIsScalarType(Libadalang.AdaNode.NONE)) {
            builder.append(cInterfaceParamTypename(param))
                    .append(" := ")
                    .append(cInterfaceNativeTypename(nativeType))
                    .append(" (")
                    .append(argName)
                    .append(")");
        } else if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            builder.append(cInterfaceParamTypename(param))
                    .append(" := ")
                    .append("(First => Interfaces.C.int (")
                    .append(argName)
                    .append("'First),")
                    .append(" Last => Interfaces.C.int (")
                    .append(argName)
                    .append("'Last),")
                    .append(" Data => ")
                    .append(argName)
                    .append("'Address)");
        } else if (AdaTypeMatcher.isArrayAccess(type)) {
            builder.append(cInterfaceTypename(type))
                    .append(" := ")
                    .append("(if ")
                    .append(type.pParentBasicDecl().pFullyQualifiedName())
                    .append(".\"=\" (")
                    .append(argName)
                    .append(", null) then (1, 0, System.Null_Address) else ")
                    .append("(First => Interfaces.C.int (")
                    .append(argName)
                    .append(".all'First),")
                    .append(" Last => Interfaces.C.int (")
                    .append(argName)
                    .append(".all'Last),")
                    .append(" Data => ")
                    .append(argName)
                    .append(".all'Address))");
        } else if (type.pIsAccessType(Libadalang.AdaNode.NONE)) {
            builder.append(cInterfaceParamTypename(param))
                    .append(" := ")
                    .append(argName)
                    .append(".all'Address");
        } else {
            throw new RuntimeException("Unsupported dispatch " + type);
        }

        return builder.toString();
    }

    public String makeDispatchedArgument(SubpParam param) {
        StringBuilder builder = new StringBuilder();

        builder.append(valueName(param.name));
        if (param.getType().pIsAccessType(Libadalang.AdaNode.NONE) && param.isOutMode()) {
            builder.append("'Address");
        }
        return builder.toString();
    }

    /**
     * Create a return statement that returns a dummy value for when an exception is thrown. This
     * value should never reach the user and is only used for the correctness of the generated code.
     */
    public String makeDefaultReturn(Libadalang.BaseTypeDecl returnType) {
        if (AdaTypeMatcher.isCharacter(returnType))
            return "return Interfaces.C.To_C ( Character'Val(0))";
        if (returnType.pIsFloatType(Libadalang.AdaNode.NONE)) return "return 0.0";
        if (returnType.pIsScalarType(Libadalang.AdaNode.NONE)) return "return 0";
        if (returnType.pIsArrayType(Libadalang.AdaNode.NONE)
                || AdaTypeMatcher.isArrayAccess(returnType))
            return " return(1, 0, System.Null_Address)";
        if (AdaTypeMatcher.isReturnedAsAddress(returnType)) return "return System.Null_Address";
        return "return (others => <>)";
    }

    public String makeDefaultGetterReturn(Libadalang.BaseTypeDecl returnType) {
        if (returnType.pIsArrayType(Libadalang.AdaNode.NONE)
                || AdaTypeMatcher.isArrayAccess(returnType))
            return "return (1, 0, System.Null_Address)";
        return "return System.Null_Address";
    }

    public String syncParamValue(SubpParam param) {
        StringBuilder builder = new StringBuilder();
        if (AdaTypeMatcher.isArrayAccess(param.getType()) && param.isOutMode()) {
            String polyglotArray = makeTemp(param.name, "Polyglot_Array");
            String valueArg = valueName(param.name);
            String eqFunction =
                    param.getType().pParentBasicDecl().pFullyQualifiedName() + ".\"=\"(";
            builder.append("declare\n")
                    .append(polyglotArray)
                    .append(" : ")
                    .append(
                            cInterfaceTypename(
                                    param.getType().pAccessedType(Libadalang.AdaNode.NONE)))
                    .append(" with Address => ")
                    .append(argName(param.name))
                    .append("; pragma Import(Ada, ")
                    .append(polyglotArray)
                    .append(");\n")
                    .append("begin\n")
                    .append(polyglotArray)
                    .append(".First := Interfaces.C.int (if ")
                    .append(eqFunction)
                    .append(valueArg)
                    .append(", null) then 0 else ")
                    .append(valueArg)
                    .append(".all'First);\n")
                    .append(polyglotArray)
                    .append(".Last := Interfaces.C.int (if ")
                    .append(eqFunction)
                    .append(valueArg)
                    .append(", null) then -1 else ")
                    .append(valueArg)
                    .append(".all'Last);\n")
                    .append(polyglotArray)
                    .append(".Data := (if ")
                    .append(eqFunction)
                    .append(valueArg)
                    .append(", null) then System.Null_Address else ")
                    .append(valueArg)
                    .append(".all'Address);\n")
                    .append("end;");
        }
        return builder.toString();
    }

    public String syncDispatchParamValue(SubpParam param) {
        StringBuilder builder = new StringBuilder();
        String valueName = valueName(param.name);
        String argName = argName(param.name);
        if (AdaTypeMatcher.isArrayAccess(param.getType()) && param.isOutMode()) {
            String fatPtr = makeTemp(param.name, "Fat_Pointer");
            String tmpAccess = makeTemp(param.name, "Tmp_Access");
            builder.append("declare\n")
                    .append(tmpAccess)
                    .append(" : ")
                    .append(param.getType().pFullyQualifiedName())
                    .append(";\n")
                    .append(fatPtr)
                    .append(" : Polyglot.Ada.Arrays.Fat_Pointer := (")
                    .append(valueName)
                    .append(".Data, System.Storage_Elements.\"-\"(")
                    .append(valueName)
                    .append(".Data, ")
                    .append(
                            param.getType()
                                    .pAccessedType(Libadalang.AdaNode.NONE)
                                    .pFullyQualifiedName())
                    .append("'Descriptor_Size / 8));\n")
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
                    .append("end;");
        } else if (param.getType().pIsAccessType(Libadalang.AdaNode.NONE) && param.isOutMode()) {
            String converter = makeTemp(param.name, "Converter");
            builder.append("declare\n")
                    .append("function ")
                    .append(converter)
                    .append(" is new Ada.Unchecked_Conversion (System.Address, ")
                    .append(param.getType().pFullyQualifiedName())
                    .append(");\n")
                    .append("begin\n")
                    .append(argName)
                    .append(" := ")
                    .append(converter)
                    .append(" (")
                    .append(valueName)
                    .append(");\n")
                    .append("end;");
        }
        return builder.toString();
    }
}
