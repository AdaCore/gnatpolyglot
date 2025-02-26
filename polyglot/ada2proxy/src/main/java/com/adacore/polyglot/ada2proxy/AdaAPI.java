package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI {

    /**
     * Build the parent reference to ``decl``, with ``suffix`` as the last suffix of the chain of
     * references.
     */
    private static Reference makeParentReferences(Libadalang.BasicDecl decl, Reference suffix) {
        Libadalang.Symbol[] names = decl.pFullyQualifiedNameArray(false);
        Reference res = suffix;
        for (int i = names.length - 2; i >= 0; i--) {
            res =
                    new Reference(
                            Name.fromLower(names[i].text),
                            Reference.ReferenceKind.MODULE,
                            res,
                            false,
                            false,
                            false);
        }
        return res;
    }

    /** Create a reference to decl, or its parent if ``onlyParent`` is true. */
    public static Reference makeReferenceTo(Libadalang.BasicDecl decl, Boolean onlyParent) {
        Reference res = null;
        if (!onlyParent) {
            Reference.ReferenceKind kind = null;
            if (decl instanceof Libadalang.BaseTypeDecl) kind = Reference.ReferenceKind.CLASS;
            else if (decl instanceof Libadalang.PackageDecl) kind = Reference.ReferenceKind.MODULE;
            else
                throw new UnsupportedOperationException(
                        "Creating reference to an unsupported declaration type");
            res =
                    new Reference(
                            Name.fromPascalWithUnderscore(decl.pDefiningName().getText()),
                            kind,
                            res,
                            false,
                            false,
                            false);
        }
        return makeParentReferences(decl, res);
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

        Libadalang.BaseTypeDecl typeDecl = typeExpr.pDesignatedTypeDecl();

        NativeType nativeType = checkNativeType(typeDecl);
        if (nativeType != null) return nativeType.reference;

        return makeReferenceTo(typeDecl, false);
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
                            argBuilder.append(p.name.toPascalWithUnderscore());
                            argBuilder.append(": ");
                            argBuilder.append(cInterfaceTypename(p.getFormalType()));
                            return argBuilder.toString();
                        }))
                .collect(Collectors.joining("; "));
    }

    /** Create a string to call a function from the proxy. */
    public static String call(Subprogram funDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(funDecl.name.toPascalWithUnderscore());
        if (!funDecl.parameters.isEmpty()) {
            builder.append(" (");
            builder.append(
                    funDecl.parameters.stream()
                            .map(
                                    p -> {
                                        // Cast C Interface types to Ada types.
                                        StringBuilder argBuilder = new StringBuilder();
                                        argBuilder.append(p.getTypeExpr().getText());
                                        argBuilder.append(" (");
                                        argBuilder.append(p.name.toPascalWithUnderscore());
                                        argBuilder.append(")");
                                        return argBuilder.toString();
                                    })
                            .collect(Collectors.joining(", ")));
            builder.append(")");
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
        bTypeDecl = (Libadalang.TypeDecl) bTypeDecl.pRootType(Libadalang.AdaNode.NONE);
        NativeType nativeType = checkNativeType(bTypeDecl);
        if (nativeType != null) return cInterfaceNativeTypename(nativeType);

        throw new UnsupportedOperationException("Type not supported");
    }
}
