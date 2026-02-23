package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseRecordDef;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.proxy.AdaDeclaration;
import com.adacore.polyglot.ada2proxy.proxy.AdaException;
import com.adacore.polyglot.ada2proxy.proxy.Array;
import com.adacore.polyglot.ada2proxy.proxy.Component;
import com.adacore.polyglot.ada2proxy.proxy.EnumLiteral;
import com.adacore.polyglot.ada2proxy.proxy.EnumType;
import com.adacore.polyglot.ada2proxy.proxy.GlobalVariable;
import com.adacore.polyglot.ada2proxy.proxy.Package;
import com.adacore.polyglot.ada2proxy.proxy.Record;
import com.adacore.polyglot.ada2proxy.proxy.SubpParam;
import com.adacore.polyglot.ada2proxy.proxy.Subprogram;
import com.adacore.polyglot.ada2proxy.proxy.Subtype;
import com.adacore.polyglot.proxy.BadNameSyntaxException;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Visitor class to analyze Ada spec files and generate the proxy. */
public class AdaVisitor extends Libadalang.DefaultVisitor<Void> {

    private Set<String> symbols = new HashSet<>();

    private Queue<Libadalang.BasicDecl> queuedDecls = new LinkedList<>();

    private AdaAPI api;

    public AdaVisitor(AdaAPI api) {
        this.api = api;
    }

    /** Turn a fully qualified name into a unique C symbol. */
    private String symbolify(Libadalang.BaseSubpSpec spec) {
        StringBuilder builder = new StringBuilder();
        // Symbols starting with a "_", followed by either a capital letter or an other
        // "_" are considered reserved identifier.
        // The prefix of the symbols:
        // - starts with the 2 same first characters ("_P") to reduce the risk of clashing with
        // other external symbols as much as possible
        // - has a third character to differentiate binded ``U``ser functions from the polyglot
        // ``G``enerated functions such as getters or setters.
        builder.append("_PU");

        // If the name is an operator, use the predefined proxy name.
        if (spec.pName().pIsOperatorName())
            builder.append(
                            spec.pParentBasicDecl()
                                    .pParentBasicDecl()
                                    .pFullyQualifiedName()
                                    .replace(".", "_"))
                    .append(
                            AdaAPI.functionProxyName(spec.pName().pCanonicalText().text)
                                    .toPascalWithUnderscore());
        else builder.append(spec.pName().pFullyQualifiedName().replace(".", "_"));

        if (spec.pReturnType(spec).isNone()) {
            builder.append("Void");
        } else {
            builder.append(
                    spec.pReturnType(spec).pSpecificType().pFullyQualifiedName().replace(".", "_"));
        }
        String res = builder.toString();
        int count = 0;
        // While the symbol is not unique, keep trying with a new one.
        while (symbols.contains(res)) {
            StringBuilder b = new StringBuilder(builder);
            res = b.append(count).toString();
            count++;
        }
        // Register the new symbol.
        symbols.add(res);
        return res;
    }

    /** List of declarations declared by the module */
    private List<AdaDeclaration> declarations = new ArrayList<>();

    /** The current package being analyzed */
    private Libadalang.PackageDecl analyzedPackage;

    /** Map Ada declarations to their AdaProxy objects. */
    private HashMap<Libadalang.BasicDecl, AdaDeclaration> mappedDecls = new HashMap<>();

    /** Map Ada PackageDecl to their AdaProxy Package. */
    private HashMap<Libadalang.PackageDecl, Package> mappedPackages = new HashMap<>();

    private int exceptionNumber = 1;

    /** Analyse an Ada specification file. */
    public Package analyzeSpec(Libadalang.AnalysisUnit unit) {
        // Reset the previous values
        declarations = new ArrayList<>();
        analyzedPackage = Libadalang.PackageDecl.NONE;

        // Visit the AST
        unit.getRoot().accept(this);

        if (analyzedPackage.isNone()) return null;

        // Return the module.
        Package pack = new Package(analyzedPackage, declarations);
        mappedPackages.put(analyzedPackage, pack);
        return pack;
    }

    public static boolean isPrivateUnit(Libadalang.AnalysisUnit unit) {
        if (unit.getRoot() instanceof Libadalang.CompilationUnit cu
                && cu.fBody() instanceof Libadalang.LibraryItem li
                && li.pTopLevelDecl(unit) instanceof Libadalang.BasePackageDecl packageDecl) {
            boolean isPrivate = li.fHasPrivate().pAsBool();
            // Verify that the parent is not a private package either
            if (!isPrivate
                    && packageDecl.fPackageName().fName() instanceof Libadalang.DottedName name) {
                return isPrivateUnit(name.fPrefix().pReferencedDecl(false).getUnit());
            }
            return isPrivate;
        }
        return false;
    }

    /** Return the package that contains decl. */
    public Libadalang.BasePackageDecl getOwningPackage(Libadalang.BasicDecl decl) {
        Libadalang.BasicDecl parent = decl.pParentBasicDecl();
        while (!parent.isNone()) {
            if (parent instanceof Libadalang.BasePackageDecl packageDecl) return packageDecl;
            parent = parent.pParentBasicDecl();
        }
        AdaScanner.warning(new UnbindableDeclException(decl, "Could not find a parent package"));
        return Libadalang.BasePackageDecl.NONE;
    }

    public void enqueueDecl(Libadalang.BasicDecl decl) {
        if (decl.isNone()) {
            throw new IllegalArgumentException("Unexpected null decl");
        }
        queuedDecls.add(decl);
    }

    /**
     * Create a list of all the packages with the types that are missing in the proxy.
     *
     * <p>The possible occurence of a missing package is when `ada2proxy` is called with the
     * `--unit` parameter and the parents of listed packages are not in the list.
     *
     * <p>The possible occurence of a missing type is when a type from a with-ed package is used in
     * an other package.
     */
    public List<Package> getNonVisitedPackages() {
        List<Package> packages = new ArrayList<>();
        while (!queuedDecls.isEmpty()) {
            Libadalang.BasicDecl decl = queuedDecls.poll();
            if (decl instanceof Libadalang.BasePackageDecl p
                    && !p.getUnit().equals(p.pStandardUnit())) {
                if (mappedPackages.containsKey(p)) continue;
                packages.add(analyzeSpec(p.getUnit()));
            } else if (!decl.getUnit().equals(decl.pStandardUnit())) {
                enqueueDecl(getOwningPackage(decl));
            }
        }
        return packages.stream().filter(p -> p != null).toList();
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
        if (!isPrivateUnit(node.getUnit())) {
            node.fBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Libadalang.LibraryItem node) {
        if (!node.fHasPrivate().pAsBool()) {
            visitDecl(node.fItem());
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
        if (!lhs.name.equals(rhs.name) || lhs.parameters.size() != rhs.parameters.size())
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
                                Name.fromLower(
                                        subp.getReturnType()
                                                .pCanonicalFullyQualifiedName()
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
                                    .map(p -> AdaAPI.makeTypeExpr(p.getType()))
                                    .toList();
                    var otherParams =
                            other.parameters.stream()
                                    .map(p -> AdaAPI.makeTypeExpr(p.getType()))
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

    void enqueueParentPackages(Libadalang.BasePackageDecl node) {
        Libadalang.Name name = node.fPackageName().fName();
        while (name instanceof Libadalang.DottedName dotted) {
            if (dotted.fPrefix().pReferencedDecl(false) instanceof Libadalang.PackageDecl p)
                enqueueDecl(p);
            name = dotted.fPrefix();
        }
    }

    void visitDecl(Libadalang.BasicDecl decl) {
        try {
            api.getDeclChecker().checkIsBindable(decl);
            // The decl may already have been marked as unbindable, and the checker would not have
            // thrown an other exception.
            if (!api.getDeclChecker().seenUnbindable(decl)) decl.accept(this);
        } catch (UnbindableDeclException e) {
            AdaScanner.warning(e);
        } catch (BadNameSyntaxException e) {
            AdaScanner.warning(new UnbindableDeclException(decl, e));
        } catch (Throwable e) {
            AdaScanner.error(new UnbindableDeclException(decl, e));
        }
    }

    @Override
    public Void visit(Libadalang.PackageDecl node) {
        this.analyzedPackage = node;

        for (var n : node.fPublicPart().fDecls()) {
            if (n instanceof Libadalang.BasicDecl decl) visitDecl(decl);
        }
        resolveNameConflicts();
        enqueueParentPackages(node);

        return null;
    }

    /** Return whether ``subp`` is dot callable with ``type``. */
    private boolean isDotCallable(Libadalang.BaseSubpSpec spec, Libadalang.BaseTypeDecl type) {
        // TODO: When eng/libadalang/libadalang#1547 is resoled, use the new property.
        return !type.isNone()
                // Native and array types do not create new types in the proxy, so we cannot attach
                // methods
                && AdaAPI.checkNativeType(type) == null
                && !type.pIsEnumType(Libadalang.AdaNode.NONE)
                && !type.pIsArrayType(Libadalang.AdaNode.NONE)
                && !type.pIsAccessType(Libadalang.AdaNode.NONE)
                // The subprogram must be declared in the same package as the type.
                && type.pParentBasicDecl().equals(spec.pParentBasicDecl().pParentBasicDecl())
                // The first argument of the subprogram must be compatible with the primitive type.
                && spec.pParams().length != 0
                && (spec.pParamTypes(spec)[0].pMatchingType(type, spec));
    }

    public void processSubprogram(Libadalang.BasicDecl node) {
        if (mappedDecls.containsKey(node)) {
            return;
        }
        Libadalang.BaseSubpSpec spec = node.pSubpSpecOrNull(true);

        // Get the C symbol of the function.
        String symbol = null;
        if (!(node instanceof Libadalang.AbstractSubpDecl)) symbol = symbolify(spec);

        // Do not bind any of the controlled type functions. They are too "Ada-specific" and lead to
        // inconsistencies in the management of object destruction when exposed to the users in
        // other target languages.
        if (AdaTypeMatcher.isControlledPrimitve(node)) return;

        // If the function is callable with the dot notation, it is a method.
        // The subprogram may be visited when exploring inherited primitive subprograms: if so, use
        // the current derived type.
        Role role = null;
        Libadalang.BaseTypeDecl primitiveType = spec.pPrimitiveSubpFirstType(false);
        // We may be visiting a primitive from the private part. This implies that the primitive
        // type could also be the part of the type definition that is located in the private part.
        // In order to avoid using different declarations to the same type, we always use the one in
        // the public part of the package.
        if (!primitiveType.isNone()) {
            Libadalang.BaseTypeDecl previousPart = primitiveType.pPreviousPart(false);
            if (!previousPart.isNone()) primitiveType = previousPart;
        }
        if (isDotCallable(spec, primitiveType))
            role = new Role(RoleKind.METHOD, AdaAPI.makeTypeExpr(primitiveType), null);

        // Get the list of parameters.
        List<SubpParam> parameters = new ArrayList<>();
        Libadalang.BaseTypeDecl[] types = spec.pParamTypes(node);
        int typeIndex = 0;
        for (var paramSpec : spec.pAbstractFormalParams()) {
            // Enqueue the parameter's type in case we do not visit it in the required list of
            // units
            enqueueDecl(paramSpec.pFormalType(node));

            // Record value types are given through an address, but later **copied** into the
            // arguments, so the ownership does not matter.
            // TODO Access types: Ownership informations will be necessary when access types are
            // handled.
            Transfer transfer =
                    new Transfer(
                            paramSpec.pFormalType(node).pIsAccessType(Libadalang.AdaNode.NONE)
                                    ? RequiredOwner.LIBRARY
                                    : RequiredOwner.ANY);

            // For each parameter declared in the spec, add a parameter.
            for (var p : paramSpec.pDefiningNames())
                parameters.add(
                        new SubpParam(paramSpec, AdaAPI.getName(p), transfer, types[typeIndex++]));
        }

        Libadalang.BaseTypeDecl returnType = spec.pReturnType(node);
        if (!returnType.isNone()) enqueueDecl(returnType);

        Owner returnOwner = Owner.UNKNOWN;
        if (!returnType.isNone()) {
            // When returning an access, there is no way to determine whether the pointer is to be
            // freed by the user or the library at an other moment. The safest way to avoid the any
            // errors is to making the returning access library owned.
            if (returnType.pIsAccessType(Libadalang.AdaNode.NONE)) returnOwner = Owner.LIBRARY;
            // Record and Array value types are allocated on the heap before being returned from the
            // Ada glue. Since the copy is performed after the called Ada function has returned, we
            // know for sure that the user is the only owner of that heap value.
            if (returnType.pIsRecordType(Libadalang.AdaNode.NONE)
                    || returnType.pIsArrayType(Libadalang.AdaNode.NONE)) returnOwner = Owner.USER;
        }

        Name name = AdaAPI.functionProxyName(node.pDefiningName().pCanonicalText().text);

        Subprogram subProg = new Subprogram(node, name, parameters, symbol, role, returnOwner);
        if (role != null && role.kind == RoleKind.METHOD) {
            ((Record) mappedDecls.get(primitiveType)).methods.add(subProg);
        }
        // Create a similar subprogram for "/=" when the current subprogram is "="
        if (name.equals(Name.operatorEq)) {
            processSubprogram(node.pCorrespondingNeqSubprogram());
        }
        declarations.add(subProg);
        mappedDecls.put(node, subProg);
    }

    @Override
    public Void visit(Libadalang.SubpDecl node) {
        processSubprogram(node);
        return null;
    }

    @Override
    public Void visit(Libadalang.AbstractSubpDecl node) {
        processSubprogram(node);
        return null;
    }

    @Override
    public Void visit(Libadalang.ExprFunction node) {
        processSubprogram(node);
        return null;
    }

    @Override
    public Void visit(Libadalang.NullSubpDecl node) {
        processSubprogram(node);
        return null;
    }

    @Override
    public Void visit(Libadalang.GenericSubpInstantiation node) {
        processSubprogram(node);
        return null;
    }

    @Override
    public Void visit(Libadalang.ConcreteTypeDecl node) {
        AdaDeclaration decl = mappedDecls.get(node);
        // The type could have been (partially) processed when processing a child type.
        // IFF it was not already processed and is not a derived type, then process it.
        if (decl == null || node.fTypeDef() instanceof Libadalang.DerivedTypeDef) {
            node.fTypeDef().accept(this);
        } else {
            declarations.add(decl);
        }
        return null;
    }

    public Record makeRecord(Libadalang.BaseRecordDef recordDef, Libadalang.TypeDecl parentDecl) {
        Record rec = (Record) mappedDecls.get(parentDecl);
        if (rec != null) return rec;
        // Get the components of the record.
        ArrayList<Component> components = new ArrayList<>();
        if (!recordDef.isNone() && !(recordDef instanceof Libadalang.NullRecordDef)) {
            for (var c : recordDef.fComponents().fComponents()) {
                // If there is a null component decl, there should not be any component.
                if (c instanceof Libadalang.NullComponentDecl) break;

                Libadalang.ComponentDecl componentDecl = (Libadalang.ComponentDecl) c;
                enqueueDecl(componentDecl.pFormalType(recordDef));
                for (var name : componentDecl.fIds()) {
                    components.add(new Component(componentDecl, AdaAPI.getName(name)));
                }
            }
        }
        rec = new Record(parentDecl, AdaAPI.getName(parentDecl.pDefiningName()), components);
        mappedDecls.put(parentDecl, rec);
        return rec;
    }

    @Override
    public Void visit(Libadalang.PrivateTypeDef node) {
        Libadalang.TypeDecl parentDecl = (Libadalang.TypeDecl) node.pParentBasicDecl();
        // Add a new record containing the private type.
        Record res = makeRecord(Libadalang.BaseRecordDef.NONE, parentDecl);
        declarations.add(res);

        return null;
    }

    @Override
    public Void visit(Libadalang.RecordTypeDef node) {
        Libadalang.TypeDecl parentDecl = (Libadalang.TypeDecl) node.pParentBasicDecl();

        // Add a new Record containing the ada record type.
        declarations.add(makeRecord(node.fRecordDef(), parentDecl));

        return null;
    }

    /**
     * Visit the overriding primitives of a type that may be located in the private part of the
     * package, or inherited from specialized parent in the private definition of the type.
     */
    private void visitPrivateOverrides(
            Libadalang.BaseTypeDecl publicPart, Libadalang.BaseTypeDecl privatePart) {
        var publicSubpRoots =
                Stream.of(publicPart.pGetPrimitives(true, false))
                        .filter(p -> p.pIsVisible(publicPart))
                        .flatMap(p -> Stream.of(p.pRootSubpDeclarations(publicPart, false)))
                        .toList();
        var privatePrimitives = Stream.of(privatePart.pGetPrimitives(false, false)).toList();

        for (var prim : privatePrimitives) {
            var roots = Stream.of(prim.pRootSubpDeclarations(Libadalang.AdaNode.NONE, false));
            // If the primitive is not visible from the public part, and shares a root with one of
            // the public part's primitive, then it is inherited from a specialized parent and must
            // be bound.
            //
            // :: code:
            //
            //    type A is abstract tagged null record;
            //    procedure Foo(V: A) is abstract;
            //
            //    type B is new A with private;
            //
            //    type C is new A with null record;
            //    procedure Bar(X: C) is null;
            //
            //    package Pkg is
            //       type D is new A with null record;
            //       procedure Foo(X: D) is null
            //    end Pkg;
            //
            //    type E is new A with null record;
            //
            // private
            //
            //    overriding procedure Foo(V: C) is null;
            //    -- The override is hidden in the private part but should still be bound.
            //
            //    type B is new C with null record;
            //    -- C is the parent of the private part: this should not appear in the proxy
            //    -- However, C overrides Foo: B should share the override, but not Bar
            //
            //    procedure Baz (X: B) is null;
            //    -- Baz is only defined in the private part and is not a primitive of A nor C:
            //
            //    type E is new Pkg.D with null record;
            //    -- PKg.Foo is visible from the E public part, but only inherited by the private
            //    -- part.
            if (roots.anyMatch(publicSubpRoots::contains)) {
                visitDecl(prim);
            }
        }
    }

    @Override
    public Void visit(Libadalang.DerivedTypeDef node) {
        Libadalang.TypeDecl parentDecl = (Libadalang.TypeDecl) node.pParentBasicDecl();

        if (parentDecl.pIsTaggedType(Libadalang.AdaNode.NONE)) {
            // The parent type could be located in an ada unit that was not already processed. If
            // that is the case, then process the parent type, but do not register it.
            Record rec = makeRecord(node.fRecordExtension(), parentDecl);
            Libadalang.TypeDecl parentType =
                    (Libadalang.TypeDecl)
                            node.fSubtypeIndication().pDesignatedTypeDecl().pBaseSubtype(node);

            if (!parentType.isNone()) {
                if (parentType.fTypeDef() instanceof Libadalang.DerivedTypeDef derived)
                    rec.parent = makeRecord(derived.fRecordExtension(), parentType);
                else if (parentType.fTypeDef() instanceof Libadalang.RecordTypeDef subrec)
                    rec.parent = makeRecord(subrec.fRecordDef(), parentType);
                else if (parentType.pIsPrivate())
                    rec.parent = makeRecord(BaseRecordDef.NONE, parentType);
                else throw new RuntimeException("Could not process parent type");
                enqueueDecl(parentType);
            }

            declarations.add(rec);

            Libadalang.BaseTypeDecl privatePart = parentDecl.pNextPart();
            if (!privatePart.isNone() && !privatePart.pBaseType(privatePart).equals(privatePart))
                // Some primitives are possibly overriden by the type inherited in the private part
                visitPrivateOverrides(parentDecl, privatePart);
            else {
                // Some primitive overrides could be hidden in the private part.
                visitPrivateOverrides(parentDecl, parentDecl);
            }
        } else if (parentDecl.pIsRecordType(Libadalang.AdaNode.NONE)
                || AdaTypeMatcher.isPrivate(parentDecl)) {
            // If the parent is a non-tagged record, simply copy the fields of the root type.
            Libadalang.TypeDecl rootType = (Libadalang.TypeDecl) parentDecl.pRootType(node);
            if (rootType.fTypeDef() instanceof Libadalang.RecordTypeDef recordDef) {
                declarations.add(makeRecord(recordDef.fRecordDef(), parentDecl));
            } else {
                declarations.add(makeRecord(Libadalang.BaseRecordDef.NONE, parentDecl));
            }

            // When derivating from a record, we need to get the primitives of said record too.
            // The primitives directly explicited on this type will be visited at an other time,
            // so only treat the inherited ones.
            for (var primitive : parentDecl.pGetPrimitives(true, false)) {
                primitive.accept(this);
            }
        } else if (parentDecl.pIsArrayType(Libadalang.AdaNode.NONE)) {
            Array array = new Array(parentDecl);
            declarations.add(array);
            mappedDecls.put(parentDecl, array);
            enqueueDecl(parentDecl.pCompType(false, node));
        } else if (AdaTypeMatcher.isEnum(parentDecl)) {
            EnumType enumType = createEnumType(parentDecl);
            declarations.add(enumType);
            mappedDecls.put(parentDecl, enumType);
            for (var prim : parentDecl.pGetPrimitives(false, false)) {
                if (!(prim instanceof Libadalang.EnumLiteralDecl)) visitDecl(prim);
            }
        } else if (!parentDecl.pIsScalarType(Libadalang.AdaNode.NONE))
            throw new IllegalArgumentException("Unsupported derivation of types " + node);

        return null;
    }

    @Override
    public Void visit(Libadalang.ArrayTypeDef node) {
        Libadalang.TypeDecl parentDecl = (Libadalang.TypeDecl) node.pParentBasicDecl();
        Libadalang.BaseTypeDecl componentType =
                node.fComponentType().fTypeExpr().pDesignatedTypeDecl();
        if (!parentDecl.pGetAspectAssoc(Libadalang.Symbol.create("pack")).isNone()) {
            // TODO: create a record
        } else {
            Array array = new Array(parentDecl);
            declarations.add(array);
            mappedDecls.put(parentDecl, array);
            enqueueDecl(componentType);
            enqueueDecl(parentDecl.pIndexType(0, node));
        }
        return null;
    }

    @Override
    public Void visit(Libadalang.ExceptionDecl node) {
        if (mappedDecls.containsKey(node)) return null;

        if (node.fRenames().isNone()) {
            for (var dn : node.pDefiningNames()) {
                AdaException exception =
                        new AdaException(AdaAPI.getName(dn), node, dn, exceptionNumber++);
                declarations.add(exception);
                mappedDecls.put(node, exception);
            }
        } else {
            enqueueDecl(node.pParentBasicDecl());
            enqueueDecl(node.fRenames().fRenamedObject().pReferencedDecl(false));
        }
        return null;
    }

    private boolean contains(List<Libadalang.EvalDiscreteRange> ranges, int enumPos) {
        BigInteger value = BigInteger.valueOf(enumPos);
        return ranges.stream()
                .anyMatch(
                        r -> value.compareTo(r.lowBound) >= 0 && value.compareTo(r.highBound) <= 0);
    }

    private EnumType createEnumType(Libadalang.TypeDecl decl) {
        List<Libadalang.EvalDiscreteRange> ranges = List.of(decl.pDiscreteStaticValues());
        List<EnumLiteral> enumValues =
                Stream.of(decl.pGetPrimitives(false, false))
                        .filter(p -> p instanceof Libadalang.EnumLiteralDecl)
                        .map(p -> (Libadalang.EnumLiteralDecl) p)
                        .filter(lit -> contains(ranges, lit.childIndex()))
                        .map(
                                lit -> {
                                    return new EnumLiteral(
                                            lit,
                                            AdaAPI.getName(lit.pDefiningName()),
                                            lit.pEnumRep().intValue());
                                })
                        .toList();
        return new EnumType(decl, AdaAPI.getName(decl.pDefiningName()), enumValues);
    }

    public Void visit(Libadalang.EnumTypeDef node) {
        Libadalang.TypeDecl parentDecl = (Libadalang.TypeDecl) node.pParentBasicDecl();

        EnumType enumType = createEnumType(parentDecl);
        declarations.add(enumType);
        mappedDecls.put(parentDecl, enumType);
        return null;
    }

    @Override
    public Void visit(Libadalang.TypeAccessDef node) {
        enqueueDecl(node.fSubtypeIndication().pDesignatedTypeDecl());
        return null;
    }

    public Void visit(Libadalang.ClasswideTypeDecl node) {
        enqueueDecl(node.pSpecificType());
        return null;
    }

    @Override
    public Void visit(Libadalang.SubtypeDecl node) {
        // TODO: Apply type constraints if any
        enqueueDecl(node.pSpecificType());
        Subtype subtype = new Subtype(node, AdaAPI.getName(node.pDefiningName()));
        declarations.add(subtype);
        mappedDecls.put(node, subtype);
        return null;
    }

    public Void visit(Libadalang.ObjectDecl node) {
        if (!(node.pParentBasicDecl() instanceof Libadalang.PackageDecl)) return null;
        for (var id : node.fIds()) {
            declarations.add(new GlobalVariable(node, id, AdaAPI.getName(id)));
        }
        enqueueDecl(node.fTypeExpr().pDesignatedTypeDecl());
        return null;
    }

    public Void visit(Libadalang.GenericPackageDecl node) {
        throw new UnbindableDeclException(node, "Generic packages are not supported");
    }

    public Void visit(Libadalang.GenericSubpDecl node) {
        throw new UnbindableDeclException(node, "Generic subprograms are not supported");
    }
}
