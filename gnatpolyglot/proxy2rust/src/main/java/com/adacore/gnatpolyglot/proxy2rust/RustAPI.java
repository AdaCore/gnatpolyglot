//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2rust;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ClassDecl;
import com.adacore.gnatpolyglot.proxy.EnumerationDecl;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.FunctionTypeExpr;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.Owner;
import com.adacore.gnatpolyglot.proxy.Parameter;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry;
import com.adacore.gnatpolyglot.proxy.ReferenceTypeExpr;
import com.adacore.gnatpolyglot.proxy.Role;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2rust.codegen.ParameterConverter;
import com.adacore.gnatpolyglot.proxy2rust.codegen.ReturnConverter;
import com.adacore.gnatpolyglot.proxy2rust.codegen.RustGenerator;
import com.adacore.gnatpolyglot.proxy2rust.codegen.TypenameGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RustAPI extends LanguageAPI {

    private ProxyContext context;

    private Name projectName;

    private final TypenameGenerator typenameGenerator = new TypenameGenerator(this);
    private final ParameterConverter parameterConverter = new ParameterConverter(this);
    private final ReturnConverter returnConverter = new ReturnConverter(this);

    public RustAPI(ProxyContext context, Name projectName) {
        this.context = context;
        this.projectName = projectName;
    }

    public Name getProjectName() {
        return projectName;
    }

    /**
     * Return the absolute Rust path to the type (class or enum) named by {@code typeFqn}: a {@code
     * crate::<module>::<Name>} path with the module segment keyword-escaped. Always fully
     * qualified, so the reference is valid from any module; use {@link
     * #rustTypePath(FullyQualifiedName, Module)} when the referencing module is known and a
     * shorter, bare name is preferred.
     */
    public String rustTypePath(FullyQualifiedName typeFqn) {
        return rustTypePath(typeFqn, null);
    }

    /**
     * Return the Rust path to the type (class or enum) named by {@code typeFqn}, as seen from
     * {@code currentModule}. Returns the bare PascalCase name when the type lives in that module,
     * and otherwise (or when {@code currentModule} is {@code null}) the absolute {@code
     * crate::<module>::<Name>} path with the module segment keyword-escaped, since a type
     * referenced from another unit must be fully qualified.
     */
    public String rustTypePath(FullyQualifiedName typeFqn, Module currentModule) {
        var simpleName = typeFqn.getLastName().toPascal();
        var moduleFqn = typeFqn.getParentFullyQualifiedName();
        if (currentModule != null && moduleFqn != null && moduleFqn.equals(currentModule.name)) {
            return simpleName;
        }
        var moduleLastName = moduleFqn != null ? moduleFqn.getLastName().toLower() : "unknown";
        return "crate::" + rustEscape(moduleLastName) + "::" + simpleName;
    }

    public ProxyContext getContext() {
        return context;
    }

    /** Return whether the given module belongs to the gnatpolyglot internal namespace. */
    public boolean isGnatpolyglotModule(Module module) {
        return module.name.names.get(0).equals(Name.fromLower("gnatpolyglot"));
    }

    private static final Set<String> RUST_KEYWORDS =
            Set.of(
                    "abstract",
                    "as",
                    "async",
                    "await",
                    "become",
                    "box",
                    "break",
                    "const",
                    "continue",
                    "crate",
                    "do",
                    "dyn",
                    "else",
                    "enum",
                    "extern",
                    "false",
                    "final",
                    "fn",
                    "for",
                    "if",
                    "impl",
                    "in",
                    "let",
                    "loop",
                    "match",
                    "mod",
                    "move",
                    "mut",
                    "override",
                    "priv",
                    "pub",
                    "ref",
                    "return",
                    "self",
                    "static",
                    "struct",
                    "super",
                    "trait",
                    "true",
                    "try",
                    "type",
                    "typeof",
                    "unsafe",
                    "unsized",
                    "use",
                    "virtual",
                    "where",
                    "while",
                    "yield");

    /** Escape a Rust identifier that clashes with a reserved keyword using r# raw syntax. */
    public String rustEscape(String name) {
        return RUST_KEYWORDS.contains(name) ? "r#" + name : name;
    }

    /** Return the Rust module name for a module, escaping reserved keywords with r#. */
    public String rustModuleName(Module module) {
        return rustEscape(module.name.getLastName().toLower());
    }

    /** Return whether the given class declares a parent class. */
    public boolean hasParent(ClassDecl classDecl) {
        return classDecl.parent != null;
    }

    /**
     * Return the Rust type path for the parent of childClass as seen from {@code currentModule}:
     * the bare class name when the parent lives in that module, otherwise an absolute
     * crate::<module>::<ClassName> path with keyword escaping.
     */
    public String rustParentTypePath(ClassDecl childClass, Module currentModule) {
        return rustTypePath(childClass.parent, currentModule);
    }

    /** Return whether the function returns void. */
    public boolean returnsVoid(FunctionTypeExpr functionType) {
        return functionType.returnType.isName()
                && context.getTypeDecl(functionType.returnType.getName())
                        .equals(NativeType.VOID.declaration);
    }

    // ===== Support checks =====

    /** Return whether a TypeExpr is supported in the current implementation phase. */
    public boolean isSupported(TypeExpr typeExpr) {
        if (typeExpr.isReference()) return isSupported(typeExpr.referencedType());
        // A bare pointer (Ada access type) has no representation in the safe API yet: the
        // safe type-name generators reject it. Report it as unsupported so the whole function
        // is skipped, rather than letting generation fail partway.
        if (typeExpr.isPointer()) return false;
        // Arrays map to PolyglotArray<E>. A native scalar element is backed by the runtime's
        // PolyglotArrayElement impls; a record element is backed by a generated impl (see
        // recordArrayElements). Both are supported; other element kinds are not.
        if (typeExpr.isArray()) return isSupportedArrayElement(typeExpr.elementType());
        if (typeExpr.isName()) {
            var typeDecl = context.getTypeDecl(typeExpr.getName());
            if (typeDecl instanceof ClassDecl classDecl) {
                return classDecl.inheritability != ClassDecl.Inheritability.VIRTUAL;
            }
            return true;
        }
        return false;
    }

    /**
     * Return whether an array element type is a native scalar backed by the runtime. Every native
     * scalar the frontend can present as an array element has an PolyglotArrayElement impl in the
     * runtime, except STRING (an array whose element is itself a string). An array of Character is
     * classified as a string rather than a CHAR-element array, and the 128-bit widths are dropped
     * by the frontend before reaching here, so STRING is the only exclusion.
     */
    private boolean isNativeArrayElement(TypeExpr element) {
        return element.isName()
                && context.getTypeDecl(element.getName()) instanceof NativeTypeDecl nativeDecl
                && nativeDecl.nativeType != NativeType.STRING;
    }

    /**
     * Return whether an array element type is a non-virtual record (generated
     * PolyglotArrayElement).
     */
    private boolean isRecordArrayElement(TypeExpr element) {
        return element.isName()
                && context.getTypeDecl(element.getName()) instanceof ClassDecl classDecl
                && classDecl.inheritability != ClassDecl.Inheritability.VIRTUAL;
    }

    /**
     * Return whether an array element type maps to an {@code PolyglotArray<E>} the backend can
     * emit.
     */
    private boolean isSupportedArrayElement(TypeExpr element) {
        return isNativeArrayElement(element) || isRecordArrayElement(element);
    }

    /** Return whether all parameter and return types of a function are supported. */
    public boolean isFunctionSupported(FunctionDecl func) {
        if (!isSupported(func.type.returnType)) return false;
        for (var param : func.type.parameters) {
            if (!isSupported(param.type)) return false;
        }
        return true;
    }

    // ===== Type name generation =====

    /** Return the Rust enum name (PascalCase) for an EnumerationDecl. */
    public String rustEnumName(EnumerationDecl enumDecl) {
        return enumDecl.name.getLastName().toPascal();
    }

    /**
     * Return the Rust {@code #[repr(...)]} integer type matching the enumeration's native
     * representation. The width comes from {@link EnumerationDecl#representationType} so both
     * bindings agree on the ABI: a wider repr than the native side uses would corrupt adjacent
     * memory when the enum is accessed through a mutable reference across the FFI boundary.
     */
    public String rustEnumRepr(EnumerationDecl enumDecl) {
        return rustNativeTypename(enumDecl.representationType());
    }

    /** Return the Rust struct name (PascalCase) for a ClassDecl. */
    public String rustClassName(ClassDecl classDecl) {
        return classDecl.name.getLastName().toPascal();
    }

    /** Return the Rust type name for a TypeExpr in a safe context. */
    public String safeTypename(TypeExpr typeExpr) {
        return typenameGenerator.safeTypename(typeExpr);
    }

    /** Return the Rust type for a parameter (class NameTypeExpr becomes &ClassName). */
    public String rustParamType(TypeExpr typeExpr) {
        return safeTypename(typeExpr);
    }

    /** Return the Rust return type in the safe API, considering ownership. */
    public String rustReturnType(TypeExpr returnType, Owner owner) {
        return typenameGenerator.safeReturnTypename(returnType, owner);
    }

    /** Return the Rust type name for a TypeExpr in the unsafe FFI declaration. */
    public String ffiTypename(TypeExpr typeExpr) {
        return typenameGenerator.ffiTypename(typeExpr);
    }

    /** Return the Rust primitive type name for a native type. */
    public String rustNativeTypename(NativeType nativeType) {
        return switch (nativeType) {
            case VOID -> "()";
            case BOOL -> "bool";
            case CHAR -> "u8";
            case SINT8 -> "i8";
            case UINT8 -> "u8";
            case SINT16 -> "i16";
            case UINT16 -> "u16";
            case SINT32 -> "i32";
            case UINT32 -> "u32";
            case SINT64 -> "i64";
            case UINT64 -> "u64";
            case FLOAT32 -> "f32";
            case FLOAT64 -> "f64";
            default -> throw new UnsupportedOperationException(
                    nativeType.toString() + " is not yet supported in proxy2rust");
        };
    }

    // ===== Function signatures =====

    /** Return whether a function is a class method (has implicit self), i.e. role <= DESTRUCT. */
    public boolean isMethod(FunctionDecl functionDecl) {
        return functionDecl.role != null
                && functionDecl.role.kind.compareTo(Role.RoleKind.DESTRUCT) <= 0;
    }

    /**
     * Return the snake_case Rust name for the given declaration. Overloaded subprograms that share
     * a base name within the same Rust scope are disambiguated by {@link #moduleFreeFunctions} /
     * {@link #supportedMethods}; this returns the name chosen there, falling back to the plain base
     * name for declarations that were never run through disambiguation.
     */
    public String rustFunctionName(FunctionDecl functionDecl) {
        return functionNames.getOrDefault(functionDecl, baseFunctionName(functionDecl));
    }

    /**
     * Render documentation as a Rust doc comment, prefixing every line with {@code ///} so that
     * multi-line documentation stays a valid (and fully attached) doc comment. The first line's
     * indentation is supplied by the template; continuation lines are indented by {@code indent}
     * levels so the comment stays aligned with the item it documents (e.g. methods nested in an
     * {@code impl} block).
     */
    public String formatDoc(String doc, int indent) {
        var pad = "    ".repeat(indent);
        var builder = new StringBuilder();
        var lines = doc.lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) builder.append("\n").append(pad);
            var line = lines.get(i);
            builder.append(line.isBlank() ? "///" : "/// " + line);
        }
        return builder.toString();
    }

    // ===== Function name disambiguation =====

    /**
     * Rust has no function overloading, so subprograms that share a base name within the same scope
     * (a module's free functions, or a single class's methods) must be given distinct names. This
     * map records the disambiguated name chosen for each declaration. It is populated lazily by
     * {@link #moduleFreeFunctions} / {@link #supportedMethods} and read back by {@link
     * #rustFunctionName}.
     */
    private final Map<FunctionDecl, String> functionNames = new IdentityHashMap<>();

    /** Candidate Rust name for a function, before any overload disambiguation. */
    private String baseFunctionName(FunctionDecl functionDecl) {
        return functionDecl.name.getLastName().toLower();
    }

    /**
     * Assign distinct Rust names to the given functions, which together form a single Rust naming
     * scope. Rust has no overloading, so overloads that share a base name keep that name for the
     * first occurrence (in declaration order) and get a "_1", "_2", ... suffix for the rest. The
     * chosen names are recorded for later lookup by {@link #rustFunctionName}. The list is returned
     * for the templates to emit.
     */
    private List<FunctionDecl> assignNames(List<FunctionDecl> functions) {
        var occurrences = new HashMap<String, Integer>();
        for (var f : functions) {
            var base = baseFunctionName(f);
            int seen = occurrences.merge(base, 1, Integer::sum) - 1;
            functionNames.put(f, seen == 0 ? base : base + "_" + seen);
        }
        return functions;
    }

    /** Free functions of a module that get safe wrappers, with overload names disambiguated. */
    public List<FunctionDecl> moduleFreeFunctions(Module module) {
        var functions =
                module.declarations.stream()
                        .filter(FunctionDecl.class::isInstance)
                        .map(FunctionDecl.class::cast)
                        .filter(f -> f.role == null)
                        .filter(this::isFunctionSupported)
                        .collect(Collectors.toList());
        return assignNames(functions);
    }

    /** Member functions of a class that get safe methods, with overload names disambiguated. */
    public List<FunctionDecl> supportedMethods(FunctionMembersEntry members) {
        var functions =
                members.memberFunctions.stream()
                        .filter(this::isFunctionSupported)
                        .collect(Collectors.toList());
        return assignNames(functions);
    }

    /** Return the Rust return-type suffix for the safe wrapper (e.g. " -> Pair" or ""). */
    public String rustReturnTypeSuffix(FunctionDecl functionDecl) {
        if (returnsVoid(functionDecl.type)) return "";
        return " -> " + rustReturnType(functionDecl.type.returnType, functionDecl.type.returnOwner);
    }

    /** Return the Rust return-type suffix for the FFI declaration. */
    public String ffiReturnTypeSuffix(FunctionDecl functionDecl) {
        if (returnsVoid(functionDecl.type)) return "";
        return " -> " + ffiTypename(functionDecl.type.returnType);
    }

    /** Safe parameter name that avoids the Rust keyword "self" in FFI declarations. */
    private String safeFfiParamName(Name name) {
        return name.toLower().equals("self") ? "_self" : name.toLower();
    }

    /** Return the comma-separated Rust parameter list for the FFI declaration (all params). */
    public String ffiParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .map(p -> safeFfiParamName(p.name) + ": " + ffiTypename(p.type))
                .collect(Collectors.joining(", "));
    }

    /**
     * Return the comma-separated Rust parameter list for the safe wrapper. Skips the first
     * parameter (self) for member functions (role <= DESTRUCT).
     */
    public String rustParameters(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .skip(isMethod(functionDecl) ? 1 : 0)
                .map(p -> p.name.toLower() + ": " + rustParamType(p.type))
                .collect(Collectors.joining(", "));
    }

    // ===== Call argument generation =====

    /** Return the call argument expression for a single parameter. */
    public String callArg(Parameter p) {
        return parameterConverter.callArg(p);
    }

    /** Return the comma-separated call arguments for a function (skips self for member methods). */
    public String callArguments(FunctionDecl functionDecl) {
        return functionDecl.type.parameters.stream()
                .skip(isMethod(functionDecl) ? 1 : 0)
                .map(this::callArg)
                .collect(Collectors.joining(", "));
    }

    /** Return the full expression for a free function's FFI call, including return conversion. */
    public String ffiCallExpr(FunctionDecl func) {
        var rawCall = RustGenerator.ffiCall(func.symbol, callArguments(func));
        return returnConverter.wrapReturn(rawCall, func.type.returnType, func.type.returnOwner);
    }

    // ===== Class (impl) helpers =====

    /** Return "&self" or "&mut self" based on the first parameter's constness. */
    public String selfParam(FunctionDecl func) {
        var firstParam = func.type.parameters.get(0);
        return firstParam.type.isConst() ? "&self" : "&mut self";
    }

    /** Return the call argument expression for the implicit self parameter. */
    public String selfCallArg(FunctionDecl func) {
        var firstType = func.type.parameters.get(0).type;
        var selfPtr = RustGenerator.asPtr("self");
        if (firstType.isConst()) {
            return RustGenerator.cast(selfPtr, RustGenerator.cVoidPtr(true));
        }
        if (firstType.isPointer()) {
            return RustGenerator.cast(selfPtr, RustGenerator.cVoidPtr(false));
        }
        return selfPtr;
    }

    /** Return the full method signature parameter list ("&self" plus any other params). */
    public String methodSignatureParams(FunctionDecl func) {
        var selfPart = selfParam(func);
        var otherParts = rustParameters(func);
        return otherParts.isEmpty() ? selfPart : selfPart + ", " + otherParts;
    }

    /** Return the comma-separated FFI call arguments for a member function (self + others). */
    public String memberCallArguments(FunctionDecl func) {
        var selfArg = selfCallArg(func);
        var otherArgs =
                func.type.parameters.stream()
                        .skip(1)
                        .map(this::callArg)
                        .collect(Collectors.joining(", "));
        return otherArgs.isEmpty() ? selfArg : selfArg + ", " + otherArgs;
    }

    /** Return the full expression for a member function's FFI call, including return conversion. */
    public String memberFfiCallExpr(FunctionDecl func) {
        var rawCall = RustGenerator.ffiCall(func.symbol, memberCallArguments(func));
        return returnConverter.wrapReturn(rawCall, func.type.returnType, func.type.returnOwner);
    }

    // ===== Mutable field accessors =====

    /**
     * Whether a method is a field getter that should also expose a {@code _mut} accessor. The plain
     * getter returns a scalar/enum by copy; the {@code _mut} accessor hands out a real {@code &mut}
     * reference for in-place mutation. (Class fields are reached through their {@code ManuallyDrop}
     * view instead, so they are excluded.)
     */
    public boolean hasMutAccessor(FunctionDecl func) {
        if (func.role == null || func.role.kind != Role.RoleKind.GETTER) return false;
        var returnType = func.type.returnType;
        return returnType.isReference()
                && !returnType.isConst()
                && context.isNativeScalar(returnType.referencedType());
    }

    /** Among the given methods, the getters that also get a {@code _mut} accessor. */
    public List<FunctionDecl> mutAccessorGetters(List<FunctionDecl> methods) {
        return methods.stream().filter(this::hasMutAccessor).collect(Collectors.toList());
    }

    /** Name of the in-place mutable accessor for a field getter: {@code <getter>_mut}. */
    public String rustMutAccessorName(FunctionDecl getter) {
        return rustFunctionName(getter) + "_mut";
    }

    /** Return-type suffix of a {@code _mut} accessor, e.g. {@code " -> &mut i32"}. */
    public String mutAccessorReturnTypeSuffix(FunctionDecl getter) {
        var pointee = rustParamType(getter.type.returnType.referencedType());
        return " -> " + RustGenerator.reference(false, pointee);
    }

    /** Body expression of a {@code _mut} accessor: a {@code &mut} reference into the field. */
    public String mutAccessorExpr(FunctionDecl getter) {
        var rawCall = RustGenerator.ffiCall(getter.symbol, memberCallArguments(getter));
        return returnConverter.mutAccessorExpr(rawCall, getter.type.returnType.referencedType());
    }

    /**
     * Return the constructor name for an alloc function. Uses "new_default" for zero-param allocs,
     * "new" for the first non-empty alloc, and "new_N" for subsequent overloads.
     */
    public String rustConstructorName(FunctionDecl allocFunc, int index) {
        if (allocFunc.type.parameters.isEmpty()) return "new_default";
        return index == 0 ? "new" : "new_" + index;
    }

    /**
     * Return whether an alloc function is the clone constructor (first param: const ref to self).
     */
    public boolean isCloneAlloc(FunctionDecl func, ClassDecl classDecl) {
        if (func.type.parameters.isEmpty()) return false;
        var first = func.type.parameters.get(0);
        return first.type instanceof ReferenceTypeExpr ref
                && ref.isConst
                && ref.typeExpr.isName()
                && ref.typeExpr.getName().equals(classDecl.name);
    }

    /** Return the non-clone, non-shadow alloc functions for a class. */
    public List<FunctionDecl> regularAllocs(FunctionMembersEntry members, ClassDecl classDecl) {
        return members.allocFunctions.stream()
                .filter(f -> !isCloneAlloc(f, classDecl))
                .filter(f -> f.role.kind != Role.RoleKind.SHADOW_ALLOC)
                .collect(Collectors.toList());
    }

    /** Return the clone alloc function for a class, or null if absent. */
    public FunctionDecl findCloneAlloc(FunctionMembersEntry members, ClassDecl classDecl) {
        return members.allocFunctions.stream()
                .filter(f -> isCloneAlloc(f, classDecl))
                .findFirst()
                .orElse(null);
    }

    /** Return the function members entry for a class. */
    public FunctionMembersEntry getMembers(ClassDecl classDecl) {
        return context.getMembers(classDecl.name.asTypeExpr());
    }

    /**
     * Return the functions in a module that need an FFI declaration, excluding shadow_alloc
     * functions whose actual C signature has implicit extra parameters not present in the proxy IR.
     * Every other type is renderable in the FFI layer — arrays and strings as {@code array_data},
     * pointers and classes as {@code c_void} — so shadow_alloc is the only filter. The FFI layer is
     * broader than the safe API, so it keeps the class lifecycle functions (free/clone/alloc) that
     * the safe API never exposes directly but that the generated {@code Drop}/{@code
     * Clone}/constructor code still calls. A function with no safe wrapper just leaves an unused
     * (dead-code-allowed) FFI declaration.
     */
    public List<FunctionDecl> allFfiFunctions(Module module) {
        return module.declarations.stream()
                .filter(FunctionDecl.class::isInstance)
                .map(FunctionDecl.class::cast)
                .filter(f -> f.role == null || f.role.kind != Role.RoleKind.SHADOW_ALLOC)
                .collect(Collectors.toList());
    }

    // ===== Record-element arrays =====

    /**
     * The array accessor functions (alloc / clone / free / getter / setter) for arrays whose
     * element is this class's type, or {@code null} when the class is never used as an array
     * element.
     */
    public FunctionMembersEntry getArrayMembers(ClassDecl classDecl) {
        return context.getMembers(classDecl.name.asTypeExpr().makeArray());
    }

    /**
     * The non-virtual record classes used as an array element type, for which an {@code
     * PolyglotArrayElement} impl must be generated (see {@code arrays.jte}). gnatpolyglot-internal
     * modules are skipped, since their types are not generated.
     */
    public List<ClassDecl> recordArrayElements(List<Module> modules) {
        var elements = new ArrayList<ClassDecl>();
        for (var module : modules) {
            if (isGnatpolyglotModule(module)) continue;
            for (var decl : module.declarations) {
                if (decl instanceof ClassDecl classDecl
                        && classDecl.inheritability != ClassDecl.Inheritability.VIRTUAL) {
                    var members = getArrayMembers(classDecl);
                    if (members != null && members.freeFunction != null) elements.add(classDecl);
                }
            }
        }
        return elements;
    }

    /**
     * The allocator ({@code clone == false}) or cloner ({@code clone == true}) among an array's
     * alloc-role functions, for arrays of this element class. Both share the ALLOC role, so they
     * are told apart by whether they take the source array as a parameter: the cloner does, the
     * bounds allocator takes only scalar bounds. This mirrors how {@link #isCloneAlloc} splits a
     * class's own constructors. Its {@code symbol} is the Ada entry point the generated {@code
     * arrays.jte} binds.
     */
    public FunctionDecl arrayAllocFunction(ClassDecl classDecl, boolean clone) {
        return getArrayMembers(classDecl).allocFunctions.stream()
                .filter(
                        f ->
                                f.type.parameters.stream()
                                                .anyMatch(
                                                        p ->
                                                                p.type.isReference()
                                                                        && p.type.referencedType()
                                                                                .isArray())
                                        == clone)
                .findFirst()
                .orElseThrow();
    }

    /**
     * The element getter or setter ({@code kind}) among an array's member functions, for arrays of
     * this element class. Its {@code symbol} is the Ada entry point the generated {@code
     * arrays.jte} binds.
     */
    public FunctionDecl arrayMemberFunction(ClassDecl classDecl, Role.RoleKind kind) {
        return getArrayMembers(classDecl).memberFunctions.stream()
                .filter(f -> f.role.kind == kind)
                .findFirst()
                .orElseThrow();
    }
}
