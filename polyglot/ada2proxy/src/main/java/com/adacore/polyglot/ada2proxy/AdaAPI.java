package com.adacore.polyglot.ada2proxy;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI {

    /** Return the fully qualified name of a name with its parents. */
    public static String makeFullyQualifiedName(Reference parent, Name name) {
        StringBuilder builder = new StringBuilder();
        while (parent != null) {
            builder.append(parent.name.toPascalWithUnderscore());
            builder.append(".");
            parent = parent.suffix;
        }
        builder.append(name.toPascalWithUnderscore());
        return builder.toString();
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file.
     */
    public static String makeInterfaces(List<Module> modules) {
        return modules.stream()
                .map(
                        m -> {
                            return "\"%s.proxy\""
                                    .formatted(
                                            makeFullyQualifiedName(m.parent, m.name).toLowerCase());
                        })
                .collect(Collectors.joining(", "));
    }

    /** Return name with the correct Ada syntax with ``_Proxy`` as a suffix. */
    public static String proxyName(Name name) {
        return name.toPascalWithUnderscore() + "_Proxy";
    }

    /** Build a string containing the parameter specifications of a subprogram. */
    public static String cInterfaceParameters(FunctionDecl functionDecl) {
        return functionDecl.parameters.stream()
                .map(
                        (p -> {
                            StringBuilder argBuilder = new StringBuilder();
                            argBuilder.append(p.name.toPascalWithUnderscore());
                            argBuilder.append(": ");
                            argBuilder.append(cInterfaceTypename(p.type));
                            return argBuilder.toString();
                        }))
                .collect(Collectors.joining("; "));
    }

    /** Create a string to call a function from the proxy. */
    public static String call(FunctionDecl funDecl) {
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
                                        argBuilder.append(typename(p.type));
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
    public static Path toAdaFilename(Module module, String suffix) {
        StringBuilder builder = new StringBuilder();
        Reference parent = module.parent;
        while (parent != null) {
            builder.append(parent.name.toLower());
            builder.append("-");
        }
        builder.append(module.name.toLower());
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
    public static String cInterfaceTypename(Reference reference) {
        if (reference.kind == ReferenceKind.SCALAR)
            return cInterfaceNativeTypename(
                    NativeType.valueOf(reference.name.toLower().toUpperCase()));
        throw new UnsupportedOperationException("Only native types are supported");
    }

    /** Return the Ada name of a native type. */
    public static String nativeTypeName(NativeType nativeType) {
        switch (nativeType) {
            case BOOL:
                return "Boolean";
            case FLOAT128:
                return "Long_Long_Float";
            case FLOAT32:
                return "Float";
            case FLOAT64:
                return "Long_Float";
            case UINT8:
            case SINT8:
                return "Short_Short_Integer";
            case UINT16:
            case SINT16:
                return "Long_Integer";
            case UINT32:
            case SINT32:
                return "Integer";
            case UINT64:
            case SINT64:
                return "Short_Integer";
            case UINT128:
            case SINT128:
                return "Long_Long_Integer";
            case STRING:
                return "String";
            case VOID:
                throw new IllegalArgumentException("Ada has no ``void`` type.");
            default:
                throw new UnsupportedOperationException(
                        nativeType.toString() + " is not handled yet");
        }
    }

    /** Return a string containing the Ada typename of the referenced type. */
    public static String typename(Reference reference) {
        if (reference.kind == ReferenceKind.SCALAR)
            return nativeTypeName(NativeType.valueOf(reference.name.toLower().toUpperCase()));
        throw new UnsupportedOperationException("Only native types are supported");
    }

    /** Return whether ``funDecl`` is a procedure or a function. */
    public static boolean isProcedure(FunctionDecl functionDecl) {
        return functionDecl.returnType.equals(NativeType.VOID.reference);
    }
}
