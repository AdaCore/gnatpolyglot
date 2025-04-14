package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.NativeType.NativeTypeDecl;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.ProxyContext;
import com.adacore.polyglot.proxy.ReferenceTypeExpr;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.TypeDecl;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public String cppTypename(FullyQualifiedName name) {
        TypeDecl typeDecl = context.getTypeDecl(name);
        if (typeDecl instanceof NativeTypeDecl nativeType)
            return nativeTypeName(nativeType.nativeType);
        if (typeDecl instanceof ClassDecl)
            return makeRefString(name, fqn -> lastNameToCppName(fqn), "", "::", "");
        throw new UnsupportedOperationException("Unsupported Cpp type");
    }

    private String lastNameToCppName(FullyQualifiedName fqn) {
        if (context.getTypeDecl(fqn) != null) return fqn.getLastName().toPascal();
        return fqn.getLastName().toLower();
    }

    /** Return the refered C type's name. */
    public String cTypename(FullyQualifiedName name) {
        TypeDecl typeDecl = context.getTypeDecl(name);
        if (typeDecl instanceof NativeTypeDecl nativeType)
            return nativeTypeName(nativeType.nativeType);
        if (typeDecl instanceof ClassDecl) return "void *";
        throw new UnsupportedOperationException("Unsupported C type");
    }

    /**
     * Join all the names with a prefix, suffix and separator, using a function to convert the names
     * to strings. The converter is run on every sub-FullyQualifiedName and should return a
     * conversion of the last name only.
     */
    public String makeRefString(
            FullyQualifiedName ref,
            Function<FullyQualifiedName, String> converter,
            String prefix,
            String separator,
            String suffix) {
        StringBuilder builder = new StringBuilder(prefix);
        FullyQualifiedName current = new FullyQualifiedName(ref.names.subList(0, 1));
        builder.append(converter.apply(current));
        for (int i = 2; i <= ref.names.size(); i++) {
            current = new FullyQualifiedName(ref.names.subList(0, i));
            builder.append(separator).append(converter.apply(current));
        }
        return builder.append(suffix).toString();
    }

    /** Create the string of the C++ namespace of the corresponding module. */
    public String namespacePath(Module module) {
        return makeRefString(module.name, r -> r.getLastName().toLower(), "", "::", "");
    }

    /** Return the path to the source file of the corresponing module. */
    public Path sourceFilePath(Module module) {
        return outputPath.resolve(
                makeRefString(module.name, r -> r.getLastName().toLower(), "", "_", ".cpp"));
    }

    /** Return the path to the header file of the corresponing module. */
    public Path headerFilePath(Module module) {
        return headerDir.resolve(
                makeRefString(module.name, r -> r.getLastName().toLower(), "", "_", ".h"));
    }

    /** Create a string for the name of the header guard macro. */
    public String headerGuard(Module module) {
        return makeRefString(
                module.name, r -> r.getLastName().toLower().toUpperCase(), "", "_", "_H");
    }

    /** Create a string corresponding to the C parameter. */
    private String toCParam(Parameter parameter) {
        StringBuilder builder = new StringBuilder();
        builder.append(cTypename(parameter.type.getName()))
                .append(" ")
                .append(parameter.name.toLower());
        return builder.toString();
    }

    /** Create a string corresponding to the C++ parameter. */
    private String toCppParam(Parameter parameter) {
        StringBuilder builder = new StringBuilder();
        if (parameter.type.isConst()) builder.append("const ");
        builder.append(cppTypename(parameter.type.getName())).append(" ");
        if (parameter.type instanceof ReferenceTypeExpr) builder.append("&");
        builder.append(parameter.name.toLower());
        return builder.toString();
    }

    /** Create a string of all the C parameters of the function's symbol. */
    public String cParameters(FunctionDecl functionDecl) {
        return functionDecl.parameters.stream()
                .map(p -> toCParam(p))
                .collect(Collectors.joining(", "));
    }

    public boolean isMethod(FunctionDecl functionDecl) {
        // A method can have the role ``METHOD``, ``GETTER`` or ``SETTER``.
        return functionDecl.role != null && functionDecl.role.kind.compareTo(RoleKind.SETTER) <= 0;
    }

    /** Return the const keyword if the function is a const method, else return an empty string. */
    public String getConstMethod(FunctionDecl functionDecl) {
        if (isMethod(functionDecl) && functionDecl.parameters.get(0).type.isConst()) return "const";
        return "";
    }

    /** Create a string of all the C++ parameters of the function. */
    public String cppParameters(FunctionDecl functionDecl) {
        return functionDecl.parameters.stream()
                .skip(isMethod(functionDecl) ? 1 : 0)
                .map(p -> toCppParam(p))
                .collect(Collectors.joining(", "));
    }

    /** Create a call to the C symbol of the funtion. */
    public String callCSymbol(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        boolean funcIsMethod = isMethod(functionDecl);
        builder.append(functionDecl.symbol).append("(");
        // If the function is attached to a type, use ``this->data`` as the first argument.
        if (funcIsMethod) {
            builder.append("this->_data");
            if (functionDecl.parameters.size() > 1) builder.append(", ");
        }
        builder.append(
                functionDecl.parameters.stream()
                        .skip(funcIsMethod ? 1 : 0)
                        .map(
                                p -> {
                                    if (context.getTypeDecl(p.type.getName()) instanceof ClassDecl)
                                        return p.name.toLower() + ".data()";
                                    else return p.name.toLower();
                                })
                        .collect(Collectors.joining(", ")));
        builder.append(")");
        return builder.toString();
    }

    /** Return the C++ name of the function to define ``functionDecl``. */
    public String functionDefinitionName(FunctionDecl functionDecl) {
        String functionName = functionDecl.getLastName().toLower();
        if (!isMethod(functionDecl)) return functionName;
        return makeRefString(
                functionDecl.role.type, r -> lastNameToCppName(r), "", "::", "::" + functionName);
    }

    /** Return the member functions of a type. */
    public ProxyContext.FunctionMembersEntry getMembers(TypeDecl decl) {
        return context.getMembers(decl);
    }
}
