//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.ExceptionDecl;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.PointerTypeExpr;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.TypeDecl;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy.VTableEntry;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.CppGenerator;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.DispatchParameterConverter;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.DispatchReturnConverter;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.ParameterGenerator;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.proxy2cpp.codegen.TypenameGenerator;
import java.nio.file.Path;
import java.util.ArrayList;
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

    private ParameterGenerator parameterConverter = new ParameterGenerator(this);

    private ReturnConverter returnConverter = new ReturnConverter(this);

    private DispatchReturnConverter dispatchReturnConverter = new DispatchReturnConverter(this);

    private DispatchParameterConverter dispatchParameterConverter =
            new DispatchParameterConverter(this);

    private TypenameGenerator typenameGenerator = new TypenameGenerator(this);

    public CppAPI(ProxyContext context, Path outputPath, Name projectName) {
        this.headerDir = outputPath.resolve("include");
        this.outputPath = outputPath;
        this.context = context;
        this.projectName = projectName;
    }

    public Name getProjectName() {
        return projectName;
    }

    public ProxyContext getContext() {
        return context;
    }

    public String formatDoc(String doc, int indent) {
        StringBuilder builder = new StringBuilder();
        doc = doc.replace("*/", "*\\/");
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
                return "gnatpolyglot::ada::strings::polyglot_string";
            case VOID:
                return "void";
            default:
                throw new UnsupportedOperationException(
                        nativeType.toString() + " is not handled yet");
        }
    }

    /** Return the refered C++ type's name. */
    public String cppTypename(TypeExpr typeExpr, boolean addLeadingColons) {
        return typenameGenerator.cppTypename(typeExpr, addLeadingColons);
    }

    public String cppTypename(TypeExpr typeExpr) {
        return cppTypename(typeExpr, true);
    }

    /**
     * Create a string that represents the C++ typename when returning a value of type `typeExpr`
     */
    public String cppReturnTypename(TypeExpr typeExpr) {
        return typenameGenerator.cppReturnTypename(typeExpr);
    }

    private static Set<Name> reservedGlobalEntities = Set.of(Name.fromLower("system"));

    public String lastNameToCppName(FullyQualifiedName fqn) {
        Name lastName = fqn.getLastName();
        if (context.getTypeDecl(fqn) != null) return lastName.toPascal();
        String res = toLower(lastName);
        if (fqn.names.size() == 1 && reservedGlobalEntities.contains(lastName)
                || CppKeyword.isKeyword(lastName)) res += "_";
        return res;
    }

    /** Return the refered C type's name. */
    public String cTypename(TypeExpr typeExpr) {
        return typenameGenerator.cTypename(typeExpr);
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
            builder.append(builder.isEmpty() ? "" : ", ")
                    .append("void *_self, void *_self_data, void *vtable");
        }
        return builder.toString();
    }

    public boolean isStringOrArray(TypeExpr typeExpr) {
        return typeExpr.isArray() || context.isStringType(typeExpr);
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
        return parameterConverter.buildConversion(p);
    }

    public String getParamForConstructor(Parameter p) {
        TypeExpr type = p.type.referencedType();
        if (type.isPointer()) {
            String name = toLower(p.name);
            return CppGenerator.makeTernary(
                            name.concat(".get() == nullptr"),
                            nullValue((PointerTypeExpr) type),
                            name.concat("->data_()"))
                    .toString();
        }
        return getParamForCall(p);
    }

    /** Create a call to the C symbol of the funtion. */
    public String callCSymbol(FunctionDecl functionDecl) {
        ArrayList<String> parameters = new ArrayList<>();
        boolean funcIsMethod = isMethod(functionDecl);
        // If the function is attached to a type, use ``this->data`` as the first argument.
        if (funcIsMethod) {
            TypeExpr type = functionDecl.type.parameters.get(0).type;
            if (functionDecl.role.type.isArray()
                    && type.isReference()
                    && type.referencedType().isPointer()) parameters.add("&this->_data");
            else parameters.add("this->_data");
        }

        RoleKind roleKind = functionDecl.role != null ? functionDecl.role.kind : null;

        functionDecl.type.parameters.stream()
                .skip(funcIsMethod ? 1 : 0)
                .map(
                        roleKind == RoleKind.ALLOC || roleKind == RoleKind.SHADOW_ALLOC
                                ? p -> getParamForConstructor(p)
                                : p -> getParamForCall(p))
                .collect(() -> parameters, ArrayList::add, ArrayList::addAll);
        if (roleKind == RoleKind.SHADOW_ALLOC) {
            String className = functionDecl.role.type.getName().getLastName().toPascal();
            // C++ does not need additional data in the shadow object.
            parameters.add("nullptr");
            parameters.add("_self");
            parameters.add("&%s_vtable".formatted(className));
        }
        return CppGenerator.makeCall(functionDecl.symbol, parameters).toString();
    }

    /** Create a string of the return statement. */
    public String makeReturnStatement(FunctionDecl functionDecl, String returnedValue) {
        return returnConverter.build(functionDecl, returnedValue);
    }

    public String cppOwner(Owner owner) {
        return "gnatpolyglot::memory_owner::" + owner.toString();
    }

    public String functionName(FunctionDecl functionDecl) {
        Name lastName = functionDecl.name.getLastName();
        // An operator function must have at least one function parameter or implicit object
        // parameter whose type is a class, a reference to a class, an enumeration, or a reference
        // to an enumeration.
        Predicate<Parameter> predicate = null;
        predicate =
                (p) -> {
                    TypeExpr typeExpr = p.type.referencedType();
                    if (typeExpr.isName()) {
                        TypeDecl typeDecl = context.getTypeDecl(typeExpr.getName());
                        return typeDecl instanceof EnumerationDecl
                                || typeDecl instanceof ClassDecl
                                || context.isStringType(typeExpr);
                    }
                    // Arrays and pointers are binded as classes, so it works
                    return typeExpr.isArray() || typeExpr.isPointer();
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
        builder.append("void *,");
        builder.append("gnatpolyglot::data *_self");
        for (var param : function.parameters.stream().skip(1).toList()) {
            builder.append(", ").append(toCParam(param));
        }
        return builder.toString();
    }

    /** Return a string that is the C++ converted parameter from its raw data given by argument. */
    public String convertForDispatch(Parameter param) {
        return dispatchParameterConverter.buildConversion(param);
    }

    /** Create a dispatching call to the member function set in the vtable's extra data. */
    public String callDispatch(VTableEntry function) {
        return CppGenerator.makeCall(
                        "__self->".concat(function.name.toLower()),
                        function.functionType.parameters.stream()
                                .skip(1)
                                .map(p -> toLower(p.name))
                                .map(DispatchParameterConverter::getConverterValue)
                                .toList())
                .toString();
    }

    /**
     * Create a call to ``.release_()`` on objects returned by the dispatchers when the value
     * returned is an object in order to prevent the destructor from freeing the memory returned.
     */
    public String makeReturnFromDispatch(FunctionTypeExpr functionType, String returnedValue) {
        return dispatchReturnConverter.buildReturn(functionType, returnedValue);
    }

    public boolean returnsVoid(FunctionTypeExpr functionType) {
        return functionType.returnType.isName()
                && context.getTypeDecl(functionType.returnType.getName())
                        .equals(NativeType.VOID.declaration);
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
     * <p>When an exception is caught, its information is moved to the GNATpolyglot kernel to be
     * analyzed back by the binded library. The exception is not rethrown in the catch statement,
     * and a return statement is necessary for code validity, even though its returned value should
     * not be used by the library.
     */
    public String makeDispatchDefaultReturn(FunctionTypeExpr function) {
        return dispatchReturnConverter.buildDefaultReturn(function);
    }

    public String verifyOwnership(Parameter param) {
        return parameterConverter.buildPointerOwnershipCheck(param);
    }

    public String createPointerBuffer(Parameter param) {
        return parameterConverter.buildPointerBuffer(param);
    }

    public String checkPointerValue(Parameter param) {
        return parameterConverter.buildPointerValueUpdate(param);
    }

    public String checkDispatchPointerValue(Parameter param) {
        return dispatchParameterConverter.buildPointerValueUpdate(param);
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
        if (typeExpr.pointedType().isArray())
            return "gnatpolyglot::ada::arrays::array_data{1, 0, nullptr}";
        if (context.isStringType(typeExpr.pointedType()))
            return "gnatpolyglot::ada::strings::string_data{1, 0, nullptr}";
        return "nullptr";
    }

    public String getEnumSizeType(EnumerationDecl enumDecl) {
        return nativeTypeName(enumDecl.representationType());
    }
}
