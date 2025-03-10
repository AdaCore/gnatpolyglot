package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.ProxyContext;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import java.nio.file.Path;
import java.util.function.Function;

public class CppAPI {

    /** Context of the proxy. */
    private ProxyContext context;

    /** Output directory */
    private Path outputPath;

    /** Directory in which the headers will be written. */
    private Path headerDir;

    public CppAPI(ProxyContext context, Path outputPath) {
        this.headerDir = outputPath.resolve("include");
        this.outputPath = outputPath;
        this.context = context;
    }

    public String formatDoc(String doc, int indent) {
        StringBuilder builder = new StringBuilder();
        builder.append("/** ");
        if (!doc.contains("\n")) builder.append(doc);
        else {
            builder.append("\n");
            for (var line : doc.split("\n")) {
                builder.append("    ".repeat(indent)).append(" * ").append(line).append("\n");
            }
            builder.append("    ".repeat(indent));
        }
        builder.append(" */");
        return builder.toString();
    }

    /** Return the correct syntax for the name of this refernce, without the suffix. */
    public String toCppName(Reference reference) {
        switch (reference.kind) {
            case CLASS:
                return reference.name.toPascal();
            case MODULE:
                return reference.name.toLower();
            default:
                throw new UnsupportedOperationException("Unsupported reference kind");
        }
    }

    /** Return the C++ name of a native type. */
    public String nativeTypeName(NativeType nativeType) {
        switch (nativeType) {
            case BOOL:
                return "bool";
            case FLOAT128:
                return "long double";
            case FLOAT32:
                return "float";
            case FLOAT64:
                return "double";
            case UINT8:
                return "uint8_t";
            case SINT8:
                return "int8_t";
            case UINT16:
                return "uint16_t";
            case SINT16:
                return "int16_t";
            case UINT32:
                return "uint32_t";
            case SINT32:
                return "int32_t";
            case UINT64:
                return "uint64_t";
            case SINT64:
                return "int64_t";
            case UINT128:
                return "uint128_t";
            case SINT128:
                return "int128_t";
            case STRING:
                return "std::string";
            case VOID:
                return "void";
            default:
                throw new UnsupportedOperationException(
                        nativeType.toString() + " is not handled yet");
        }
    }

    /** Return the refered C++ type's name. */
    public String cppTypename(Reference reference) {
        if (reference.kind == ReferenceKind.SCALAR)
            return nativeTypeName(NativeType.valueOf(reference.name.toLower().toUpperCase()));
        throw new UnsupportedOperationException("Unsupported Cpp type");
    }

    /** Return the refered C type's name. */
    public String cTypename(Reference reference) {
        if (reference.kind == ReferenceKind.SCALAR)
            return nativeTypeName(NativeType.valueOf(reference.name.toLower().toUpperCase()));
        throw new UnsupportedOperationException("Unsupported C type");
    }

    /**
     * Join all the reference names with a prefix, suffix and separator, using a function to convert
     * the names to strings.
     */
    public String makeRefString(
            Reference ref,
            Function<Reference, String> converter,
            String prefix,
            String separator,
            String suffix) {
        StringBuilder builder = new StringBuilder(prefix);
        while (ref != null) {
            if (!builder.isEmpty()) builder.append(separator);
            builder.append(converter.apply(ref));
            ref = ref.suffix;
        }
        builder.append(suffix);
        return builder.toString();
    }

    /** Create the string of the C++ namespace of the corresponding module. */
    public String namespacePath(Module module) {
        return makeRefString(context.getReference(module), r -> toCppName(r), "", "::", "");
    }

    /** Return the path to the source file of the corresponing module. */
    public Path sourceFilePath(Module module) {
        return outputPath.resolve(
                makeRefString(
                        context.getReference(module), r -> r.name.toLower(), "", "_", ".cpp"));
    }

    /** Return the path to the header file of the corresponing module. */
    public Path headerFilePath(Module module) {
        return headerDir.resolve(
                makeRefString(context.getReference(module), r -> r.name.toLower(), "", "_", ".h"));
    }

    /** Create a string for the name of the header guard macro. */
    public String headerGuard(Module module) {
        return makeRefString(
                context.getReference(module), r -> r.name.toLower().toUpperCase(), "", "_", "_H");
    }

    /** Create a call to the C symbol of the funtion. */
    public String callCSymbol(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(functionDecl.symbol);
        builder.append("()");
        return builder.toString();
    }
}
