package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.NativeType.NativeTypeDecl;
import com.adacore.polyglot.proxy.ArrayTypeExpr;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.EnumerationDecl;
import com.adacore.polyglot.proxy.ExceptionDecl;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.NameTypeExpr;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.PointerTypeExpr;
import com.adacore.polyglot.proxy.ProxyContext;
import com.adacore.polyglot.proxy.ReferenceTypeExpr;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import com.adacore.polyglot.proxy.TypeDecl;
import com.adacore.polyglot.proxy.TypeExpr;
import com.adacore.polyglot.proxy.VTableEntry;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CppAPI {

    /** Context of the proxy. */
    private ProxyContext context;

    /** Output directory */
    private Path outputPath;

    /** Directory in which the headers will be written. */
    private Path headerDir;

    /** Name of the project . */
    private Name projectName;

    public CppAPI(ProxyContext context, Path outputPath, Name projectName) {
        this.headerDir = outputPath.resolve("include");
        this.outputPath = outputPath;
        this.context = context;
        this.projectName = projectName;
    }

    public Name getProjectName() {
        return projectName;
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

    /**
     * Return the result of toLower on the given name, appending an underscore at the end if the
     * result conflicts with a C++ keyword.
     */
    public String toLower(Name name) {
        return name.toLower().concat(CppKeyword.isKeyword(name) ? "_" : "");
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
            case CHAR:
                return "char";
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
    public String cppTypename(TypeExpr typeExpr, boolean addLeadingColons) {
        String prefix = addLeadingColons ? "::" : "";
        if (typeExpr instanceof NameTypeExpr name) {
            TypeDecl typeDecl = context.getTypeDecl(name.name);
            if (typeDecl instanceof NativeTypeDecl nativeType)
                return nativeTypeName(nativeType.nativeType);
            if (typeDecl instanceof ClassDecl || typeDecl instanceof EnumerationDecl)
                return name.name.join(fqn -> lastNameToCppName(fqn), prefix, "::", "");
        } else if (typeExpr instanceof ArrayTypeExpr array) {
            return prefix
                    + "polyglot::ada::arrays::polyglot_array<"
                    + cppTypename(array.typeExpr)
                    + ">";
        } else if (typeExpr instanceof ReferenceTypeExpr ref) {
            StringBuilder builder = new StringBuilder();
            if (ref.isConst) builder.append("const ");
            builder.append(cppTypename(ref.typeExpr)).append(" &");
            return builder.toString();
        } else if (typeExpr instanceof PointerTypeExpr pointer) {
            StringBuilder builder = new StringBuilder(prefix).append("polyglot::polyglot_ptr<");
            if (pointer.isConst) builder.append("const ");
            builder.append(cppTypename(pointer.typeExpr)).append(">");
            return builder.toString();
        }
        throw new UnsupportedOperationException("Unsupported Cpp type");
    }

    public String cppTypename(TypeExpr typeExpr) {
        return cppTypename(typeExpr, true);
    }

    /**
     * Create a string that represents the C++ typename when returning a value of type `typeExpr`
     */
    public String cppReturnTypename(TypeExpr typeExpr) {
        // When returning a reference to a non-native type, we cannot allocate a new proxy object
        // and return a real C++ reference to it. Instead, return a `view` to the returned pointer
        // that acts as a reference and that won't free the underlying pointer when destroyed.
        if (typeExpr instanceof ReferenceTypeExpr ref && !context.isNativeScalar(ref.typeExpr)) {
            String constness = ref.isConst ? "const " : "";
            return constness.concat(cppTypename(ref.typeExpr)).concat("::view");
        }
        return cppTypename(typeExpr);
    }

    private static Set<Name> reservedGlobalEntities = Set.of(Name.fromLower("system"));

    private String lastNameToCppName(FullyQualifiedName fqn) {
        Name lastName = fqn.getLastName();
        if (context.getTypeDecl(fqn) != null) return lastName.toPascal();
        String res = toLower(lastName);
        if (fqn.names.size() == 1 && reservedGlobalEntities.contains(lastName)
                || CppKeyword.isKeyword(lastName)) res += "_";
        return res;
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
            if (typeDecl instanceof EnumerationDecl) return nativeTypeName(NativeType.SINT32);
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
            if (!ref.isConst && ref.typeExpr instanceof PointerTypeExpr ptr) {
                if (context.isClassType(ptr.typeExpr)) return constness + "void **";
            }
            if (ref.isConst
                    && ref.typeExpr instanceof PointerTypeExpr ptr
                    && ptr.typeExpr instanceof ArrayTypeExpr) {
                return cTypename(ptr.typeExpr);
            }
            return constness + "void *";
        } else if (typeExpr instanceof PointerTypeExpr ptr) {
            String constness = ptr.isConst ? "const " : "";
            if (isStringOrArray(ptr.typeExpr)) return constness + cTypename(ptr.typeExpr);
            return constness + "void *";
        }
        throw new UnsupportedOperationException("Unsupported C type");
    }

    /** Create the string of the C++ namespace of the corresponding module. */
    public String namespacePath(Module module) {
        return module.name.join(this::lastNameToCppName, "", "::", "");
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
        builder.append(cTypename(parameter.type)).append(" ").append(toLower(parameter.name));
        return builder.toString();
    }

    /** Create a string corresponding to the C++ parameter. */
    private String toCppParam(Parameter parameter) {
        StringBuilder builder = new StringBuilder();
        builder.append(cppTypename(parameter.type)).append(" ").append(toLower(parameter.name));
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

    public boolean isStringOrArray(TypeExpr typeExpr) {
        return typeExpr instanceof ArrayTypeExpr || context.isStringType(typeExpr);
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
        // The SHADOW_ALLOC function has one hidden argument:
        //  - The self argument of the class's raw pointer type.
        if (functionDecl.role != null && functionDecl.role.kind == RoleKind.SHADOW_ALLOC) {
            String className = functionDecl.role.type.getName().getLastName().toPascal();
            builder.append(builder.isEmpty() ? "" : ", ").append(className).append(" *_self");
            ;
        }
        return builder.toString();
    }

    /** Return a string that gets the value of a parameter for the call to the Ada subprogram */
    public String getParamForCall(Parameter p) {
        String lower = toLower(p.name);
        if (p.type instanceof NameTypeExpr name
                && context.getTypeDecl(name.name) instanceof EnumerationDecl)
            return "static_cast<int>(" + lower + ")";
        // When the parameter is a reference to a scalar, get
        // the correspondign address.
        if (p.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof NameTypeExpr name) {
            if (context.getTypeDecl(name.name) instanceof NativeTypeDecl nat)
                return switch (nat.nativeType) {
                    case STRING -> lower + ".data()";
                    default -> "&" + lower;
                };
            if (context.getTypeDecl(name.name) instanceof EnumerationDecl) return "&" + lower;
        }
        if (p.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof PointerTypeExpr)
            return (ref.isConst ? "" : "&") + "_" + lower + "_data";
        if (p.type instanceof PointerTypeExpr) return "_" + lower + "_data";
        if ((p.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof ArrayTypeExpr)
                || p.type instanceof ArrayTypeExpr
                || context.getTypeDecl(p.type.getName()) instanceof ClassDecl)
            return lower + ".data()";
        return lower;
    }

    public String getParamForConstructor(Parameter p) {
        PointerTypeExpr ptrType = null;
        if (p.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof PointerTypeExpr ptr)
            ptrType = ptr;
        else if (p.type instanceof PointerTypeExpr ptr) ptrType = ptr;
        if (ptrType != null) {
            String name = toLower(p.name);
            return "%s.get() == nullptr ? %s : %s->data()"
                    .formatted(name, nullValue(ptrType), name);
        }
        return getParamForCall(p);
    }

    /** Create a call to the C symbol of the funtion. */
    public String callCSymbol(FunctionDecl functionDecl) {
        StringBuilder builder = new StringBuilder();
        boolean funcIsMethod = isMethod(functionDecl);
        builder.append(functionDecl.symbol).append("(");
        // If the function is attached to a type, use ``this->data`` as the first argument.
        if (funcIsMethod) {
            if (functionDecl.role.type instanceof ArrayTypeExpr
                    && functionDecl.type.parameters.get(0).type instanceof ReferenceTypeExpr ref
                    && ref.typeExpr instanceof PointerTypeExpr) builder.append("&");
            builder.append("this->_data");
            if (functionDecl.type.parameters.size() > 1) builder.append(", ");
        }

        RoleKind roleKind = functionDecl.role != null ? functionDecl.role.kind : null;

        builder.append(
                functionDecl.type.parameters.stream()
                        .skip(funcIsMethod ? 1 : 0)
                        .map(
                                roleKind == RoleKind.ALLOC || roleKind == RoleKind.SHADOW_ALLOC
                                        ? p -> getParamForConstructor(p)
                                        : p -> getParamForCall(p))
                        .collect(Collectors.joining(", ")));
        if (roleKind == RoleKind.SHADOW_ALLOC) {
            String className = functionDecl.role.type.getName().getLastName().toPascal();
            builder.append(functionDecl.type.parameters.isEmpty() ? "" : ", ")
                    .append("_self, &")
                    .append(className)
                    .append("_vtable");
        }
        builder.append(")");
        return builder.toString();
    }

    /** Create a string of the return statement. */
    public String makeReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        StringBuilder builder = new StringBuilder("return ");
        if (functionDecl.type.returnType instanceof ReferenceTypeExpr ref) {
            if (context.isNativeScalar(ref.typeExpr)) {
                // When returning a reference to a native type, get the address returned by the
                // `extern "C"` function and dereference it to create a reference.
                // Note that unlike class types that require a wrapping proxy objects, native types
                // can be directely addressed, thus we can return real C++ references.
                builder.append("*");
                if (ref.typeExpr instanceof NameTypeExpr name
                        && context.getTypeDecl(name.name) instanceof EnumerationDecl) {
                    // Scoped enums cannot be instantiated with a list initializer until C++17. Use
                    // a static_cast instead.
                    builder.append("static_cast<")
                            .append(ref.isConst ? "const " : "")
                            .append(cppTypename(name))
                            .append("*>");
                }
            } else {
                // When the returned pointer to an opaque data is `const void *`, we must return a
                // const view.
                // The view constructor cannot accept const pointers, since the concept of "const
                // constructor" does not exist. Instead, use a `create` function that has an
                // overload for returning const views with `const void *` data pointers.
                ReferenceTypeExpr nonConstRef = ref.typeExpr.makeReference(false);
                builder.append(cppReturnTypename(nonConstRef)).append("::create");
            }
        } else if (functionDecl.type.returnType instanceof NameTypeExpr name
                && context.getTypeDecl(name.name) instanceof EnumerationDecl) {
            // Scoped enums cannot be instantiated with a list initializer until C++17. Use a
            // static_cast instead.
            builder.append("static_cast<")
                    .append(cppReturnTypename(functionDecl.type.returnType))
                    .append(">");
        } else if (functionDecl.type.returnType instanceof PointerTypeExpr ptr) {
            builder.append(cppReturnTypename(functionDecl.type.returnType));
            returnedValue =
                    "%s%s == nullptr ? nullptr : new %s(%s), %s"
                            .formatted(
                                    returnedValue,
                                    isStringOrArray(ptr.typeExpr) ? ".data" : "",
                                    cppTypename(ptr.typeExpr),
                                    returnedValue,
                                    cppOwner(functionDecl.type.returnOwner));
        } else if (!context.isNativeScalar(functionDecl.type.returnType)) {
            // Otherwise, create a new object that wraps the returned pointer.
            builder.append(cppReturnTypename(functionDecl.type.returnType));
        }
        builder.append("(").append(returnedValue).append(")");
        return builder.toString();
    }

    private String cppOwner(Owner owner) {
        return "polyglot::memory_owner::" + owner.toString();
    }

    public String functionName(FunctionDecl functionDecl) {
        Name lastName = functionDecl.name.getLastName();
        // An operator function must have at least one function parameter or implicit object
        // parameter whose type is a class, a reference to a class, an enumeration, or a reference
        // to an enumeration.
        Predicate<Parameter> predicate = null;
        predicate =
                (p) -> {
                    TypeExpr typeExpr = p.type;
                    if (typeExpr instanceof ReferenceTypeExpr ref) typeExpr = ref.typeExpr;
                    if (typeExpr instanceof NameTypeExpr name) {
                        TypeDecl typeDecl = context.getTypeDecl(name.name);
                        return typeDecl instanceof EnumerationDecl
                                || typeDecl instanceof ClassDecl
                                || context.isStringType(typeExpr);
                    }
                    // Arrays and pointers are binded as classes, so it works
                    return typeExpr instanceof ArrayTypeExpr || typeExpr instanceof PointerTypeExpr;
                };
        if (functionDecl.type.parameters.stream().anyMatch(predicate)) {
            if (lastName.equals(Name.operatorPlus)) return "operator+";
            else if (lastName.equals(Name.operatorMinus)) return "operator-";
            else if (lastName.equals(Name.operatorMult)) return "operator*";
            else if (lastName.equals(Name.operatorDiv)) return "operator/";
            else if (lastName.equals(Name.operatorMod)) return "operator%";
            else if (lastName.equals(Name.operatorEq)) return "operator==";
            else if (lastName.equals(Name.operatorNe)) return "operator!=";
            else if (lastName.equals(Name.operatorLt)) return "operator<";
            else if (lastName.equals(Name.operatorLe)) return "operator<=";
            else if (lastName.equals(Name.operatorGt)) return "operator>";
            else if (lastName.equals(Name.operatorGe)) return "operator>=";
            else if (lastName.equals(Name.operatorBitAnd)) return "operator&";
            else if (lastName.equals(Name.operatorBitOr)) return "operator|";
            else if (lastName.equals(Name.operatorBitXor)) return "operator^";
            else if (lastName.equals(Name.operatorBitNot)) return "operator~";
        }
        return toLower(lastName);
    }

    /** Return the C++ name of the function to define ``functionDecl``. */
    public String functionDefinitionName(FunctionDecl functionDecl) {
        String functionName = functionName(functionDecl);

        if (!isMethod(functionDecl)) return functionName;
        StringBuilder builder = new StringBuilder();
        builder.append(cppTypename(functionDecl.role.type, false))
                .append("::")
                .append(functionName);
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
        builder.append("void *_self");
        for (var param : function.parameters.stream().skip(1).toList()) {
            builder.append(", ").append(toCParam(param));
        }
        return builder.toString();
    }

    /** Return a string that is the C++ converted parameter from its raw data given by argument. */
    public String convertForDispatch(Parameter param) {
        StringBuilder builder = new StringBuilder();
        TypeExpr type = param.type;
        String lower = toLower(param.name);
        String data = lower;
        // We need value types in cases where there are references, so use the
        // c++ return type in order to build view types when necessary.
        // Returning references to pointers is not possible, so manually enforce building simple
        // pointers.
        if (type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof PointerTypeExpr ptr) {
            builder.append(cTypename(ptr.typeExpr))
                    .append(isStringOrArray(ptr.typeExpr) ? "*" : "")
                    .append(" __")
                    .append(lower)
                    .append(" = ");
            if (isStringOrArray(ptr.typeExpr)) {
                builder.append("(").append(cTypename(ptr.typeExpr)).append("*)");
                data = "*__" + lower;
            } else {
                builder.append("*");
                data = "__" + lower;
            }
            builder.append(lower).append(";\n");
            type = ptr;
        }
        builder.append(cppReturnTypename(type)).append(" _").append(lower).append(" = ");

        if (type instanceof ReferenceTypeExpr ref
                && ref.typeExpr instanceof NameTypeExpr name
                && context.isNativeScalar(name)) {
            // When making a reference to a native type, dereference the pointer to make a reference
            builder.append("*");
        } else {
            // Otherwise, create a new object that wraps the returned pointer.
            if (type instanceof ReferenceTypeExpr ref)
                builder.append(cppReturnTypename(ref.typeExpr.makeReference(false)))
                        .append("::create");
            else builder.append(cppReturnTypename(type));
        }
        builder.append("(");
        if (type instanceof PointerTypeExpr ptr) {
            builder.append("new ").append(cppTypename(ptr.typeExpr)).append("(");
        }
        builder.append(data);
        if (type instanceof PointerTypeExpr) {
            builder.append(")");
        }
        builder.append(")");

        return builder.toString();
    }

    /** Create a dispatching call to the member function set in the vtable's extra data. */
    public String callDispatch(VTableEntry function) {
        StringBuilder builder = new StringBuilder();
        builder.append("__self->")
                .append(function.name.toLower())
                .append("(")
                .append(
                        function.functionType.parameters.stream()
                                .skip(1)
                                .map(p -> "_" + toLower(p.name))
                                .collect(Collectors.joining(", ")))
                .append(")");
        return builder.toString();
    }

    /**
     * Create a call to ``.release()`` on objects returned by the dispatchers when the value
     * returned is an object in order to prevent the destructor from freeing the memory returned.
     */
    public String makeReturnFromDispatch(FunctionTypeExpr functionType, String returnedValue) {
        StringBuilder builder = new StringBuilder();
        if (isStringOrArray(functionType.returnType)) {
            builder.append("return ").append(returnedValue).append(".release();");
        } else if (functionType.returnType instanceof NameTypeExpr name) {
            builder.append("return ");
            TypeDecl typeDecl = context.getTypeDecl(name.name);
            if (typeDecl instanceof NativeTypeDecl || typeDecl instanceof EnumerationDecl) {
                builder.append("static_cast<")
                        .append(cTypename(functionType.returnType))
                        .append(">(")
                        .append(returnedValue)
                        .append(");");
            } else {
                builder.append(returnedValue).append(".release();");
            }
        } else if (functionType.returnType instanceof PointerTypeExpr) {
            builder.append("if (")
                    .append(returnedValue)
                    .append(".get() == nullptr) {")
                    .append(makeDispatchDefaultReturn(functionType))
                    .append("} else { return ")
                    .append(returnedValue)
                    .append(".get()->data() ;}");
        } else {
            throw new UnsupportedOperationException("Unsupported returned dispatch type");
        }
        return builder.toString();
    }

    public boolean returnsVoid(FunctionTypeExpr functionType) {
        return functionType.returnType instanceof NameTypeExpr name
                && context.getTypeDecl(name.name).equals(NativeType.VOID.declaration);
    }

    public String exceptionFullyQualifiedName(ExceptionDecl exc) {
        return exc.name.join(n -> lastNameToCppName(n), "::", "::", "");
    }

    public String exceptionEnumIdentifier(ExceptionDecl exc) {
        return exc.name.join(n -> lastNameToCppName(n), "", "_", "_kind");
    }

    /**
     * Create a return statement that returns a default value for when a dynamically-dispatched
     * called function is exitted by a thrown exception.
     *
     * <p>When an exception is caught, its information is moved to the polyglot kernel to be
     * analyzed back by the binded library. The exception is not rethrown in the catch statement,
     * and a return statement is necessary for code validity, even though its returned value should
     * not be used by the library.
     */
    public String makeDispatchDefaultReturn(FunctionTypeExpr function) {
        if (function.returnType instanceof ArrayTypeExpr)
            return "return polyglot::ada::arrays::array_data{1, 0, nullptr};";
        if (function.returnType instanceof NameTypeExpr name) {
            TypeDecl returnType = context.getTypeDecl(name.name);
            if (returnType instanceof NativeTypeDecl nat) {
                return switch (nat.nativeType) {
                    case VOID -> "";
                    case BOOL -> "return false;";
                    case STRING -> "return polyglot::ada::strings::string_data{1, 0, nullptr};";
                    default -> "return 0;";
                };
            } else if (returnType instanceof EnumerationDecl) {
                return "return 0;";
            }
            return "return nullptr;";
        }
        return "return %s;".formatted(nullValue((PointerTypeExpr) function.returnType));
    }

    public String verifyOwnership(Parameter param) {
        StringBuilder builder = new StringBuilder();
        if ((param.type instanceof ReferenceTypeExpr ref && ref.typeExpr instanceof PointerTypeExpr
                        || param.type instanceof PointerTypeExpr)
                && param.transfer.required_owner != RequiredOwner.ANY) {
            String lower = toLower(param.name);
            builder.append("if (")
                    .append(lower)
                    .append(".get_owner() < polyglot::memory_owner::")
                    .append(param.transfer.required_owner.toString())
                    .append(") throw std::invalid_argument(")
                    .append("std::string(__FUNCTION__) + \": ")
                    .append(lower)
                    .append(": owner should be ")
                    .append(param.transfer.required_owner)
                    .append("\");");
        }
        return builder.toString();
    }

    public String createPointerBuffer(Parameter param) {
        StringBuilder builder = new StringBuilder();
        PointerTypeExpr ptrType = null;
        if (param.type instanceof PointerTypeExpr ptr) ptrType = ptr;
        else if (param.type instanceof ReferenceTypeExpr ref
                && ref.typeExpr instanceof PointerTypeExpr ptr) ptrType = ptr;

        if (ptrType != null) {
            String name = toLower(param.name);
            boolean isArray = ptrType.typeExpr instanceof ArrayTypeExpr;
            String dataType = cTypename(ptrType);

            builder.append(dataType)
                    .append(" _")
                    .append(name)
                    .append("_data = ")
                    .append(name)
                    .append(".get() == nullptr ? ")
                    .append(nullValue(ptrType))
                    .append(" : ")
                    .append(name)
                    .append(".get()->data();");
            // When the type is a reference, create a copy of the internal data to compare it after
            // the call as it may have been modified.
            if (param.type instanceof ReferenceTypeExpr ref && !ref.isConst) {
                builder.append(dataType)
                        .append(" __")
                        .append(name)
                        .append("_data = _")
                        .append(name)
                        .append("_data;");
            }
        }
        return builder.toString();
    }

    public String checkPointerValue(Parameter param) {
        StringBuilder builder = new StringBuilder();
        if (param.type instanceof ReferenceTypeExpr ref
                && !ref.isConst
                && ref.typeExpr instanceof PointerTypeExpr ptr) {
            String name = toLower(param.name);
            String dataName = "_" + name + "_data";
            String addressAccessor = isStringOrArray(ptr.typeExpr) ? ".data" : "";
            String copyName = "__" + name + "_data";
            builder.append("if (")
                    .append(dataName)
                    .append(addressAccessor)
                    .append(" != ")
                    .append(copyName)
                    .append(addressAccessor)
                    .append(") ")
                    .append(name)
                    .append(".reset(")
                    .append(dataName)
                    .append(addressAccessor)
                    .append("== nullptr ? nullptr : new ")
                    .append(cppTypename(ptr.typeExpr))
                    .append("(")
                    .append(dataName)
                    .append("), polyglot::memory_owner::LIBRARY);");
        }
        return builder.toString();
    }

    public String checkDispatchPointerValue(Parameter param) {
        StringBuilder builder = new StringBuilder();
        if (param.type instanceof ReferenceTypeExpr ref
                && ref.typeExpr instanceof PointerTypeExpr ptr) {
            String dataName = "_" + toLower(param.name);
            builder.append("*");
            if (isStringOrArray(ptr.typeExpr)) builder.append("__");
            builder.append(toLower(param.name))
                    .append(" = ")
                    .append(dataName)
                    .append(".get() == nullptr ? ")
                    .append(nullValue(ptr));
            builder.append(" : ").append(dataName).append("->data();");
        }
        return builder.toString();
    }

    public boolean isCopyable(ClassDecl classDecl) {
        return getMembers(classDecl).copyFunction != null;
    }

    public boolean isCopyConstructible(ClassDecl classDecl) {
        TypeExpr paramType = classDecl.name.asTypeExpr();
        List<TypeExpr> copyParams =
                List.of(paramType, paramType.makeReference(false), paramType.makeReference(true));
        return getMembers(classDecl).allocFunctions.stream()
                .filter(alloc -> alloc.type.parameters.size() == 1)
                .map(alloc -> alloc.type.parameters.getFirst().type)
                .anyMatch(funcParam -> copyParams.stream().anyMatch(p -> p.equals(funcParam)));
    }

    public String nullValue(PointerTypeExpr typeExpr) {
        if (typeExpr.typeExpr instanceof ArrayTypeExpr)
            return "polyglot::ada::arrays::array_data{1, 0, nullptr}";
        if (context.isStringType(typeExpr.typeExpr))
            return "polyglot::ada::strings::string_data{1, 0, nullptr}";
        return "nullptr";
    }

    public String getEnumSizeType(EnumerationDecl enumDecl) {
        int lastValue = enumDecl.items.getLast().value;
        if (lastValue < (1 << 8)) return nativeTypeName(NativeType.SINT8);
        if (lastValue < (1 << 16)) return nativeTypeName(NativeType.SINT16);
        if (lastValue < (1 << 32)) return nativeTypeName(NativeType.SINT32);
        if (lastValue < (1 << 64)) return nativeTypeName(NativeType.SINT64);
        throw new UnsupportedOperationException(
                "enumerations of size %d are not supported".formatted(lastValue));
    }
}
