package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
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
    protected Function<Libadalang.AdaNode, Void> createDefaultBehavior() {
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
        Libadalang.SubpSpec spec = node.fSubpSpec();

        // Get the C symbol of the function.
        String symbol = symbolify(node.pFullyQualifiedName());

        // If the function is callable with the dot notation, it is a method.
        // TODO: When eng/libadalang/libadalang#1547 is resoled, use the new property.
        Role role = null;
        Libadalang.BaseTypeDecl primitiveType = spec.pPrimitiveSubpFirstType(false);
        if (!primitiveType.isNone() && spec.pParams().length != 0) {
            if (spec.pParams()[0]
                    .pFormalType(Libadalang.AdaNode.NONE)
                    .pMatchingType(primitiveType, Libadalang.AdaNode.NONE))
                role =
                        new Role(
                                RoleKind.METHOD,
                                AdaAPI.makeProxyFullyQualifiedName(primitiveType),
                                null);
        }

        // Get the list of parameters.
        List<SubpParam> parameters = new ArrayList<>();
        if (!spec.fSubpParams().isNone()) {
            for (var child : spec.fSubpParams().fParams().children()) {
                Libadalang.ParamSpec paramSpec = (Libadalang.ParamSpec) child;

                // Record value types are given through an address, but later **copied** into the
                // arguments, so the ownership does not matter.
                // TODO Access types: Ownership informations will be necessary when access types are
                // handled.
                Transfer transfer = new Transfer(RequiredOwner.ANY);

                // For each parameter declared in the spec, add a parameter.
                for (var p : paramSpec.fIds().children())
                    parameters.add(
                            new SubpParam(
                                    paramSpec,
                                    Name.fromPascalWithUnderscore(p.getText()),
                                    transfer));
            }
        }

        // Get the most visible part of the type of the parameter. The TypeExpr may refer to
        // an incomplete type:
        // .. code::
        //
        //    type T is private;
        //
        //    function Foo return T; -- ``T`` refers to the TypeDecl above, but it holds
        //                           -- close to no information.
        //
        //  private
        //     type T is record
        //        ...
        //     end record;
        //
        Libadalang.BaseTypeDecl returnType = spec.pReturnType(Libadalang.AdaNode.NONE);
        if (!returnType.isNone())
            returnType =
                    (Libadalang.BaseTypeDecl)
                            returnType.pMostVisiblePart(Libadalang.AdaNode.NONE, false);

        // Record value type are allocated on the heap before being returned. If a function returns
        // one, it is copied to a heap address before being returned to the user.
        Owner returnOwner = Owner.UNKNOWN;
        if (!returnType.isNone() && returnType.pIsRecordType(Libadalang.AdaNode.NONE))
            returnOwner = Owner.LIBRARY;

        Subprogram subProg =
                new Subprogram(
                        node,
                        Name.fromPascalWithUnderscore(spec.fSubpName().getText()),
                        parameters,
                        symbol,
                        role,
                        returnOwner);
        declarations.add(subProg);
        return null;
    }

    public Void visit(Libadalang.PrivateTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();
        // Add a new record containing the private type.
        declarations.add(
                new Record(
                        parentDecl,
                        Name.fromPascalWithUnderscore(parentDecl.pDefiningName().getText()),
                        List.of()));

        return null;
    }

    public Void visit(Libadalang.RecordTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();

        // Get the components of the record.
        ArrayList<Component> components = new ArrayList<>();
        for (var c : node.fRecordDef().fComponents().fComponents().children()) {
            Libadalang.ComponentDecl componentDecl = (Libadalang.ComponentDecl) c;
            for (var name : componentDecl.fIds().children()) {
                components.add(
                        new Component(
                                componentDecl, Name.fromPascalWithUnderscore(name.getText())));
            }
        }

        // Add a new Record containing the ada record type.
        declarations.add(
                new Record(
                        parentDecl,
                        Name.fromPascalWithUnderscore(parentDecl.pDefiningName().getText()),
                        components));

        return null;
    }
}
