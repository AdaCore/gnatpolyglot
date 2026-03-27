package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.codegen.CGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.JavaGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.ParameterConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.TypenameGenerator;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class JavaAPI extends LanguageAPI {

    private FullyQualifiedName groupId;

    private Name projectName;

    private ProxyContext context;

    private TypenameGenerator typenameGenerator = new TypenameGenerator(this);

    private ReturnConverter returnConverter = new ReturnConverter(this);

    private ParameterConverter parameterConverter = new ParameterConverter(this);

    public JavaAPI(ProxyContext context, List<String> groupId, Name projectName) {
        this.context = context;
        this.groupId = new FullyQualifiedName(groupId.stream().map(Name::fromLower).toList());
        this.projectName = projectName;
    }

    public ProxyContext getContext() {
        return context;
    }

    /** Return the string of the base package ({groupId}.lib{projectName}). */
    public String basePackage() {
        return groupId.append(Name.fromLower("lib".concat(projectName.toLower())))
                .join((n) -> n.getLastName().toLower(), "", ".", "");
    }

    /** Return the string of the package for the given module. */
    public String packagePath(Module module) {
        return groupId.append(Name.fromLower("lib".concat(projectName.toLower())))
                .append(module.name)
                .join((n) -> n.getLastName().toLower(), "", ".", "");
    }

    /** Return the path of the file to generate for a module. */
    public Path filepath(Module module) {
        Path p = Path.of(".");
        for (var n : module.name.names) {
            p = p.resolve(n.toLower());
        }
        return p.resolve(module.name.getLastName().toPascal().concat("Package.java"));
    }

    /** Return the name of a java argument. */
    public String javaArgName(Name name) {
        return name.toCamel();
    }

    /** Return the expected name of a converted java argument. */
    public String javaValueName(Name name) {
        return "val$".concat(name.toCamel());
    }

    /** Return the name of the argument in the JNI layer. */
    public String jniArgName(Name name) {
        return name.concat(Name.fromLower("arg")).toLower();
    }

    /** Return the expected name of a converted JNI argument. */
    public String jniValueName(Name name) {
        return name.concat(Name.fromLower("value")).toLower();
    }

    /** Return the name of the primitive type in Java. */
    public String javaPrimitiveTypename(NativeType type) {
        return switch (type) {
            case BOOL -> "boolean";
            case CHAR -> "char";
            case FLOAT32 -> "float";
            case FLOAT64 -> "double";
            case UINT8, SINT8 -> "byte";
            case UINT16, SINT16 -> "short";
            case UINT32, SINT32 -> "int";
            case UINT64, SINT64 -> "long";
            case VOID -> "void";
            case STRING -> throw new UnsupportedOperationException(
                    "String types are not yet supported");
            default -> throw new UnsupportedOperationException("Unsupported native type");
        };
    }

    /** Return the name of the primitive type in JNI. */
    public String jniPrimitiveTypename(NativeType type) {
        return switch (type) {
            case BOOL -> "jboolean";
            case CHAR -> "jchar";
            case FLOAT32 -> "jfloat";
            case FLOAT64 -> "jdouble";
            case UINT8, SINT8 -> "jbyte";
            case UINT16, SINT16 -> "jshort";
            case UINT32, SINT32 -> "jint";
            case UINT64, SINT64 -> "jlong";
            case VOID -> "void";
            case STRING -> throw new UnsupportedOperationException(
                    "String types are not yet supported");
            default -> throw new UnsupportedOperationException("Unsupported native type");
        };
    }

    /** Return the name of the primitive type in C. */
    public String cPrimitiveTypename(NativeType type) {
        return switch (type) {
            case BOOL -> "int";
            case CHAR -> "char";
            case FLOAT32 -> "float";
            case FLOAT64 -> "double";
            case UINT8 -> "unsigned char";
            case SINT8 -> "signed char";
            case UINT16 -> "unsigned short";
            case SINT16 -> "short";
            case UINT32 -> "unsigned int";
            case SINT32 -> "int";
            case UINT64 -> "unsigned long";
            case SINT64 -> "long";
            case VOID -> "void";
            case STRING -> throw new UnsupportedOperationException(
                    "String types are not yet supported");
            default -> throw new UnsupportedOperationException("Unsupported native type");
        };
    }

    /** Return the Java name of a type (ie. the type exposed to the user). */
    public String javaTypename(TypeExpr type) {
        return typenameGenerator.javaTypename(type);
    }

    /** Return the native Java name of a type. */
    public String javaNativeTypename(TypeExpr type) {
        return typenameGenerator.javaNativeTypename(type);
    }

    /** Return the JNI name of a type. */
    public String jniTypename(TypeExpr type) {
        return typenameGenerator.jniTypename(type);
    }

    /** Return the C name of a type. */
    public String cTypename(TypeExpr type) {
        return typenameGenerator.cTypename(type);
    }

    /** Return the name of the function to implement in the JNI layer. */
    public String jniName(FunctionDecl function) {
        StringBuilder builder = new StringBuilder("Java_");
        if (function.role == null) {
            FullyQualifiedName parent = function.name.getParentFullyQualifiedName();
            builder.append(groupId.join((n) -> n.getLastName().toLower(), "", "_", ""))
                    .append("_lib")
                    .append(projectName.toLower())
                    .append("_")
                    .append(
                            parent.join(
                                    (n) -> n.getLastName().toLower(),
                                    "",
                                    "_",
                                    "_".concat(parent.getLastName().toPascal().concat("Package"))));
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
        // All native function handle names start with `$` (Unicode character: 00024)
        return builder.append("__00024").append(function.name.getLastName().toCamel()).toString();
    }

    /** Create a call to the C symbol in the JNI layer. */
    public String callCSymbol(FunctionDecl functionDecl) {
        return CGenerator.makeCall(
                        functionDecl.symbol,
                        functionDecl.type.parameters.stream()
                                .map(p -> jniValueName(p.name))
                                .toList())
                .toString();
    }

    /** Create a call to the Java native function. */
    public String callJavaNative(FunctionDecl functionDecl, String nativeName) {
        return JavaGenerator.makeCall(
                        nativeName,
                        functionDecl.type.parameters.stream()
                                .map(p -> javaValueName(p.name))
                                .toList())
                .toString();
    }

    /** Return whether a function returns void. */
    public boolean returnsVoid(FunctionTypeExpr functionType) {
        return functionType.returnType.isName()
                && context.getTypeDecl(functionType.returnType.getName())
                        .equals(NativeType.VOID.declaration);
    }

    /** Create the return statement for the function in its JNI layer implementation */
    public String makeCReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.cReturnStatement(functionDecl, returnedValue);
    }

    /** Create the return statement for the function in its Java layer implementation */
    public String makeJavaReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.javaReturnStatement(functionDecl, returnedValue);
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .map(p -> "%s %s".formatted(javaTypename(p.type), javaArgName(p.name)))
                .collect(Collectors.joining(", "));
    }

    /** Return the string to declare arguments in the native Java function. */
    public String javaNativeParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .map(p -> "%s %s".formatted(javaNativeTypename(p.type), javaArgName(p.name)))
                .collect(Collectors.joining(", "));
    }

    /** Return the string to declare arguments in the JNI function. */
    public String jniParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder("JNIEnv *env, jobject obj");
        if (!functionDecl.type.parameters.isEmpty()) {
            builder.append(", ")
                    .append(
                            functionDecl.type.parameters.stream()
                                    .map(
                                            p ->
                                                    "%s %s"
                                                            .formatted(
                                                                    jniTypename(p.type),
                                                                    jniArgName(p.name)))
                                    .collect(Collectors.joining(", ")));
        }
        return builder.toString();
    }

    /** Return the string to declare arguments in the C function. */
    public String cParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .map(p -> "%s %s".formatted(cTypename(p.type), p.name.toCamel()))
                .collect(Collectors.joining(", "));
    }

    /** Create the string to convert the java parameter for calling the native handle. */
    public String makeJavaParamConversion(Parameter param) {
        return parameterConverter.javaParam(param);
    }

    /** Create the string to convert the JNI parameter for calling the C symbol. */
    public String makeJNIParamConversion(Parameter param) {
        return parameterConverter.jniParam(param);
    }
}
