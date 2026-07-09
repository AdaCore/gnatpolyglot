//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.ada2proxy.codegen.AdaGenerator;
import com.adacore.gnatpolyglot.ada2proxy.codegen.GetterReturnConverter;
import com.adacore.gnatpolyglot.ada2proxy.codegen.ParamConverter;
import com.adacore.gnatpolyglot.ada2proxy.codegen.ParamUpdater;
import com.adacore.gnatpolyglot.ada2proxy.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.ada2proxy.codegen.ShadowParamConverter;
import com.adacore.gnatpolyglot.ada2proxy.codegen.ShadowReturnConverter;
import com.adacore.gnatpolyglot.ada2proxy.proxy.AdaDeclaration;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Array;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Component;
import com.adacore.gnatpolyglot.ada2proxy.proxy.EnumType;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Package;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Record;
import com.adacore.gnatpolyglot.ada2proxy.proxy.SubpParam;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Subprogram;
import com.adacore.gnatpolyglot.ada2proxy.proxy.Subtype;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI extends LanguageAPI {

    private Name projectName;

    private final BindableDeclChecker declChecker = new BindableDeclChecker();

    private ParamConverter paramConverter = new ParamConverter(this);

    private ReturnConverter returnConverter = new ReturnConverter(this);

    private GetterReturnConverter getterReturnConverter = new GetterReturnConverter(this);

    private ShadowParamConverter shadowParamConverter = new ShadowParamConverter(this);

    private ShadowReturnConverter shadowReturnConverter = new ShadowReturnConverter(this);

    private ParamUpdater paramUpdater = new ParamUpdater(this);

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
                return makeTypeExpr(typeDecl.pCompType(false, decl)).makeArray();
            } else if (typeDecl.pIsAccessType(Libadalang.AdaNode.NONE)) {
                Libadalang.TypeDecl rootType = (Libadalang.TypeDecl) typeDecl.pRootType(decl);
                Libadalang.AccessDef accessDef = (Libadalang.AccessDef) rootType.fTypeDef();
                boolean hasNonNull = accessDef.fHasNotNull().pAsBool();
                boolean hasConst = false;
                if (rootType.fTypeDef() instanceof Libadalang.TypeAccessDef access)
                    hasConst = access.fHasConstant().pAsBool();
                return makeTypeExpr(typeDecl.pAccessedType(decl)).makePointer(hasNonNull, hasConst);
            }
        }
        return makeProxyFullyQualifiedName(decl).asTypeExpr();
    }

    /** Return the native type corresponding to bTypeDecl, or null if the type is not native. */
    public static NativeType checkNativeType(Libadalang.BaseTypeDecl baseTypeDecl) {
        if (baseTypeDecl.isNone()) return NativeType.VOID;

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
        if (baseTypeDecl.pBaseSubtype(baseTypeDecl).equals(baseTypeDecl.pBoolType()))
            return NativeType.BOOL;

        baseTypeDecl = baseTypeDecl.pRootType(baseTypeDecl);

        if (baseTypeDecl instanceof Libadalang.TypeDecl typeDecl) {
            if (AdaTypeMatcher.isStringType(typeDecl)) return NativeType.STRING;
            if (AdaTypeMatcher.isCharacter(typeDecl)) return NativeType.CHAR;
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
                        int bitLength =
                                range.highBound.pEvalAsInt().subtract(BigInteger.ONE).bitLength();
                        if (bitLength <= 8) return NativeType.UINT8;
                        if (bitLength <= 16) return NativeType.UINT16;
                        if (bitLength <= 32) return NativeType.UINT32;
                        if (bitLength <= 64) return NativeType.UINT64;
                        if (bitLength <= 128) return NativeType.UINT128;
                        throw new UnbindableDeclException(
                                typeDecl, "Unsupported integer size (%s)".formatted(bitLength));
                    }
                } catch (Libadalang.LangkitException e) {
                    throw new UnbindableDeclException(baseTypeDecl, e.getLocalizedMessage());
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
                    throw new UnbindableDeclException(baseTypeDecl, e.getLocalizedMessage());
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

    /** Safe method to get the documentation of a BasicDecl */
    public static String getDoc(Libadalang.BasicDecl decl) {
        try {
            return decl.pDoc();
        } catch (Libadalang.LangkitException e) {
            return "";
        }
    }

    /** Return whether the declaration requires to write in a package body during codegen. */
    public static boolean requiresBodyPackage(AdaDeclaration decl) {
        return !(decl instanceof Array || decl instanceof Subtype || decl instanceof EnumType);
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

    public static String getDisplayName(Libadalang.BasicDecl decl) {
        if (decl instanceof Libadalang.AnonymousTypeDecl anon) return anon.getImage();
        if (decl.pDefiningNames().length > 1) return decl.getImage();
        return decl.pFullyQualifiedName();
    }

    public String getProxyAccessFullyQualifiedName(Libadalang.BaseTypeDecl type) {
        if (type.equals(type.pStdStringType())) return "GNATpolyglot.Ada.Strings.String_Access";
        Libadalang.BasePackageDecl pack = (Libadalang.BasePackageDecl) type.pParentBasicDecl();
        if (Package.isAdaRuntimePackage(pack))
            return Package.getProxyUnitName(pack).concat(".").concat(asAccess(type));
        return Package.getProxyUnitName(pack).concat(".").concat(asAccess(type));
    }

    /** Return the typename of the parameter */
    public String cInterfaceParamTypename(SubpParam p) {
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
                    .append(
                            "Self_Data_Arg: System.Address; Self_Arg : System.Address; Vtable_Arg :"
                                    + " System.Address");
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
    public String makeParamConversion(
            Name name, Libadalang.BaseTypeDecl type, boolean isOutMode, boolean isAliased) {
        return paramConverter.build(name, type, isOutMode, isAliased);
    }

    /**
     * Create an entity that is the conversion of a C interface type parameter that represent the
     * new value of the setter for ``component`` into the actual Ada type.
     */
    public String makeParamConversion(Component component) {
        return makeParamConversion(component.name, component.getType(), false, false);
    }

    /**
     * Create an entity that is the conversion from a C interface type parameter into the actual Ada
     * type.
     */
    public String makeParamConversion(SubpParam param) {
        return makeParamConversion(
                param.name, param.getType(), param.isOutMode(), param.isAliased());
    }

    /** Create a string to call a function from the proxy. */
    public String call(Subprogram subp) {
        // (eng/toolchain/gnat#1918): Avoid calling the "/=" subpgram. It may be rejected by GNAT.
        // Instead, call the "=" operator and negate the result.
        String subpFQN = subp.getOriginFullyQualifiedName();
        Libadalang.BaseTypeDecl returnType = subp.getReturnType();
        boolean isImplicitNeq =
                subpFQN.endsWith("\"/=\"") && returnType.equals(returnType.pBoolType());
        if (isImplicitNeq) subpFQN = subpFQN.replace("/=", "=");
        String call =
                AdaGenerator.makeCall(
                                subpFQN,
                                subp.parameters.stream().map(p -> getParamForCall(p)).toList())
                        .toString();
        if (isImplicitNeq) return "not " + call;
        return call;
    }

    public String getParamForCall(Component component) {
        BaseTypeDecl type = component.getType();
        String valueName = valueName(component.name);
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            return AdaGenerator.makeCast(type, valueName).toString();
        }
        return valueName;
    }

    private String getParamForCall(SubpParam param) {
        BaseTypeDecl type = param.getType();
        String valueName = valueName(param.name);
        if (type.pIsArrayType(Libadalang.AdaNode.NONE)) {
            return AdaGenerator.makeCast(type, valueName)
                    .append("'Unrestricted_Access.all")
                    .toString();
        }
        return valueName;
    }

    /** Create the necessary declarations for the return statement. */
    public String makeReturnDeclarations(Libadalang.BaseTypeDecl returnedType) {
        return returnConverter.prepareReturn(returnedType);
    }

    /**
     * Create a string of the value returned by functions, with the necessary cast to the C
     * interface type.
     */
    public String makeReturnConversion(Libadalang.BaseTypeDecl returnedType, String returnedValue) {
        return returnConverter.buildReturn(returnedType, returnedValue);
    }

    /** Return the type that must be used when returning from a getter. */
    public String getterReturnTypename(Libadalang.BaseTypeDecl type) {
        if (AdaTypeMatcher.isArrayAccess(type))
            return cInterfaceTypename(type.pAccessedType(Libadalang.AdaNode.NONE));
        if (type.pIsArrayType(Libadalang.AdaNode.NONE))
            return "GNATpolyglot.Ada.Arrays.Polyglot_Array";
        return "System.Address";
    }

    /** Create a string of the return statement for value returned by component getter functions. */
    public String makeGetterReturnConversion(String componentAccess, Libadalang.BaseTypeDecl type) {
        return getterReturnConverter.build(type, componentAccess);
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
                return "Interfaces.Integer_32";
            case FLOAT128:
                return "Interfaces.C.long_double";
            case FLOAT32:
                return "Interfaces.C.C_float";
            case FLOAT64:
                return "Interfaces.C.double";
            case UINT8:
            case SINT8:
                return "Interfaces.Integer_8";
            case UINT16:
            case SINT16:
                return "Interfaces.Integer_16";
            case UINT32:
            case SINT32:
                return "Interfaces.Integer_32";
            case UINT64:
            case SINT64:
                return "Interfaces.Integer_64";
            case UINT128:
            case SINT128:
                return "Interfaces.Integer_128";
            case CHAR:
                return "Interfaces.C.char";
            case STRING:
                return "GNATpolyglot.Ada.Strings.Polyglot_String";
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
            return cInterfaceTypename(typeDecl.pAccessedType(typeDecl));
        if (typeDecl.pIsArrayType(Libadalang.AdaNode.NONE))
            return "GNATpolyglot.Ada.Arrays.Polyglot_Array";
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
                .append("(Self_Data: System.Address; ")
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
        return shadowReturnConverter.prepareReturn(returnedType);
    }

    /** Return the return statement of shadow dispatching functions. */
    public String makeShadowReturn(Subprogram subp) {
        return shadowReturnConverter.buildReturn(subp);
    }

    /**
     * Return a string of the argument for param when calling an extern subprogram from a vtable.
     */
    public String makeShadowParamConversion(SubpParam param) {
        return shadowParamConverter.build(param);
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
        if (returnType.pIsFloatType(returnType)) return "return 0.0";
        if (returnType.pIsScalarType(returnType)) return "return 0";
        if (returnType.pIsArrayType(returnType) || AdaTypeMatcher.isArrayAccess(returnType))
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
        return paramUpdater.build(param);
    }

    public String syncDispatchParamValue(SubpParam param) {
        return paramUpdater.buildDispatch(param);
    }
}
