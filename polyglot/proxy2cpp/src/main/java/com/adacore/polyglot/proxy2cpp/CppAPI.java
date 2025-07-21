package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.NativeType.NativeTypeDecl;
import com.adacore.polyglot.proxy.ArrayTypeExpr;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.NameTypeExpr;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.PointerTypeExpr;
import com.adacore.polyglot.proxy.ProxyContext;
import com.adacore.polyglot.proxy.ReferenceTypeExpr;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.TypeDecl;
import com.adacore.polyglot.proxy.TypeExpr;
import com.adacore.polyglot.proxy.VTableEntry;
import java.nio.file.Path;
import java.util.List;
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
                return "polyglot::ada::strings::polyglot_string";
            case VOID:
                return "void";
            default:
                throw new UnsupportedOperationException(
                        nativeType.toString() + " is not handled yet");
        }
    }

    /** Return the refered C++ type's name. */
    public String cppTypename(TypeExpr typeExpr) {
        if (typeExpr instanceof NameTypeExpr name) {
            TypeDecl typeDecl = context.getTypeDecl(name.name);
            if (typeDecl instanceof NativeTypeDecl nativeType)
                return nativeTypeName(nativeType.nativeType);
            if (typeDecl instanceof ClassDecl)
                return name.name.join(fqn -> lastNameToCppName(fqn), "", "::", "");
        } else if (typeExpr instanceof ArrayTypeExpr array) {
            return "polyglot::ada::arrays::polyglot_array<" + cppTypename(array.typeExpr) + ">";
        } else if (typeExpr instanceof ReferenceTypeExpr ref) {
            StringBuilder builder = new StringBuilder();
            if (ref.isConst) builder.append("const ");
            builder.append(cppTypename(ref.typeExpr)).append(" &");
            return builder.toString();
        } else if (typeExpr instanceof PointerTypeExpr pointer) {
            StringBuilder builder = new StringBuilder();
            if (pointer.isConst) builder.append("const ");
            builder.append(cppTypename(pointer.typeExpr)).append(" *");
            return builder.toString();
        }
        throw new UnsupportedOperationException("Unsupported Cpp type");
    }

    /**
     * Create a string that represents the C++ typename when returning a value of type `typeExpr`
     */
    public String cppReturnTypename(TypeExpr typeExpr) {
        // When returning a reference to a non-native type, we cannot allocate a new proxy object
        // and return a real C++ reference to it. Instead, return a `view` to the returned pointer
        // that acts as a reference and that won't free the underlying pointer when destroyed.
        if (typeExpr instanceof ReferenceTypeExpr ref
                && !(context.getTypeDecl(ref.typeExpr.getName()) instanceof NativeTypeDecl)) {
            return cppTypename(ref.typeExpr).concat("::view");
        }
        return cppTypename(typeExpr);
    }

    private String lastNameToCppName(FullyQualifiedName fqn) {
        if (context.getTypeDecl(fqn) != null) return fqn.getLastName().toPascal();
        return fqn.getLastName().toLower();
    }

    /** Return the refered C type's name. */
    public String cTypename(TypeExpr typeExpr) {
        if (typeExpr instanceof NameTypeExpr name) {
            TypeDecl typeDecl = context.getTypeDecl(name.name);
            if (typeDecl instanceof NativeTypeDecl nativeType)
                return switch (nativeType.nativeType) {
                    case STRING -> "polyglot::ada::strings::string_data";
                    default -> nativeTypeName(nativeType.nativeType);
                };
            // Classes are mapped as pointers in C.
            if (typeDecl instanceof ClassDecl) return "void *";
        } else if (typeExpr instanceof ArrayTypeExpr) {
            return "polyglot::ada::arrays::array_data";
        } else if (typeExpr instanceof ReferenceTypeExpr ref) {
            // References are mapped as pointers in C.
            if (ref.typeExpr instanceof ArrayTypeExpr) return cTypename(ref.typeExpr);
            String constness = ref.isConst ? "const " : "";
            if (ref.typeExpr instanceof NameTypeExpr name
                    && context.getTypeDecl(name.name) instanceof NativeTypeDecl nativeType) {
                return constness
                        + switch (nativeType.nativeType) {
                            case STRING -> cTypename(name);
                            default -> nativeTypeName(nativeType.nativeType) + "*";
                        };
            }
            return constness + "void *";
        } else if (typeExpr instanceof PointerTypeExpr ptr) {
            String constness = ptr.isConst ? "const " : "";
            if (ptr.typeExpr instanceof NameTypeExpr name
                    && context.getTypeDecl(name.name) instanceof NativeTypeDecl nativeType)
                return constness + nativeTypeName(nativeType.nativeType) + "*";
            return constness + "void *";
        }
        throw new UnsupportedOperationException("Unsupported C type");
    }

    /** Create the string of the C++ namespace of the corresponding module. */
    public String namespacePath(Module module) {
        return module.name.join(r -> r.getLastName().toLower(), "", "::", "");
    }

    /** Return the path to the source file of the corresponing module. */
    public Path sourceFilePath(Module module) {
        return outputPath.resolve(
                module.name.join(r -> r.getLastName().toLower(), "", "_", ".cpp"));
    }

    /** Return the path to the header file of the corresponing module. */
    public Path headerFilePath(Module module) {
        return headerDir.resolve(module.name.join(r -> r.getLastName().toLower(), "", "_", ".h"));
    }

    /** Create a string for the name of the header guard macro. */
    public String headerGuard(Module module) {
        return module.name.join(r -> r.getLastName().toLower().toUpperCase(), "", "_", "_H");
    }

    /** Create a string corresponding to the C parameter. */
    private String toCParam(Parameter parameter) {
        StringBuilder builder = new StringBuilder();
        builder.append(cTypename(parameter.type)).append(" ").append(parameter.name.toLower());
        return builder.toString();
    }

    /** Create a string corresponding to the C++ parameter. */
    private String toCppParam(Parameter parameter) {
        StringBuilder builder = new StringBuilder();
        builder.append(cppTypename(parameter.type)).append(" ").append(parameter.name.toLower());
        return builder.toString();
    }

    /** Create a string of all the C parameters of the function's symbol. */
    public String cParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                functionDecl.type.parameters.stream()
                        .map(p -> toCParam(p))
                        .collect(Collectors.joining(", ")));
        // SHADOW_ALLOC functions have 2 hidden raw pointer arguments:
        //  - The self argument of the class's raw pointer type.
        //  - The vtable argument of the class' vtable raw pointer type.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(builder.isEmpty() ? "" : ", ").append("void *_self, void *vtable");
        }
        return builder.toString();
    }

    public boolean isMethod(FunctionDecl functionDecl) {
        // A method can have the role ``METHOD``, ``GETTER``, ``SETTER``, ``CONSTRUCT``, or
        // ``DESTRUCT``.
        return functionDecl.role != null
                && functionDecl.role.kind.compareTo(RoleKind.DESTRUCT) <= 0;
    }

    /** Return the const keyword if the function is a const method, else return an empty string. */
    public String getConstMethod(FunctionDecl functionDecl) {
        if (isMethod(functionDecl) && functionDecl.type.parameters.get(0).type.isConst())
            return "const";
        return "";
    }

    /** Create a string of all the C++ parameters of the function. */
    public String cppParameters(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                functionDecl.type.parameters.stream()
                        .skip(isMethod(functionDecl) ? 1 : 0)
                        .map(p -> toCppParam(p))
                        .collect(Collectors.joining(", ")));
        // SHADOW_ALLOC functions have 2 hidden arguments:
        //  - The self argument of the class's raw pointer type.
        //  - The vtable argument of the class' vtable raw pointer type.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            String className = functionDecl.role.type.getName().getLastName().toPascal();
            builder.append(builder.isEmpty() ? "" : ", ")
                    .append(className)
                    .append(" *_self, ")
                    .append(className)
                    .append("::vtable *_vtable");
        }
        return builder.toString();
    }

    /** Return a string that gets the value of a parameter for the call to the Ada subprogram */
    public String getParamForCall(Parameter p) {
        // When the parameter is a reference to a scalar, get
        // the correspondign address.
        if (p.type instanceof ReferenceTypeExpr ref
                && ref.typeExpr instanceof NameTypeExpr name
                && context.getTypeDecl(name.name) instanceof NativeTypeDecl nat) {
            return switch (nat.nativeType) {
                case STRING -> p.name.toLower() + ".data()";
                default -> "&" + p.name.toLower();
            };
        }
        if ((p.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof ArrayTypeExpr)
                || p.type instanceof ArrayTypeExpr
                || context.getTypeDecl(p.type.getName()) instanceof ClassDecl)
            return p.name.toLower() + ".data()";
        return p.name.toLower();
    }

    /** Create a call to the C symbol of the funtion. */
    public String callCSymbol(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        boolean funcIsMethod = isMethod(functionDecl);
        builder.append(functionDecl.symbol).append("(");
        // If the function is attached to a type, use ``this->data`` as the first argument.
        if (funcIsMethod) {
            if (functionDecl.role.type instanceof ArrayTypeExpr
                    && functionDecl.type.parameters.get(0).type instanceof PointerTypeExpr)
                builder.append("&");
            builder.append("this->_data");
            if (functionDecl.type.parameters.size() > 1) builder.append(", ");
        }

        builder.append(
                functionDecl.type.parameters.stream()
                        .skip(funcIsMethod ? 1 : 0)
                        .map(p -> getParamForCall(p))
                        .collect(Collectors.joining(", ")));
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            builder.append(functionDecl.type.parameters.isEmpty() ? "" : ", ")
                    .append("_self, _vtable");
        }
        builder.append(")");
        return builder.toString();
    }

    /** Create a string of the return statement. */
    public String makeReturnStatement(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder("return ");
        if (functionDecl.type.returnType instanceof ReferenceTypeExpr ref
                && ref.typeExpr instanceof NameTypeExpr name
                && context.getTypeDecl(name.name) instanceof NativeTypeDecl) {
            // When returning a reference to a native type, get the address returned by the `extern
            // "C"` function and dereference it to create a reference.
            // Note that unlike class types that require a wrapping proxy objects, native types can
            // be directely addressed, thus we can return real C++ references.
            builder.append("*");
        } else {
            // Otherwise, create a new object that wraps the returned pointer.
            builder.append(cppReturnTypename(functionDecl.type.returnType));
        }
        builder.append("(").append(callCSymbol(functionDecl)).append(")");
        return builder.toString();
    }

    /** Return the C++ name of the function to define ``functionDecl``. */
    public String functionDefinitionName(FunctionDecl functionDecl) {
        String functionName = functionDecl.getLastName().toLower();
        if (!isMethod(functionDecl)) return functionName;
        StringBuilder builder = new StringBuilder();
        builder.append(cppTypename(functionDecl.role.type)).append("::").append(functionName);
        return builder.toString();
    }

    /** Return the member functions of a type. */
    public ProxyContext.FunctionMembersEntry getMembers(TypeDecl decl) {
        return context.getMembers(decl.name.asTypeExpr());
    }

    public List<String> getIncludes(Module module) {
        return IncludeCollector.getIncludes(module);
    }

    /** Return whether the function overrides a function of the role type parent. */
    public boolean isOverriding(FunctionDecl functionDecl) {
        if (functionDecl.role == null || functionDecl.role.kind != RoleKind.METHOD) return false;
        TypeDecl type = context.getTypeDecl(functionDecl.role.type.getName());
        if (type instanceof ClassDecl classDecl) {
            if (classDecl.parent == null) return false;
            // All the parameter types, excluding the first one, must match.
            List<Parameter> functionParams = functionDecl.type.parameters.stream().skip(1).toList();

            return context.getMembers(classDecl.parent.asTypeExpr()).memberFunctions.stream()
                    .filter(m -> isMethod(m))
                    .anyMatch(
                            m ->
                                    m.name.getLastName().equals(functionDecl.name.getLastName())
                                            && m.type.parameters.stream()
                                                    .skip(1)
                                                    .toList()
                                                    .equals(functionParams)
                                            && m.type.returnType.equals(
                                                    functionDecl.type.returnType));
        }
        return false;
    }

    /** Return a string of the parameters of the dispatching function. */
    public String dispatchParameters(FunctionTypeExpr function) {
        StringBuilder builder = new StringBuilder();
        builder.append("void *_vtable, void *_self");
        for (var param : function.parameters.stream().skip(1).toList()) {
            builder.append(", ").append(toCParam(param));
        }
        return builder.toString();
    }

    /**
     * Construct a variable that is a static function pointer to the dispatching function of
     * functionDecl.
     *
     * <p>The resulting function type is similar to what the functionDecl is, except that: the
     * second argument is the vtable given to the shadow object; the second argument is the `this`
     * value given at construction of the shadow object, and only requires a cast to the C++ object
     * type when doing the dispatching call;
     *
     * <p>For the following class and function: <code>
     *  class A { int foo(int a, const A &b); }
     * </code>
     *
     * <p>the following will be generated: <code>
     * int (*foo_dispatch)(void *_self, void *_vtable, int a, const void *b)
     * </code>
     */
    public String dispatchFunctionPointerVariable(VTableEntry function) {
        StringBuilder builder = new StringBuilder();
        builder.append(cTypename(function.functionType.returnType))
                .append("(*")
                .append(function.name.toLower())
                .append("_dispatch)(")
                .append(dispatchParameters(function.functionType))
                .append(")");
        return builder.toString();
    }

    /**
     * Construct a variable that is a member function pointer to the functionDecl.
     *
     * <p>For the following class and function: <code>
     *  class A { int foo(int a, const A &b) const; }
     * </code>
     *
     * <p>the following will be generated: <code>
     * int (A::*foo_member)(int a, const A& b) const
     * </code>
     */
    public String memberFunctionPointerVariable(VTableEntry function, String className) {
        StringBuilder builder = new StringBuilder();
        builder.append(cppTypename(function.functionType.returnType))
                .append("(")
                .append(className)
                .append("::*")
                .append(function.name.toLower())
                .append("_member)(")
                .append(
                        function.functionType.parameters.stream()
                                .skip(1)
                                .map(p -> toCppParam(p))
                                .collect(Collectors.joining(", ")))
                .append(")")
                .append(" ")
                .append(function.functionType.parameters.get(0).type.isConst() ? "const " : "");
        return builder.toString();
    }

    /** Create a string that is the parameters of the class vtable constructor. */
    public String vtableCtorArguments(ClassDecl classDecl) {
        return classDecl.vtable.stream()
                .map(m -> memberFunctionPointerVariable(m, classDecl.getLastName().toPascal()))
                .collect(Collectors.joining(", "));
    }
}
