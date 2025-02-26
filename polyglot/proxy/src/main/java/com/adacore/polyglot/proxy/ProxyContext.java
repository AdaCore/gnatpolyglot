package com.adacore.polyglot.proxy;

import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import java.util.HashMap;
import java.util.Map;

public class ProxyContext {

    /** Map a reference to its corresponding module. */
    private Map<Reference, Module> modules = new HashMap<>();

    /** Map a reference to its corresponding type. */
    private Map<Reference, Declaration> types = new HashMap<>();

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

    /** Make a reference to the module. */
    private Reference makeReference(Module module) {
        Reference res = new Reference(module.name, ReferenceKind.MODULE, null, false, false, false);
        return append(module.parent, res);
    }

    /**
     * Make a reference to the declaration. Functions cannot be refered and will always return null.
     */
    private Reference makeReference(Module module, Declaration type) {
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
     * Register the type in the context and return true if there was no type with the same name and
     * parent, else return false. Registering a function will do nothing and always return true.
     */
    public boolean register(Module module, Declaration declaration) {
        if (declaration instanceof FunctionDecl) return true;
        return types.put(makeReference(module, declaration), declaration) == null;
    }

    /** Get a module from its corresponding reference. */
    public Module getModule(Reference ref) {
        return modules.get(ref);
    }

    /** Get a declaration from its corresponding reference. */
    public Declaration getDeclaration(Reference ref) {
        return types.get(ref);
    }
}
