package com.adacore.gnatpolyglot.proxy2java;

import com.adacore.gnatpolyglot.LanguageAPI;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.Module;
import com.adacore.gnatpolyglot.proxy.Name;
import com.adacore.gnatpolyglot.proxy.ProxyContext;
import com.adacore.gnatpolyglot.proxy.TypeExpr;
import com.adacore.gnatpolyglot.proxy2java.codegen.CGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.JavaGenerator;
import com.adacore.gnatpolyglot.proxy2java.codegen.TypenameGenerator;
import java.nio.file.Path;
import java.util.List;

public class JavaAPI extends LanguageAPI {

    private FullyQualifiedName groupId;

    private Name projectName;

    private ProxyContext context;

    private TypenameGenerator typenameGenerator = new TypenameGenerator(this);

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

    /** Return the path of the file to generate for a module. */
    public Path filepath(Module module) {
        Path p = Path.of(".");
        for (var n : module.name.names) {
            p = p.resolve(n.toLower());
        }
        return p.resolve(module.name.getLastName().toPascal().concat("Package.java"));
    }

    /** Return the Java name of a type (ie. the type exposed to the user). */
    public String javaTypename(TypeExpr type) {
        return typenameGenerator.javaTypename(type);
    }

    /** Return the native Java name of a type. */
    public String javaNativeTypename(TypeExpr type) {
        return typenameGenerator.javaNativeTypename(type);
    }

    /** Return the JNI name of a type. */
    public String jniTypename(TypeExpr type) {
        return typenameGenerator.jniTypename(type);
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
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
        // All native function handle names start with `$` (Unicode character: 00024)
        return builder.append("__00024").append(function.name.getLastName().toCamel()).toString();
    }

    /** Create a call to the C symbol in the JNI layer. */
    public String callCSymbol(FunctionDecl functionDecl) {
        return CGenerator.makeCall(functionDecl.symbol, List.of()).toString();
    }

    /** Create a call to the Java native function. */
    public String callJavaNative(FunctionDecl functionDecl, String nativeName) {
        return JavaGenerator.makeCall(nativeName, List.of()).toString();
    }
}
