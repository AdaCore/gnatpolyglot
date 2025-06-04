package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Visitor class to analyze Ada spec files and generate the proxy. */
public class AdaVisitor extends Libadalang.DefaultVisitor<Void> {

    private Set<String> symbols = new HashSet<>();

    /** Turn a fully qualified name into a unique C symbol. */
    private String symbolify(Libadalang.SubpSpec spec) {
        StringBuilder builder = new StringBuilder();
        // Symbols starting with a "_", followed by either a capital letter or an other
        // "_" are considered reserved identifier.
        // The prefix of the symbols:
        // - starts with the 2 same first characters ("_P") to reduce the risk of clashing with
        // other external symbols as much as possible
        // - has a third character to differentiate binded ``U``ser functions from the polyglot
        // ``G``enerated functions such as getters or setters.
        builder.append("_PU").append(spec.pName().pFullyQualifiedName().replace(".", "_"));
        if (spec.fSubpReturns().isNone()) {
            builder.append("Void");
        } else {
            builder.append(
                    spec.pReturnType(Libadalang.AdaNode.NONE)
                            .pFullyQualifiedName()
                            .replace(".", "_"));
        }
        String res = builder.toString();
        int count = 0;
        // While the symbol is not unique, keep trying with a new one.
        while (symbols.contains(res)) {
            StringBuilder b = new StringBuilder(builder);
            res = b.append(count).toString();
        }
        // Register the new symbol.
        symbols.add(res);
        return res;
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

    /**
     * Group subprograms of the current package by name and ignore any subprogram that are not
     * overloaded.
     */
    private List<List<Subprogram>> getOverloads() {
        return declarations.stream()
                .filter(d -> d instanceof Subprogram)
                .map(d -> (Subprogram) d)
                // Group functions by their name
                .collect(Collectors.groupingBy(d -> d.name))
                .entrySet()
                .stream()
                // Ignore functions that have no overloads
                .filter(e -> e.getValue().size() > 1)
                .map(Entry::getValue)
                .toList();
    }

    /**
     * Return whether lhs and rhs have a naming conflict and their parameters are similar, but not
     * their return type.
     */
    public boolean hasNamingConflict(Subprogram lhs, Subprogram rhs) {
        if (!lhs.name.equals(rhs.name) || lhs.parameters.size() != lhs.parameters.size())
            return false;
        for (int i = 0; i < lhs.parameters.size(); i++) {
            if (!lhs.parameters.get(i).equals(rhs.parameters.get(i))) return false;
        }
        return !lhs.getReturnType().equals(rhs.getReturnType());
    }

    /**
     * Resolve naming conflicts among the subprograms found in the current package.
     *
     * <p>Ada supports return type overloading. As this is not very common among other programming
     * languages, the Ada scanner resolves naming conflicts itself when it occurs.
     */
    public void resolveNameConflicts() {
        for (var overloads : getOverloads()) {
            Subprogram firstSubp = overloads.get(0);
            boolean needsRenaming =
                    overloads.stream().skip(1).anyMatch(sp -> hasNamingConflict(firstSubp, sp));
            // If any subprogram return type differ in the list of overload, add the return type as
            // the prefix of the subprogram's name in the proxy.
            //
            // :: code:
            //      procedure Func;              => "void_func"
            //      function Func return Integer => "standard_integer_func"
            if (needsRenaming) {
                for (var subp : overloads) {
                    Name prefix;
                    if (subp.getReturnType().isNone()) {
                        prefix = NativeType.VOID.declaration.name.getLastName();
                    } else {
                        prefix =
                                Name.fromPascalWithUnderscore(
                                        subp.getReturnType()
                                                .pFullyQualifiedName()
                                                .replace(".", "_"));
                    }
                    subp.name = prefix.concat(subp.name);
                }
            }
        }

        // If, once translated to the proxy, the names match and the parameters type expr are still
        // similar after the previous loop, rename ``other`` to a unique name.
        //
        // :: code:
        //      type My_Int is new Integer;
        //      procedure Func (I: My_Int);  => "func"
        //      procedure Func (I: Integer); => "func_1"
        for (var overloads : getOverloads()) {
            int renameNum = 1;
            for (int i = 0; i < overloads.size(); i++) {
                Subprogram subp = overloads.get(i);
                for (var other : overloads.stream().skip(i + 1).toList()) {
                    var subpParams =
                            subp.parameters.stream()
                                    .map(p -> AdaAPI.makeTypeExpr(p.getTypeExpr()))
                                    .toList();
                    var otherParams =
                            other.parameters.stream()
                                    .map(p -> AdaAPI.makeTypeExpr(p.getTypeExpr()))
                                    .toList();
                    if (subp.name.equals(other.name) && subpParams.equals(otherParams)) {
                        other.name =
                                Name.fromLower(
                                        other.name.toLower() + "_" + String.valueOf(renameNum));
                        renameNum += 1;
                    }
                }
            }
        }
    }

    @Override
    public Void visit(Libadalang.PackageDecl node) {
        this.analyzedPackage = node;

        node.fPublicPart().fDecls().accept(this);
        resolveNameConflicts();

        return null;
    }

    @Override
    public Void visit(Libadalang.SubpDecl node) {
        Libadalang.SubpSpec spec = node.fSubpSpec();

        // Get the C symbol of the function.
        String symbol = symbolify(spec);

        // If the function is callable with the dot notation, it is a method.
        // TODO: When eng/libadalang/libadalang#1547 is resoled, use the new property.
        Role role = null;
        Libadalang.BaseTypeDecl primitiveType = spec.pPrimitiveSubpFirstType(false);
        if (!primitiveType.isNone()
                && AdaAPI.checkNativeType(primitiveType) == null
                && spec.pParams().length != 0) {
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
