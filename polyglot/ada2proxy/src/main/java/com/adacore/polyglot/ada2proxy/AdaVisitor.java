package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.AdaNode;
import com.adacore.libadalang.Libadalang.SubpSpec;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.proxy.Declaration;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Module;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Visitor class to analyze Ada spec files and generate the proxy. */
public class AdaVisitor extends Libadalang.DefaultVisitor<Void> {

    /** Maps Ada declarations to Reference. */
    private static Map<Libadalang.BasicDecl, Reference> referenceMap = new HashMap<>();

    /** Turn a fully qualified name into a unique C symbol. */
    private static String symbolify(String fullyQualName) {
        return "__" + fullyQualName.replace(".", "_");
    }

    /** Return a Reference from an Ada BasicDecl. */
    private static Reference makeReference(Libadalang.BasicDecl decl) {
        // If the type has already been processed, return its memoized value.
        if (referenceMap.containsKey(decl)) return referenceMap.get(decl);

        Reference res = null;

        if (decl instanceof Libadalang.SubtypeDecl typeDecl)
            decl = typeDecl.pRootType(Libadalang.AdaNode.NONE);

        // If the BasicDecl was declared in the Standard Package, it is a builtin type.
        if (decl instanceof Libadalang.TypeDecl typeDecl
                && decl.getUnit().equals(decl.pStandardUnit())) {
            // TODO: Handle all builtin types

            // ``Standard.Boolean`` maps to BOOL.
            if (typeDecl.equals(typeDecl.pBoolType())) res = NativeType.BOOL.reference;
            else if (typeDecl.pIsIntType(Libadalang.AdaNode.NONE)) {
                // Is there a way to know the max bounds of an integer type?
                if (typeDecl.fTypeDef() instanceof Libadalang.SignedIntTypeDef)
                    res = NativeType.SINT32.reference;
                else res = NativeType.UINT32.reference;
            } else throw new UnsupportedOperationException("Type not supported yet.");
        } else {
            // TODO: Handle user defined types.
            throw new UnsupportedOperationException("Type not supported yet.");
        }

        // Memoize the value.
        referenceMap.put(decl, res);
        return res;
    }

    /** List of declarations declared by the module */
    private List<Declaration> declarations = new ArrayList<>();

    /** Name of the spec being analyzed */
    private Name moduleName = null;

    /** Parent package of the spec being analyzed */
    private Reference moduleParent = null;

    /** Analyse an Ada specification file. */
    public Module analyzeSpec(Libadalang.AnalysisUnit unit) {
        // Reset the previous values
        declarations = new ArrayList<>();
        moduleName = null;
        moduleParent = null;

        // Visit the AST
        unit.getRoot().accept(this);

        // Return the module.
        return new Module(moduleName, declarations, moduleParent);
    }

    @Override
    protected Function<AdaNode, Void> createDefaultBehavior() {
        return (node) -> {
            Stream.of(node.children())
                    .filter(Predicate.not(Libadalang.AdaNode::isNone))
                    .forEach((c) -> c.accept(this));
            return null;
        };
    }

    @Override
    public Void visit(Libadalang.CompilationUnit node) {
        node.fBody().accept(this);
        return null;
    }

    @Override
    public Void visit(Libadalang.LibraryItem node) {
        if (!node.fHasPrivate().pAsBool()) {
            node.fItem().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Libadalang.PackageDecl node) {
        node.fPublicPart().fDecls().accept(this);

        Libadalang.Symbol[] names = node.fPackageName().pFullyQualifiedNameArray();
        // The name of the current package is the last symbol in the array.
        moduleName = Name.fromLower(names[names.length - 1].text);

        // The rest of the modules are the parents
        Reference moduleParent = null;
        for (int i = names.length - 2; i >= 0; i--) {
            moduleParent =
                    new Reference(
                            Name.fromLower(names[i].text),
                            Reference.ReferenceKind.MODULE,
                            moduleParent,
                            false,
                            false,
                            false);
        }
        this.moduleParent = moduleParent;

        return null;
    }

    @Override
    public Void visit(Libadalang.SubpDecl node) {
        SubpSpec spec = node.fSubpSpec();

        // Get the name of the function.
        Name name = Name.fromPascalWithUnderscore(spec.pName().getText());

        // Get the C symbol of the function.
        String symbol = symbolify(node.pFullyQualifiedName());

        // TODO: Handle subprograms that can be attached to a type.
        Role role = null;

        // TODO: Get the list of parameters.
        List<Parameter> parameters = new ArrayList<>();

        Libadalang.BaseTypeDecl adaRetType = spec.pReturnType(Libadalang.AdaNode.NONE);
        final Reference returnType;
        // If there is no return type, the subprogram is a procedure: use VOID.
        if (adaRetType.isNone()) returnType = NativeType.VOID.reference;
        else returnType = makeReference(adaRetType);

        // TODO: Handle the Function's boolean attributes.

        FunctionDecl funDecl =
                new FunctionDecl(
                        name,
                        node.pDoc(),
                        role,
                        symbol,
                        parameters,
                        returnType,
                        Owner.UNKNOWN,
                        false,
                        false,
                        false);
        declarations.add(funDecl);
        return null;
    }
}
