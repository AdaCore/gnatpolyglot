package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl.Inheritability;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.VTableEntry;
import com.adacore.gnatpolyglot.proxy2java.codegen.CGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.DispatchParameterConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.DispatchReturnConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.JavaGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.ParameterConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.proxy2java.codegen.TypenameGenerator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaAPI extends LanguageAPI {

    private FullyQualifiedName groupId;

    private Name projectName;

    private ProxyContext context;

    private TypenameGenerator typenameGenerator = new TypenameGenerator(this);

    private ReturnConverter returnConverter = new ReturnConverter(this);

    private DispatchReturnConverter dispatchReturnConverter = new DispatchReturnConverter(this);

    private ParameterConverter parameterConverter = new ParameterConverter(this);

    private DispatchParameterConverter dispatchParameterConverter =
            new DispatchParameterConverter(this);

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

    /** Return the string of the package for the given class. */
    public String packagePath(FullyQualifiedName className) {
        return groupId.append(Name.fromLower("lib".concat(projectName.toLower())))
                .append(className.getParentFullyQualifiedName())
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

    /** Return the path of the file to generate for a class. */
    public Path filepath(FullyQualifiedName className) {
        Path p = Path.of(".");
        for (var n : className.getParentFullyQualifiedName().names) {
            p = p.resolve(n.toLower());
        }
        return p.resolve(className.getLastName().toPascal().concat(".java"));
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

    /** Return the Java name of a type when it's used as a return type. */
    public String javaReturnTypename(TypeExpr type) {
        return typenameGenerator.javaTypename(type.referencedType());
    }

    /** Return the native Java name of a type. */
    public String javaNativeTypename(TypeExpr type) {
        return typenameGenerator.javaNativeTypename(type);
    }

    /** Return the native Java name of a type when it's used as a return type. */
    public String javaNativeReturnTypename(TypeExpr type) {
        return typenameGenerator.javaNativeTypename(type.referencedType());
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
        } else if (context.isClassType(function.role.type)
                || context.isException(function.role.type)) {
            builder.append(javaTypename(function.role.type).replace(".", "_"));
        } else if (function.role.type.isArray()) {
            builder.append(
                    javaTypename(function.role.type.elementType())
                            .replace(".", "_")
                            .concat("_00024Array"));
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
        // All native function handle names start with `$` (Unicode character: 00024)
        return builder.append("__00024").append(function.name.getLastName().toCamel()).toString();
    }

    public String jniSignature(FunctionTypeExpr type, ClassDecl classDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append("(")
                .append(
                        "L%s;"
                                .formatted(
                                        javaTypename(classDecl.name.asTypeExpr())
                                                .replace(".", "/")));
        type.parameters.stream()
                .skip(1)
                .map(p -> p.type)
                .map(t -> jniTypeSignature(t, false))
                .forEach(builder::append);
        builder.append(")").append(jniTypeSignature(type.returnType, true));
        return builder.toString();
    }

    private String jniTypeSignature(TypeExpr type, boolean isReturn) {
        if (context.isNativeScalar(type)) {
            NativeTypeDecl decl = (NativeTypeDecl) context.getTypeDecl(type.getName());
            return switch (decl.nativeType) {
                case VOID -> "V";
                case BOOL -> "Z";
                case CHAR -> "C";
                case FLOAT32 -> "F";
                case FLOAT64 -> "D";
                case UINT8, SINT8 -> "B";
                case UINT16, SINT16 -> "S";
                case UINT32, SINT32 -> "I";
                case UINT64, SINT64 -> "J";
                default -> throw new UnsupportedOperationException("Unsupported native type");
            };
        }
        if (context.isClassType(type.referencedType())) {
            return jniTypeSignature(NativeType.UINT64.typeExpr, isReturn);
        }
        String javaNativeType =
                isReturn ? javaNativeReturnTypename(type) : javaNativeTypename(type);
        return "L%s;".formatted(javaNativeType.replace(".", "/"));
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
    public String callJavaDispatch(VTableEntry method) {
        List<String> args =
                method.functionType.parameters.stream()
                        .skip(1)
                        .map(p -> javaValueName(p.name))
                        .toList();
        return new StringBuilder("_self.")
                .append(JavaGenerator.makeCall(method.name.toCamel(), args))
                .toString();
    }

    public String callJNIDispatch(VTableEntry method) {
        List<String> args = new ArrayList<>(List.of("clazz", "_self"));
        method.functionType.parameters.stream()
                .skip(1)
                .map(p -> jniValueName(p.name))
                .forEachOrdered(args::add);
        String envMember;
        if (context.isClassType(method.functionType.returnType)) {
            envMember = callStaticTypeMethodJNIName(NativeType.UINT64.typeExpr);
        } else {
            envMember = callStaticTypeMethodJNIName(method.functionType.returnType);
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
    public String makeCReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.cReturnStatement(functionDecl, returnedValue);
    }

    /** Create the return statement for the function in its JNI layer implementation */
    public String makeCDispatchReturnStatement(VTableEntry method, String returnedValue) {
        return dispatchReturnConverter.cReturnStatement(method, returnedValue);
    }

    /** Create the return statement for the function in its Java layer implementation */
    public String makeJavaReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.javaReturnStatement(functionDecl, returnedValue);
    }

    public String makeJavaDispatchReturnStatement(VTableEntry method, String returnedValue) {
        return dispatchReturnConverter.javaReturnStatement(method, returnedValue);
    }

    /** Create the default return statement for the function in its Java layer implementation */
    public String makeJavaDispatchDefaultReturnStatement(FunctionTypeExpr functionType) {
        return dispatchReturnConverter.javaDefaultReturnStatement(functionType);
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .skip(isMethod(functionDecl) ? 1 : 0)
                .map(p -> "%s %s".formatted(javaTypename(p.type), javaArgName(p.name)))
                .collect(Collectors.joining(", "));
    }

    /** Return the string to declare arguments in the Java function. */
    public String javaDispatchParameters(FunctionTypeExpr functionType) {
        return functionType.parameters.stream()
                .skip(1)
                .map(p -> ", %s %s".formatted(javaNativeTypename(p.type), javaArgName(p.name)))
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
        // In the java world, SHADOW_ALLOC functions have 1 hidden arguments: a reference to the
        // object. The vtable is generated in the JNI layer.
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
        // Java passes an additional argument for shadow alloc functions, corresponding to
        // the object itself.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(builder.isEmpty() ? "" : ", ").append("jobject _self");
        }
        return builder.toString();
    }

    /** Return the string to declare arguments in the JNI function. */
    public String jniDispatchParameters(FunctionTypeExpr functionType) {
        StringBuilder builder = new StringBuilder("void *_self_data, jobject _self");
        if (functionType.parameters.size() > 1) {
            builder.append(", ")
                    .append(
                            functionType.parameters.stream()
                                    .skip(1)
                                    .map(
                                            p ->
                                                    "%s %s"
                                                            .formatted(
                                                                    cTypename(p.type),
                                                                    jniArgName(p.name)))
                                    .collect(Collectors.joining(", ")));
        }
        return builder.toString();
    }

    /** Return the string to declare arguments in the C function. */
    public String cParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                functionDecl.type.parameters.stream()
                        .map(p -> "%s %s".formatted(cTypename(p.type), p.name.toCamel()))
                        .collect(Collectors.joining(", ")));
        //  The C Symbol expects two additional hidden arguments:
        //  - The self argument of the class's raw pointer type.
        //  - The vtable argument of the class' vtable raw pointer type.
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
    public String makeJavaDispatchParamConversion(Parameter param) {
        return dispatchParameterConverter.javaParam(param);
    }

    /** Create the string to convert the JNI parameter for calling the C symbol. */
    public String makeJNIParamConversion(Parameter param) {
        return parameterConverter.jniParam(param);
    }

    public String makeJNIDispatchParamConversion(Parameter param) {
        return dispatchParameterConverter.jniParam(param);
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

    /** Return the name of the data owner. */
    public String javaOwner(Owner returnOwner) {
        return "com.adacore.gnatpolyglot.runtime.PolyglotData.Owner."
                .concat(returnOwner.toString());
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
            return switch (NativeTypeDecl.class.cast(context.getTypeDecl(returnedType.getName()))
                    .nativeType) {
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
            return switch (NativeTypeDecl.class.cast(context.getTypeDecl(returnedType.getName()))
                    .nativeType) {
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
}
