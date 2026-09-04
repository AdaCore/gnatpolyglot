//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl.Inheritability;
import com.adacore.gnatpolyglot.proxy.Declaration;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.Transfer.RequiredOwner;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.codegen.CGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.DocumentationFormater;
import com.adacore.gnatpolyglot.proxy2java.codegen.JNITypeSignatureGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.JavaGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.ParameterConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.TypenameGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.UpcallParameterConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.UpcallReturnConverter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaAPI extends LanguageAPI {

    private FullyQualifiedName groupId;

    private Name projectName;

    private ProxyContext context;

    private TypenameGenerator typenameGenerator = new TypenameGenerator(this);

    private JNITypeSignatureGenerator jniTypeSignatureGenerator =
            new JNITypeSignatureGenerator(this);

    private ReturnConverter returnConverter = new ReturnConverter(this);

    private UpcallReturnConverter dispatchReturnConverter = new UpcallReturnConverter(this);

    private ParameterConverter parameterConverter = new ParameterConverter(this);

    private UpcallParameterConverter dispatchParameterConverter =
            new UpcallParameterConverter(this);

    private DocumentationFormater documentationFormater = new DocumentationFormater(this);

    public JavaAPI(ProxyContext context, List<String> groupId, Name projectName) {
        this.context = context;
        this.groupId = new FullyQualifiedName(groupId.stream().map(Name::fromLower).toList());
        this.projectName = projectName;
    }

    public ProxyContext getContext() {
        return context;
    }

    public Name getProjectName() {
        return projectName;
    }

    /** Return the string of the base package ({groupId}.lib{projectName}). */
    public String basePackage() {
        return groupId.join((n) -> toJavaLower(n.getLastName()), "", ".", "");
    }

    /** Return the string of the package for the given module. */
    public String packagePath(Module module) {
        return groupId.append(module.name).join((n) -> toJavaLower(n.getLastName()), "", ".", "");
    }

    /** Return the string of the package for the given class. */
    public String packagePath(FullyQualifiedName className) {
        return groupId.append(className.getParentFullyQualifiedName())
                .join((n) -> toJavaLower(n.getLastName()), "", ".", "");
    }

    /** Return the path of the file to generate for a module. */
    public Path filepath(Module module) {
        Path p = Path.of(".");
        for (var n : groupId.names) {
            p = p.resolve(toJavaLower(n));
        }
        for (var n : module.name.names) {
            p = p.resolve(toJavaLower(n));
        }
        return p.resolve(module.name.getLastName().toPascal().concat("Package.java"));
    }

    /** Return the path of the file to generate for a class. */
    public Path filepath(FullyQualifiedName className) {
        Path p = Path.of(".");
        for (var n : groupId.names) {
            p = p.resolve(toJavaLower(n));
        }
        for (var n : className.getParentFullyQualifiedName().names) {
            p = p.resolve(toJavaLower(n));
        }
        return p.resolve(className.getLastName().toPascal().concat(".java"));
    }

    /**
     * Return the name formatted in Camel notation. If the result collides with a java keyword, an
     * underscore is appended.
     */
    public String toJavaCamel(Name name) {
        String camel = name.toCamel();
        if (JavaReservedWords.KEYWORDS.contains(camel)) return camel + "_";
        return camel;
    }

    /**
     * Return the name formatted in Lower notation. If the result collides with a java keyword, an
     * underscore is appended.
     */
    public String toJavaLower(Name name) {
        String camel = name.toLower();
        if (JavaReservedWords.KEYWORDS.contains(camel)) return camel + "_";
        return camel;
    }

    /**
     * Return the name to use for a function. If isMethod is true, also check against methods that
     * come from the Java Object class.
     */
    public String functionName(Name name, boolean isMethod) {
        String camel = name.toCamel();
        if (JavaReservedWords.KEYWORDS.contains(camel)
                || (isMethod && JavaReservedWords.RESERVED_METHODS.contains(camel))) {
            return camel + "_";
        }
        return camel;
    }

    public String formatDoc(String str, int indent) {
        return documentationFormater.formatAnyDoc(str, indent);
    }

    public String formatDoc(Declaration decl, int indent) {
        return documentationFormater.formatDoc(decl, indent);
    }

    /** Return the name of a java argument. */
    public String javaArgName(Name name) {
        return toJavaCamel(name);
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

    /** Create a unique temporary name, in the form of `{prefix}{unique_number}${name}`. */
    public String makeTemp(Name name, String prefix) {
        return makeTempName(Name.fromCamel(prefix)).toCamel().concat("$").concat(name.toCamel());
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
            case UINT8 -> "uint8_t";
            case SINT8 -> "int8_t";
            case UINT16 -> "uint16_t";
            case SINT16 -> "int16_t";
            case UINT32 -> "uint32_t";
            case SINT32 -> "int32_t";
            case UINT64 -> "uint64_t";
            case SINT64 -> "int64_t";
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

    /** Return the Java name of a type when it's used as a return type. */
    public String javaReturnTypename(TypeExpr type) {
        String typename = typenameGenerator.javaTypename(type.referencedType());
        if (type.isPointer()) typename = "java.util.Optional<" + typename + ">";
        return typename;
    }

    /** Return the native Java name of a type. */
    public String javaNativeTypename(TypeExpr type) {
        return typenameGenerator.javaNativeTypename(type);
    }

    /** Return the native Java name of a type when it's used as a return type. */
    public String javaNativeReturnTypename(TypeExpr type) {
        return typenameGenerator.javaNativeReturnTypename(type);
    }

    /** Return the JNI name of a type. */
    public String jniTypename(TypeExpr type) {
        return typenameGenerator.jniTypename(type);
    }

    /** Return the JNI name of a type. */
    public String jniReturnTypename(TypeExpr type) {
        return typenameGenerator.jniTypename(type.referencedType());
    }

    /** Return the C name of a type. */
    public String cTypename(TypeExpr type) {
        return typenameGenerator.cTypename(type);
    }

    /**
     * Return the name of the function with the given symbol to implement in the JNI layer. This
     * function does not support array owning classes, consider using {@link #jniName(String,
     * TypeExpr)} for that purpose.
     */
    public String jniName(String symbol, FullyQualifiedName owningClass) {
        StringBuilder builder = new StringBuilder("Java_");
        Function<FullyQualifiedName, String> mangle =
                (n) -> toJavaLower(n.getLastName()).replace("_", "_1");
        if (context.getModule(owningClass) != null) {
            builder.append(groupId.join(mangle, "", "_", "_"))
                    .append(
                            owningClass.join(
                                    mangle,
                                    "",
                                    "_",
                                    "_"
                                            .concat(
                                                    owningClass
                                                            .getLastName()
                                                            .toPascal()
                                                            .concat("Package"))));
        } else {
            builder.append(
                    javaTypename(owningClass.asTypeExpr()).replace("_", "_1").replace(".", "_"));
        }
        // All native function handle names start with `$` (Unicode character: 00024)
        return builder.append("_")
                .append(symbol.replace("_", "_1").replace("$", "_00024"))
                .toString();
    }

    /** Return the name of the function with the given symbol to implement in the JNI layer. */
    private String jniName(String symbol, TypeExpr roleType) {
        if (context.isClassType(roleType) || context.isException(roleType)) {
            return jniName(symbol, roleType.getName());
        }
        StringBuilder builder = new StringBuilder("Java_");
        if (roleType.isArray()) {
            builder.append(
                    javaTypename(roleType.elementType())
                            .replace("_", "_1")
                            .replace(".", "_")
                            .concat(
                                    roleType.elementType().isPointer()
                                            ? "_00024PtrArray"
                                            : "_00024Array"));
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
        // All native function handle names start with `$` (Unicode character: 00024)
        return builder.append("_")
                .append(symbol.replace("_", "_1").replace("$", "_00024"))
                .toString();
    }

    /** Return the name of the function to implement in the JNI layer. */
    public String jniName(FunctionDecl function) {
        if (function.role == null) {
            return jniName("$" + function.symbol, function.name.getParentFullyQualifiedName());
        } else {
            return jniName("$" + function.symbol, function.role.type);
        }
    }

    public String javaTypeJNISignature(TypeExpr type) {
        String javaType = javaTypename(type);
        if (type.isFunction()) {
            int lastDot = javaType.lastIndexOf(".");
            return "L%s$%s;"
                    .formatted(
                            javaType.substring(0, lastDot).replace(".", "/"),
                            javaType.substring(lastDot + 1));
        } else {
            return "L%s;".formatted(javaTypename(type).replace(".", "/"));
        }
    }

    /**
     * Return a string of the signature for the corresponding function type {@code type}.
     *
     * <p>This is used to create {@code methodID} getters in the JNI layer for callback and
     * dynamic-dispatch upcalls.
     *
     * <p>If classDecl is non-null, the function signature is considered to be used for dynamic
     * dispatch: the class itself is the first argument (jobject back reference of the library
     * shadow object). Otherwise, the function type is the first argument (corresponding to the
     * {@code CallbackData.data} field).
     */
    public String jniSignature(FunctionTypeExpr type, ClassDecl classDecl) {
        StringBuilder builder = new StringBuilder("(");
        // The first argument of the signature is a reference to a Java object: either
        // an object with the corresponding dispatchable function, or a funnction
        // itself.
        if (classDecl != null) {
            builder.append(javaTypeJNISignature(classDecl.name.asTypeExpr()));
        } else {
            builder.append(javaTypeJNISignature(type));
        }
        type.parameters.stream()
                .skip(classDecl == null ? 0 : 1)
                .map(p -> p.type)
                .map(t -> jniTypeSignature(t, false))
                .forEach(builder::append);
        builder.append(")").append(jniTypeSignature(type.returnType, true));
        return builder.toString();
    }

    /** Return the JNI type signature string for {@code type}. */
    public String jniTypeSignature(TypeExpr type, boolean isReturn) {
        return jniTypeSignatureGenerator.jniTypeSignature(type, isReturn);
    }

    /** Create a call to the C symbol in the JNI layer. */
    public String callCSymbol(FunctionDecl functionDecl) {
        List<String> args =
                functionDecl.type.parameters.stream()
                        .map(p -> jniValueName(p.name))
                        .collect(Collectors.toCollection(ArrayList::new));
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            String className = functionDecl.role.type.getName().getLastName().toPascal();
            args.add("_java_vm");
            args.add(CGenerator.makeJNICall("NewWeakGlobalRef", List.of("_self")).toString());
            args.add("&%s_vtable".formatted(className));
        }

        return CGenerator.makeCall(functionDecl.symbol, args).toString();
    }

    /**
     * Create a call to the C function held by the {@code addr} field of the {@code callbackData}
     * struct in the JNI layer.
     *
     * <p>A cast to the corresponding function pointer type is performed on-site, as the field is a
     * {@code void*}.
     */
    public String callCPointer(FunctionTypeExpr functionType, String callbackData) {
        CharSequence ptrType =
                new StringBuilder(cTypename(functionType.returnType))
                        .append("(*)(struct callback_data")
                        .append(functionType.parameters.isEmpty() ? "" : ", ")
                        .append(
                                functionType.parameters.stream()
                                        .map(p -> cTypename(p.type))
                                        .collect(Collectors.joining(", ")))
                        .append(")");
        CharSequence callee = CGenerator.makeCast(ptrType, callbackData + ".addr");
        List<String> args =
                Stream.concat(
                                Stream.of(callbackData),
                                functionType.parameters.stream().map(p -> jniValueName(p.name)))
                        .toList();
        return CGenerator.makeCall(callee, args).toString();
    }

    /** Create a call to the Java native function. */
    public String callJavaNative(FunctionDecl functionDecl, String nativeName) {
        List<String> args =
                functionDecl.type.parameters.stream()
                        .map(p -> javaValueName(p.name))
                        .collect(Collectors.toCollection(ArrayList::new));
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            args.add("this");
        }
        return JavaGenerator.makeCall(nativeName, args).toString();
    }

    /** Create a call to the method . */
    public String callJavaUpcall(FunctionTypeExpr functionType, String callee, boolean isDispatch) {
        List<String> args =
                functionType.parameters.stream()
                        .skip(isDispatch ? 1 : 0)
                        .map(p -> javaValueName(p.name))
                        .toList();
        return JavaGenerator.makeCall(callee, args).toString();
    }

    /** Return a string that performs a JNI call to the local {@code jobject}. */
    public String callJNIUpcall(FunctionTypeExpr functionType, boolean isDispatch) {
        List<String> args = new ArrayList<>(List.of("clazz"));
        // `_self` always exists in dispatch functions: it is the back reference.
        if (isDispatch) args.add("_self");
        else args.add(CGenerator.makeCast("jobject", "_callback_data.data").toString());
        functionType.parameters.stream()
                .skip(isDispatch ? 1 : 0)
                .map(p -> jniValueName(p.name))
                .forEachOrdered(args::add);
        String envMember;
        if (context.isClassType(functionType.returnType)) {
            envMember = callStaticTypeMethodJNIName(NativeType.UINT64.typeExpr);
        } else {
            envMember = callStaticTypeMethodJNIName(functionType.returnType);
        }
        return CGenerator.makeJNICall(envMember, "method", args).toString();
    }

    /** Return whether a function returns void. */
    public boolean returnsVoid(FunctionTypeExpr functionType) {
        return functionType.returnType.isName()
                && context.getTypeDecl(functionType.returnType.getName())
                        .equals(NativeType.VOID.declaration);
    }

    /** Create the return statement for the function in its JNI layer implementation */
    public String makeCReturnStatement(FunctionTypeExpr functionType, String returnedValue) {
        return returnConverter.cReturnStatement(functionType, returnedValue);
    }

    /** Create the return statement for the function in its JNI layer implementation */
    public String makeCUpcallReturnStatement(FunctionTypeExpr functionType, String returnedValue) {
        return dispatchReturnConverter.cReturnStatement(functionType, returnedValue);
    }

    /** Create the return statement for the function in its Java layer implementation */
    public String makeJavaReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.javaReturnStatement(functionDecl, returnedValue);
    }

    public String makeJavaUpcallReturnStatement(
            FunctionTypeExpr functionType, String returnedValue) {
        return dispatchReturnConverter.javaReturnStatement(functionType, returnedValue);
    }

    /** Create the default return statement for the function in its Java layer implementation */
    public String makeJavaUpcallDefaultReturnStatement(FunctionTypeExpr functionType) {
        return dispatchReturnConverter.javaDefaultReturnStatement(functionType);
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaParameters(FunctionTypeExpr functionType) {
        return functionType.parameters.stream()
                .map(p -> "%s %s".formatted(javaTypename(p.type), javaArgName(p.name)))
                .collect(Collectors.joining(", "));
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .skip(isMethod(functionDecl) ? 1 : 0)
                .map(p -> "%s %s".formatted(javaTypename(p.type), javaArgName(p.name)))
                .collect(Collectors.joining(", "));
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaUpcallParameters(FunctionTypeExpr functionType, boolean isDispatch) {
        return functionType.parameters.stream()
                .skip(isDispatch ? 1 : 0)
                .map(
                        p ->
                                ", %s %s"
                                        .formatted(
                                                //
                                                p.type.isFunction()
                                                        ? javaNativeReturnTypename(p.type)
                                                        : javaNativeTypename(p.type),
                                                javaArgName(p.name)))
                .collect(Collectors.joining());
    }

    /** Return the string to declare arguments in the native Java function. */
    public String javaNativeParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                functionDecl.type.parameters.stream()
                        .map(
                                p ->
                                        "%s %s"
                                                .formatted(
                                                        javaNativeTypename(p.type),
                                                        javaArgName(p.name)))
                        .collect(Collectors.joining(", ")));
        // In the java world, SHADOW_ALLOC functions have 1 hidden arguments: a
        // reference to the object. The vtable is generated in the JNI layer.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(builder.isEmpty() ? "" : ", ")
                    .append(javaTypename(functionDecl.role.type))
                    .append(" _self");
        }
        return builder.toString();
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
        // Java passes an additional argument for shadow alloc functions, corresponding
        // to the object itself.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(builder.isEmpty() ? "" : ", ").append("jobject _self");
        }
        return builder.toString();
    }

    /** Return the string to declare arguments in the return JNI callback. */
    public String jniCallbackParameters(FunctionTypeExpr functiontType) {
        StringBuilder builder =
                new StringBuilder("JNIEnv *env, jobject obj, jobject _callback_data");
        if (!functiontType.parameters.isEmpty()) {
            builder.append(", ")
                    .append(
                            functiontType.parameters.stream()
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

    /** Return the string to declare arguments in the JNI function. */
    public String jniUpcallParameters(FunctionTypeExpr functionType, boolean isDispatch) {
        StringBuilder builder = new StringBuilder();
        if (isDispatch) {
            builder.append("void *_self_data, jobject _self");
        } else {
            builder.append("struct callback_data _callback_data");
        }

        if (functionType.parameters.size() > (isDispatch ? 1 : 0)) builder.append(", ");
        builder.append(
                functionType.parameters.stream()
                        .skip(isDispatch ? 1 : 0)
                        .map(p -> "%s %s".formatted(cTypename(p.type), jniArgName(p.name)))
                        .collect(Collectors.joining(", ")));
        return builder.toString();
    }

    /** Return the string to declare arguments in the C function. */
    public String cParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                functionDecl.type.parameters.stream()
                        .map(p -> "%s %s".formatted(cTypename(p.type), jniArgName(p.name)))
                        .collect(Collectors.joining(", ")));
        // The C Symbol expects two additional hidden arguments:
        // - The self argument of the class's raw pointer type.
        // - The vtable argument of the class' vtable raw pointer type.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(builder.isEmpty() ? "" : ", ")
                    .append("void *_self_data, void *_self, void *vtable");
        }
        return builder.toString();
    }

    /** Create the string to convert the java parameter for calling the native handle. */
    public String makeJavaParamConversion(Parameter param, boolean isFirstMethodParam) {
        return parameterConverter.javaParam(param, isFirstMethodParam);
    }

    /** Create the string to convert the java parameter for calling the native handle. */
    public String makeJavaUpcallParamConversion(Parameter param, String callbackName) {
        return dispatchParameterConverter.javaParam(param, callbackName);
    }

    /** Create the string to convert the JNI parameter for calling the C symbol. */
    public String makeJNIParamConversion(Parameter param, FunctionDecl functionDecl) {
        return parameterConverter.jniParam(param, functionDecl);
    }

    /** Create the string to convert the JNI parameter for calling the C symbol. */
    public String makeJNIParamUpdate(Parameter param) {
        return parameterConverter.jniParamUpdate(param);
    }

    public String makeJNIUpcallParamConversion(Parameter param) {
        return dispatchParameterConverter.jniParam(param);
    }

    public String makeJNIUpcallParamUpdate(Parameter param) {
        return dispatchParameterConverter.jniParamUpdate(param);
    }

    /** Return whether a function is a class method. */
    public boolean isMethod(FunctionDecl functionDecl) {
        return functionDecl.role != null
                && functionDecl.role.kind.compareTo(RoleKind.DESTRUCT) <= 0;
    }

    /** Return the typename of the parent of a class. */
    public String parentClass(ClassDecl classDecl) {
        return classDecl.parent == null
                ? "com.adacore.gnatpolyglot.runtime.PolyglotObject"
                : javaTypename(classDecl.parent.asTypeExpr());
    }

    /** Return the class modifier for its inheritability. */
    public String getOverridability(ClassDecl classDecl) {
        return switch (classDecl.inheritability) {
            case FINAL -> "final";
            case INHERITABLE -> "";
            case VIRTUAL -> "abstract";
        };
    }

    /** Return the member functions of a type. */
    public ProxyContext.FunctionMembersEntry getMembers(TypeDecl decl) {
        return context.getMembers(decl.name.asTypeExpr());
    }

    /** Return the member functions of a type. */
    public ProxyContext.FunctionMembersEntry getArrayFunctions(TypeDecl decl) {
        return context.getMembers(decl.name.asTypeExpr().makeArray());
    }

    /** Return the member functions of a type. */
    public ProxyContext.FunctionMembersEntry getPtrArrayFunctions(TypeDecl decl) {
        return context.getMembers(decl.name.asTypeExpr().makePointer(false, false).makeArray());
    }

    /** Return the name of the data owner. */
    public String javaOwner(Owner returnOwner) {
        return "com.adacore.gnatpolyglot.runtime.PolyglotData.Owner."
                .concat(returnOwner.toString());
    }

    /** Return the name of the required data owner. */
    public String javaOwner(RequiredOwner requiredOwner) {
        return switch (requiredOwner) {
            case LIBRARY -> javaOwner(Owner.LIBRARY);
            case USER -> javaOwner(Owner.USER);
            default -> "";
        };
    }

    /** Return the name of the function to call to get the scalar reference jclass value. */
    public String runtimeReferenceTypeGetClass(NativeType nativeType) {
        return "gnatpolyglot_proxy2java_%s_class"
                .formatted(TypenameGenerator.nativeReferenceTypename(nativeType));
    }

    /**
     * Return the name of the function to call to get the jmethodID of the getter for the
     * corresponding scalar reference class.
     */
    public String runtimeReferenceTypeGetGetter(NativeType nativeType) {
        return "gnatpolyglot_proxy2java_%s_getValue"
                .formatted(TypenameGenerator.nativeReferenceTypename(nativeType));
    }

    /**
     * Return the name of the function to call to get the jmethodID of the setter for the
     * corresponding scalar reference class.
     */
    public String runtimeReferenceTypeGetSetter(NativeType nativeType) {
        return "gnatpolyglot_proxy2java_%s_setValue"
                .formatted(TypenameGenerator.nativeReferenceTypename(nativeType));
    }

    /**
     * Return the name of the function to call in the JEnv to call a Java method that returns the
     * corresponding type.
     */
    public String callTypeMethodJNIName(TypeExpr returnedType) {
        if (context.isNativeScalar(returnedType)) {
            TypeDecl decl = context.getTypeDecl(returnedType.getName());
            if (decl instanceof EnumerationDecl) decl = NativeType.SINT32.declaration;
            NativeTypeDecl nativeDecl = (NativeTypeDecl) decl;
            return switch (nativeDecl.nativeType) {
                case BOOL -> "CallBooleanMethod";
                case CHAR -> "CallCharMethod";
                case FLOAT32 -> "CallFloatMethod";
                case FLOAT64 -> "CallDoubleMethod";
                case UINT8, SINT8 -> "CallByteMethod";
                case UINT16, SINT16 -> "CallShortMethod";
                case UINT32, SINT32 -> "CallIntMethod";
                case UINT64, SINT64 -> "CallLongMethod";
                case VOID -> "CallVoidMethod";
                default -> throw new UnsupportedOperationException("Unsupported native type");
            };
        } else if (context.isClassType(returnedType) || returnedType.isArray()) {
            return "CallObjectMethod";
        }
        throw new UnsupportedOperationException("Unsupported type");
    }

    /**
     * Return the name of the function to call in the JEnv to call a static Java method that returns
     * the corresponding type.
     */
    public String callStaticTypeMethodJNIName(TypeExpr returnedType) {
        if (context.isNativeScalar(returnedType)) {
            TypeDecl decl = context.getTypeDecl(returnedType.getName());
            if (decl instanceof EnumerationDecl) decl = NativeType.SINT32.declaration;
            NativeTypeDecl nativeDecl = (NativeTypeDecl) decl;
            return switch (nativeDecl.nativeType) {
                case BOOL -> "CallStaticBooleanMethod";
                case CHAR -> "CallStaticCharMethod";
                case FLOAT32 -> "CallStaticFloatMethod";
                case FLOAT64 -> "CallStaticDoubleMethod";
                case UINT8, SINT8 -> "CallStaticByteMethod";
                case UINT16, SINT16 -> "CallStaticShortMethod";
                case UINT32, SINT32 -> "CallStaticIntMethod";
                case UINT64, SINT64 -> "CallStaticLongMethod";
                case VOID -> "CallStaticVoidMethod";
                default -> throw new UnsupportedOperationException("Unsupported native type");
            };
        } else if (context.isClassType(returnedType)
                || context.isStringType(returnedType)
                || returnedType.isArray()) {
            return "CallStaticObjectMethod";
        } else if (returnedType.isPointer()) {
            if (context.isStringOrArray(returnedType.pointedType()))
                return "CallStaticObjectMethod";
            return "CallStaticLongMethod";
        }
        throw new UnsupportedOperationException("Unsupported type");
    }

    /**
     * Return the clone constructor of a ClassDecl.
     *
     * <p>The clone constructor must take a single parameter, and its type must be the class
     * declaration itself, or a reference to it.
     */
    public FunctionDecl getCloneCtor(ClassDecl classDecl, RoleKind roleKind) {
        if (roleKind != RoleKind.ALLOC && roleKind != RoleKind.SHADOW_ALLOC)
            throw new IllegalArgumentException("roleKind must be ALLOC or SHADOW_ALLOC");
        TypeExpr paramType = classDecl.name.asTypeExpr();
        List<TypeExpr> cpParams =
                List.of(paramType, paramType.makeReference(false), paramType.makeReference(true));
        return getMembers(classDecl).allocFunctions.stream()
                .filter(f -> f.role.kind == roleKind)
                .filter(f -> f.type.parameters.size() == 1)
                .filter(
                        f ->
                                cpParams.stream()
                                        .anyMatch(p -> p.equals(f.type.parameters.getFirst().type)))
                .findFirst()
                .orElse(null);
    }

    public FunctionDecl getMatchingShadowAlloc(FunctionDecl alloc) {
        if (alloc.role == null || alloc.role.kind != RoleKind.ALLOC) {
            throw new IllegalArgumentException("Function does not have the role ALLOC");
        }
        ClassDecl classDecl = (ClassDecl) context.getTypeDecl(alloc.role.type.getName());
        if (classDecl.inheritability == Inheritability.FINAL) return null;
        return getMembers(classDecl).allocFunctions.stream()
                .filter(a -> a.role.kind == RoleKind.SHADOW_ALLOC && a.type.equals(alloc.type))
                .findFirst()
                .orElse(null);
    }

    public String enumByteBufferGet(NativeType nativeType) {
        return switch (nativeType) {
            case SINT8 -> "get";
            case SINT16 -> "getShort";
            case SINT32 -> "getInt";
            case SINT64 -> "getLong";
            default -> throw new UnsupportedOperationException(
                    "enumerations of type %d are not supported".formatted(nativeType));
        };
    }

    public String enumByteBufferPut(NativeType nativeType) {
        return switch (nativeType) {
            case SINT8 -> "put";
            case SINT16 -> "putShort";
            case SINT32 -> "putInt";
            case SINT64 -> "putLong";
            default -> throw new UnsupportedOperationException(
                    "enumerations of type %d are not supported".formatted(nativeType));
        };
    }

    private String refFunctionName(TypeExpr typeExpr, String suffix) {
        if (typeExpr.isArray() && context.isNativeScalar(typeExpr.elementType())) {
            TypeDecl decl = context.getTypeDecl(typeExpr.getName());
            if (decl instanceof NativeTypeDecl nativeDecl)
                return ("gnatpolyglot_ada2java_%s_Ref_" + suffix)
                        .formatted(TypenameGenerator.nativeArrayTypename(nativeDecl.nativeType));
        } else if (context.isStringType(typeExpr)) {
            return "gnatpolyglot_ada2java_PolyglotString_Ref_" + suffix;
        }
        FullyQualifiedName name =
                typeExpr.isArray() ? typeExpr.elementType().getName() : typeExpr.getName();
        return name.getParentFullyQualifiedName()
                .join((n) -> n.getLastName().toLower(), "", "__", "_")
                .concat(name.getLastName().toPascal())
                .concat(
                        typeExpr.isArray()
                                ? typeExpr.elementType().isPointer() ? "__PtrArray" : "__Array"
                                : "")
                .concat("_Ref_")
                .concat(suffix);
    }

    public String refClassFunctionName(TypeExpr typeExpr) {
        return refFunctionName(typeExpr, "class");
    }

    public String refCtorFunctionName(TypeExpr typeExpr) {
        return refFunctionName(typeExpr, "ctor");
    }

    /** Return the name of the function to get the {@code jclass} for the given type name. */
    public String jclassFunctionName(FullyQualifiedName name) {
        Module mod = context.getModule(name);
        if (mod != null)
            return packagePath(mod).replace(".", "__")
                    + "__"
                    + name.getLastName().toPascal()
                    + "Package";
        return javaTypename(name.asTypeExpr()).replace(".", "__");
    }

    HashMap<FunctionTypeExpr, String> javaNames = new HashMap<>();
    int nameCounter = 0;
    HashMap<FunctionTypeExpr, String> javaUpcalls = new HashMap<>();
    HashMap<FunctionTypeExpr, String> javaDowncalls = new HashMap<>();
    HashMap<FunctionTypeExpr, String> javaLambdas = new HashMap<>();
    int functionCounter = 0;

    public String getJavaGenericCallback(TypeExpr type) {
        FunctionTypeExpr functionType = (FunctionTypeExpr) type.referencedType();
        String refSuffix = type.isReference() ? ".Ref" : "";
        String genericParams =
                Stream.concat(
                                functionType.parameters.stream().map(p -> p.type),
                                returnsVoid(functionType)
                                        ? Stream.of()
                                        : Stream.of(functionType.returnType))
                        .map(t -> typenameGenerator.javaTypename(t, true))
                        .collect(Collectors.joining(", ", "<", ">"));
        if (functionType.parameters.size() > 10)
            throw new UnsupportedOperationException("Too many arguments");
        if (returnsVoid(functionType)) {
            if (functionType.parameters.size() == 0)
                return "com.adacore.gnatpolyglot.runtime.Functions.Consumer0" + refSuffix;
            return "com.adacore.gnatpolyglot.runtime.Functions.Consumer"
                    + functionType.parameters.size()
                    + refSuffix
                    + genericParams;
        } else
            return "com.adacore.gnatpolyglot.runtime.Functions.Function"
                    + functionType.parameters.size()
                    + refSuffix
                    + genericParams;
    }

    public String getJavaCallbackTypename(FunctionTypeExpr functionType) {
        String typename = javaNames.get(functionType);
        if (typename == null) {
            typename = "Callback" + nameCounter++;
            javaNames.put(functionType, typename);
        }
        return typename;
    }

    /** Return the name of the Java function for Upcalling callbacks. */
    public String getJavaUpCallbackName(FunctionTypeExpr functionType) {
        String functionName = javaUpcalls.get(functionType);
        if (functionName == null) {
            functionName = "upcall" + functionCounter++;
            javaUpcalls.put(functionType, functionName);
        }
        return functionName;
    }

    /** Return the name of the Java function for Upcalling callbacks. */
    public String getJavaDownCallbackName(FunctionTypeExpr functionType) {
        String functionName = javaDowncalls.get(functionType);
        if (functionName == null) {
            functionName = "downcall" + functionCounter++;
            javaDowncalls.put(functionType, functionName);
        }
        return functionName;
    }

    /**
     * Return the name of the Java function for returning the lambda of reference callback
     * parameters.
     */
    public String getJavaLambdaName(FunctionTypeExpr functionType) {
        String functionName = javaLambdas.get(functionType);
        if (functionName == null) {
            functionName = "lambda" + functionCounter++;
            javaLambdas.put(functionType, functionName);
        }
        return functionName;
    }

    /**
     * Return the name of the Java function for returning the lambda of reference callback
     * parameters.
     */
    public String getJavaLambdaFQN(FunctionTypeExpr functionType) {
        String functionName = getJavaLambdaName(functionType);
        return basePackage() + ".Callbacks." + functionName;
    }

    /**
     * Return the name of the C function for Upcalling callbacks. This function is used inside
     * callback data created by the JNI layer and calls the corresponding Java implentation.
     */
    public String getJNIUpCallbackName(FunctionTypeExpr functionType) {
        String javaFunction = getJavaUpCallbackName(functionType);
        return "JNI_" + basePackage().replace(".", "_") + "_Callbacks_" + javaFunction;
    }

    /**
     * Return the name of the JNI function for Upcalling callbacks. It is the JNI implementation of
     * its corresponding native Java function.
     */
    public String getJNIDownCallbackName(FunctionTypeExpr functionType) {
        String javaFunction = getJavaDownCallbackName(functionType);
        return "Java_" + basePackage().replace(".", "_") + "_Callbacks_" + javaFunction;
    }

    /**
     * Return the name of the C function for returning the lambda of downcalling callback. This
     * function calls the corresponding Java function that instantiates a Java lambda to perform a
     * downcall to the given callback data, and returns it.
     */
    public String getJNILambdaName(FunctionTypeExpr functionType) {
        String javaFunction = getJavaLambdaName(functionType);
        return "JNI_" + basePackage().replace(".", "_") + "_Callbacks_" + javaFunction;
    }

    /** Return a string of the Java lambda that calls the Java native {@code callee} function. */
    public String buildCallbackLambda(FunctionTypeExpr functionType, String callbackData) {
        StringBuilder builder =
                new StringBuilder("(")
                        .append(
                                functionType.parameters.stream()
                                        .map(p -> javaArgName(p.name))
                                        .collect(Collectors.joining(", ")))
                        .append(") -> {");
        // Make the conversion of java parameters
        for (var param : functionType.parameters) {
            builder.append(makeJavaParamConversion(param, false)).append(";");
        }
        // Create the call to the native function
        ArrayList<String> args = new ArrayList<>();
        args.add(callbackData);
        String callee = basePackage() + ".Callbacks." + getJavaDownCallbackName(functionType);
        functionType.parameters.stream().map(p -> javaValueName(p.name)).forEach(args::add);
        if (returnsVoid(functionType)) {
            builder.append(JavaGenerator.makeCall(callee, args))
                    .append(";")
                    .append(basePackage())
                    .append(".Library.raiseIfException();");
        } else {
            String lambdaReturnedValue = makeTemp(Name.fromCamel("lambda"), "returnedValue");
            builder.append(javaNativeReturnTypename(functionType.returnType))
                    .append(" ")
                    .append(lambdaReturnedValue)
                    .append(" = ")
                    .append(JavaGenerator.makeCall(callee, args))
                    .append(";\n")
                    .append(basePackage())
                    .append(".Library.raiseIfException();\n")
                    .append(returnConverter.javaReturnStatement(functionType, lambdaReturnedValue));
        }

        return builder.append("}").toString();
    }

    /** Return the name of the function of the functional interface. */
    public String functionalInterfaceMethod(FunctionTypeExpr functionType) {
        if (returnsVoid(functionType)) return functionType.parameters.size() > 0 ? "accept" : "run";
        else return functionType.parameters.size() > 0 ? "apply" : "get";
    }

    /** Return all the child types of the class. */
    public List<ClassDecl> getChildTypes(ClassDecl classDecl) {
        return context.getChildTypes(classDecl);
    }

    /**
     * Return the FQN of the function to call to identify the given type when passed to the user.
     */
    public String identificationFunction(TypeExpr typeExpr) {
        if (context.isClassType(typeExpr) && context.requiresIdentification(typeExpr)) {
            return javaTypename(typeExpr) + ".identify";
        }
        return null;
    }

    public String instantiateObject(TypeExpr type, String data) {
        if (getContext().requiresIdentification(type)) {
            return JavaGenerator.makeCall(identificationFunction(type), List.of(data)).toString();
        } else {
            return JavaGenerator.makeNew(javaTypename(type), List.of(data)).toString();
        }
    }

    public String instantiateObject(TypeExpr type, String data, Owner owner) {
        if (getContext().requiresIdentification(type)) {
            return JavaGenerator.makeCall(
                            identificationFunction(type),
                            List.of(
                                    JavaGenerator.makeNew(
                                            "com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer",
                                            List.of(data, javaOwner(owner)))))
                    .toString();
        } else {
            return JavaGenerator.makeObjectFromAddress(javaTypename(type), data, javaOwner(owner))
                    .toString();
        }
    }
}
