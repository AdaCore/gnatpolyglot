//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.gnatpolyglot.NativeType.NativeTypeDecl;
import com.adacore.gnatpolyglot.proxy.ProxyContext.FunctionMembersEntry;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ProxyContext {

    /** Class to store the member function of a type. */
    public static class FunctionMembersEntry {
        /** Functions to allocate the type. */
        public ArrayList<FunctionDecl> allocFunctions = new ArrayList<>();

        /** Function to free the type. */
        public FunctionDecl freeFunction = null;

        /** Function to copy the type. */
        public FunctionDecl copyFunction = null;

        /** List of all other member functions of the type (getters, setters, methods...). */
        public ArrayList<FunctionDecl> memberFunctions = new ArrayList<>();
    }

    /** Map a {@link FullyQualifiedName} to its corresponding {@link Module}. */
    private Map<FullyQualifiedName, Module> modules = new HashMap<>();

    /** Map a {@link FullyQualifiedName} to its corresponding {@link TypeDecl}. */
    private Map<FullyQualifiedName, TypeDecl> types = new HashMap<>();

    /** Map a type to its member functions. */
    private Map<TypeExpr, FunctionMembersEntry> membersEntries = new HashMap<>();

    /**
     * Map a ClassDecl to its direct childs.
     *
     * <p>The map is lazily loaded as {@link ProxyContext#getChildTypes} is called.
     */
    private Map<ClassDecl, List<ClassDecl>> childTypes = new HashMap<>();

    public ProxyContext() {
        for (var nativeType : NativeType.values())
            types.put(nativeType.typeExpr.getName(), nativeType.declaration);
    }

    /**
     * Register the module in the context and return true if there was no mdule with the same name
     * and parent, else return false.
     */
    public boolean register(Module module) {
        return modules.put(module.name, module) == null;
    }

    /**
     * Register the function as a member function if it has a role, otherwise do nothing. If the
     * role is ``FREE``, and a function of that role was already registered for the type, return
     * false.
     */
    public boolean register(FunctionDecl func) {
        if (func.role == null) {
            return true;
        }
        FunctionMembersEntry entry = membersEntries.get(func.role.type);
        if (entry == null) {
            entry = new FunctionMembersEntry();
            membersEntries.put(func.role.type, entry);
        }
        if (func.role.kind == RoleKind.ALLOC || func.role.kind == RoleKind.SHADOW_ALLOC) {
            // There can be multiple allocating function (no args, clone...)
            entry.allocFunctions.add(func);
        } else if (func.role.kind == RoleKind.FREE) {
            if (entry.freeFunction != null) return false;
            entry.freeFunction = func;
        } else if (func.role.kind == RoleKind.COPY) {
            if (entry.copyFunction != null) return false;
            entry.copyFunction = func;
        } else if (func.role.kind.compareTo(RoleKind.SETTER) <= 0) {
            entry.memberFunctions.add(func);
        }
        return true;
    }

    /**
     * Register the type in the context and return true if there was no type with the same name and
     * parent, else return false.
     */
    public boolean register(TypeDecl declaration) {
        membersEntries.put(declaration.name.asTypeExpr(), new FunctionMembersEntry());
        return types.put(declaration.name, declaration) == null;
    }

    /** Get a module from its corresponding name. */
    public Module getModule(FullyQualifiedName name) {
        return modules.get(name);
    }

    /** Get a type declaration from its corresponding name. */
    public TypeDecl getTypeDecl(FullyQualifiedName name) {
        return types.get(name);
    }

    /** Get the entry containing all member functions of the given type. */
    public FunctionMembersEntry getMembers(TypeExpr typeExpr) {
        return membersEntries.get(typeExpr);
    }

    public boolean isStringType(TypeExpr typeExpr) {
        return typeExpr instanceof NameTypeExpr name
                && getTypeDecl(name.name) instanceof NativeTypeDecl nativeType
                && nativeType.equals(NativeType.STRING.declaration);
    }

    public boolean isStringOrArray(TypeExpr typeExpr) {
        return typeExpr.isArray() || isStringType(typeExpr);
    }

    public boolean isNativeScalar(TypeExpr typeExpr) {
        return typeExpr instanceof NameTypeExpr name
                && (getTypeDecl(name.name) instanceof EnumerationDecl
                        || getTypeDecl(name.name) instanceof NativeTypeDecl nativeType
                                && !nativeType.equals(NativeType.STRING.declaration));
    }

    public boolean isClassType(TypeExpr typeExpr) {
        return typeExpr instanceof NameTypeExpr name && getTypeDecl(name.name) instanceof ClassDecl;
    }

    public boolean isException(TypeExpr typeExpr) {
        return typeExpr instanceof NameTypeExpr name
                && getTypeDecl(name.name) instanceof ExceptionDecl;
    }

    /**
     * Return whether the type requires to be identified in its when hierarchy when passed to the
     * user.
     */
    public boolean requiresIdentification(TypeExpr typeExpr) {
        return typeExpr.isName()
                && getTypeDecl(typeExpr.getName()) instanceof ClassDecl c
                && c.identificator != null;
    }

    /** Return a list of all the child type of the class, direct and indirect. */
    public List<ClassDecl> getChildTypes(ClassDecl classDecl) {
        List<ClassDecl> directChilds = childTypes.get(classDecl);
        if (directChilds == null) {
            // Get all the direct childs of the class and register it.
            directChilds =
                    types.values().stream()
                            .filter(
                                    t ->
                                            t instanceof ClassDecl c
                                                    && c.parent != null
                                                    && getTypeDecl(c.parent).equals(classDecl))
                            .map(ClassDecl.class::cast)
                            .toList();
            childTypes.put(classDecl, directChilds);
        }
        // Aggregate the child types recursively.
        return Stream.concat(
                        directChilds.stream(),
                        directChilds.stream().flatMap(c -> getChildTypes(c).stream()))
                .toList();
    }
}
