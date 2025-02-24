package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.AdaNode;
import com.adacore.libadalang.Libadalang.SubpSpec;
import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Visitor class to analyze Ada spec files and generate the proxy. */
public class AdaVisitor extends Libadalang.DefaultVisitor<Void> {

    /** Turn a fully qualified name into a unique C symbol. */
    private static String symbolify(String fullyQualName) {
        return "__" + fullyQualName.replace(".", "_");
    }

    /** List of declarations declared by the module */
    private List<AdaDeclaration> declarations = new ArrayList<>();

    private Libadalang.PackageDecl analyzedPackage;

    /** Analyse an Ada specification file. */
    public Package analyzeSpec(Libadalang.AnalysisUnit unit) {
        // Reset the previous values
        declarations = new ArrayList<>();
        analyzedPackage = Libadalang.PackageDecl.NONE;

        // Visit the AST
        unit.getRoot().accept(this);

        if (analyzedPackage.isNone()) return null;

        // Return the module.
        return new Package(analyzedPackage, declarations);
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

        // The name of the current package is the last symbol in the array.
        this.analyzedPackage = node;

        return null;
    }

    @Override
    public Void visit(Libadalang.SubpDecl node) {
        SubpSpec spec = node.fSubpSpec();

        // Get the C symbol of the function.
        String symbol = symbolify(node.pFullyQualifiedName());

        // TODO: Handle subprograms that can be attached to a type.
        Role role = null;

        // Get the list of parameters.
        List<SubpParam> parameters = new ArrayList<>();
        if (!spec.fSubpParams().isNone()) {
            for (var child : spec.fSubpParams().fParams().children()) {
                Libadalang.ParamSpec paramSpec = (Libadalang.ParamSpec) child;
                // TODO: Currently, only integer types are handled: when more types are supported,
                // we will need to update the transfer specs.

                Transfer transfer = new Transfer(RequiredOwner.ANY);
                for (var p : paramSpec.fIds().children())
                    parameters.add(
                            new SubpParam(
                                    paramSpec,
                                    Name.fromPascalWithUnderscore(p.getText()),
                                    transfer));
            }
        }

        Subprogram subProg =
                new Subprogram(
                        node,
                        Name.fromPascalWithUnderscore(spec.fSubpName().getText()),
                        parameters,
                        symbol,
                        role,
                        Owner.UNKNOWN);
        declarations.add(subProg);
        return null;
    }
}
