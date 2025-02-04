package com.adacore.polyglot.ada2proxy;

import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Reference;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Utility class that provides methods to help generate Ada code from a Proxy. */
public class AdaAPI {

    /** Return the fully qualified name of a name with its parents. */
    public static String makeFullyQualifiedName(Reference parent, Name name) {
        StringBuilder builder = new StringBuilder();
        while (parent != null) {
            builder.append(parent.name.toPascalWithUnderscore());
            builder.append(".");
            parent = parent.suffix;
        }
        builder.append(name.toPascalWithUnderscore());
        return builder.toString();
    }

    /**
     * Create the list of strings containing the interfaces generated from the json proxy for the
     * gpr project file.
     */
    public static String makeInterfaces(List<Module> modules) {
        return modules.stream()
                .map(
                        m -> {
                            return "\"%s.proxy\""
                                    .formatted(
                                            makeFullyQualifiedName(m.parent, m.name).toLowerCase());
                        })
                .collect(Collectors.joining(", "));
    }

    /** Return name with the correct Ada syntax with ``_Proxy`` as a suffix. */
    public static String proxyName(Name name) {
        return name.toPascalWithUnderscore() + "_Proxy";
    }

    /** Create a string to call a function from the proxy. */
    public static String call(FunctionDecl funDecl) {
        StringBuilder builder = new StringBuilder();
        builder.append(funDecl.name.toPascalWithUnderscore());
        return builder.toString();
    }

    /** Return the file name of a module with a given extension. */
    public static Path toAdaFilename(Module module, String suffix) {
        StringBuilder builder = new StringBuilder();
        Reference parent = module.parent;
        while (parent != null) {
            builder.append(parent.name.toLower());
            builder.append("-");
        }
        builder.append(module.name.toLower());
        if (suffix != null) builder.append(suffix);
        return Path.of(builder.toString());
    }
}
