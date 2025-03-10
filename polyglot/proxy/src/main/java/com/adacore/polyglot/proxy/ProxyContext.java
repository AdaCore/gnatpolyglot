package com.adacore.polyglot.proxy;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import com.adacore.polyglot.proxy.Role.RoleKind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProxyContext {

    /** Class to store the member function of a type. */
    private static class FunctionMembersEntry {
        /** Function to allocate the type. */
        FunctionDecl allocFunction = null;

        /** Function to free the type. */
        FunctionDecl freeFunction = null;

        /** List of all other member functions of the type (getters, setters, methods...). */
        ArrayList<FunctionDecl> membersFunction = new ArrayList<>();
    }

    /** Map a reference to its corresponding module. */
    private Map<Reference, Module> modules = new HashMap<>();

    /** Map a reference to its corresponding type. */
    private Map<Reference, TypeDecl> types = new HashMap<>();

    /** Map a type to its member functions. */
    private Map<TypeDecl, FunctionMembersEntry> membersEntries = new HashMap<>();

    public ProxyContext() {
        for (var nativeType : NativeType.values())
            types.put(nativeType.reference, nativeType.declaration);
    }

    /** Combine two references. */
    private Reference append(Reference prefix, Reference suffix) {
        if (suffix == null) return prefix;
        if (prefix == null) return suffix;
        suffix = append(prefix.suffix, suffix);
        return new Reference(
                prefix.name,
                prefix.kind,
                suffix,
                prefix.isPointer,
                prefix.isConst,
                prefix.isNonNull);
    }

    public Reference getReference(Declaration declaration) {
        return types.entrySet().stream()
                .filter(e -> e.getValue() == declaration)
                .findFirst()
                .get()
                .getKey();
    }

    public Reference getReference(Module module) {
        return modules.entrySet().stream()
                .filter(e -> e.getValue() == module)
                .findFirst()
                .get()
                .getKey();
    }

    /** Make a reference to the module. */
    private Reference makeReference(Module module) {
        Reference res = new Reference(module.name, ReferenceKind.MODULE, null, false, false, false);
        return append(module.parent, res);
    }

    /**
     * Make a reference to the declaration. Functions cannot be refered and will always return null.
     */
    private Reference makeReference(Module module, TypeDecl type) {
        Reference res = new Reference(type.name, ReferenceKind.CLASS, null, false, false, false);
        return append(makeReference(module), res);
    }

    /**
     * Register the module in the context and return true if there was no mdule with the same name
     * and parent, else return false.
     */
    public boolean register(Module module) {
        return modules.put(makeReference(module), module) == null;
    }

    /**
     * Register the function as a member function if it has a role, otherwise do nothing. If the
     * role is ``ALLOC`` or ``FREE``, and a function of that role was already registered for the
     * type, return false.
     */
    public boolean register(FunctionDecl func) {
        if (func.role == null) {
            return true;
        }
        TypeDecl decl = this.getTypeDecl(func.role.type);
        if (decl == null) {
            // The goal of the function is to detect when a function's role was already occupied by
            // an other. checking if the type exists is done somewhere is in the validation of the
            // proxy.
            return true;
        }
        FunctionMembersEntry entry = membersEntries.get(decl);
        if (func.role.kind == RoleKind.ALLOC) {
            if (entry.allocFunction != null) return false;
            entry.allocFunction = func;
        } else if (func.role.kind == RoleKind.FREE) {
            if (entry.freeFunction != null) return false;
            entry.freeFunction = func;
        } else if (func.role.kind == RoleKind.METHOD) {
            entry.membersFunction.add(func);
        }
        return true;
    }

    /**
     * Register the type in the context and return true if there was no type with the same name and
     * parent, else return false.
     */
    public boolean register(Module module, TypeDecl declaration) {
        membersEntries.put(declaration, new FunctionMembersEntry());
        return types.put(makeReference(module, declaration), declaration) == null;
    }

    /** Get a module from its corresponding reference. */
    public Module getModule(Reference ref) {
        return modules.get(ref);
    }

    /** Get a type declaration from its corresponding reference. */
    public TypeDecl getTypeDecl(Reference ref) {
        return types.get(ref);
    }

    /** Get the entry containing all member functions of the given type. */
    public FunctionMembersEntry getMembers(TypeDecl decl) {
        return membersEntries.get(decl);
    }
}
