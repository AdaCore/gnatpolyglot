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

        if (spec.pReturnType(Libadalang.AdaNode.NONE).isNone()) {
            builder.append("Void");
        } else {
            builder.append(
                    spec.pReturnType(Libadalang.AdaNode.NONE)
                            .pSpecificType()
                            .pFullyQualifiedName()
                            .replace(".", "_"));
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
    private HashMap<Libadalang.TypeDecl, AdaDeclaration> mappedTypes = new HashMap<>();

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

    /** Return whether the binded type was registed in the declaration list of its parent package */
    public boolean registedInParentPackage(Libadalang.BaseTypeDecl type) {
        Libadalang.BasicDecl decl = type.pParentBasicDecl();
        AdaDeclaration mappedType = mappedTypes.get(type);
        if (mappedType != null && decl instanceof Libadalang.BasePackageDecl packageDecl) {
            Package pack = mappedPackages.get(packageDecl);
            return pack != null && pack.declarations.contains(mappedType);
        }
        return false;
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
            if (decl instanceof Libadalang.PackageDecl p) {
                if (mappedPackages.containsKey(p)) continue;
                // When a package was not visited, create an empty one. We do not want to visit all
                // its declarations.
                Package pack = new Package(p, new ArrayList<>());
                packages.add(pack);
                enqueueParentPackages(p);
                mappedPackages.put(p, pack);
            } else if (decl instanceof Libadalang.BaseTypeDecl type) {
                // If the type was already visited, it is already in a package's list of
                // declaration. Also ignore types from the Std unit or native types.
                if (registedInParentPackage(type)
                        || api.getDeclChecker().seenUnbindable(decl)
                        || type.getUnit().equals(type.pStandardUnit())
                        || AdaTypeMatcher.isNumber(type)) continue;
                if (type.pParentBasicDecl() instanceof Libadalang.BasePackageDecl p) {
                    Package pack = mappedPackages.get(p);
                    // If the type's package does not yet exist, enqueue the package and the type.
                    // The package needs to exist before the mapped type.
                    if (pack == null) {
                        queuedDecls.add(p);
                        queuedDecls.add(type);
                    } else {
                        // pack may be null when the type is an array.
                        if (pack != null) declarations = pack.declarations;
                        visitDecl(type);
                        declarations = null;
                    }
                } else {
                    AdaScanner.error(
                            new UnbindableDeclException(decl, "Unsupported parent declaration"));
                }
            }
        }
        return packages;
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
                queuedDecls.add(p);
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
                // The first argument of the subprogram must be compatible with the primitive type.
                && spec.pParams().length != 0
                && (spec.pParamTypes(Libadalang.AdaNode.NONE)[0].pMatchingType(
                        type, Libadalang.AdaNode.NONE));
    }

    public void processSubprogram(Libadalang.BasicDecl node) {
        Libadalang.BaseSubpSpec spec = node.pSubpSpecOrNull(false);

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
        if (isDotCallable(spec, primitiveType))
            role = new Role(RoleKind.METHOD, AdaAPI.makeTypeExpr(primitiveType), null);

        // Get the list of parameters.
        List<SubpParam> parameters = new ArrayList<>();
        Libadalang.BaseTypeDecl[] types = spec.pParamTypes(Libadalang.AdaNode.NONE);
        int typeIndex = 0;
        for (var paramSpec : spec.pAbstractFormalParams()) {
            // Enqueue the parameter's type in case we do not visit it in the required list of
            // units
            queuedDecls.add(paramSpec.pFormalType(Libadalang.AdaNode.NONE));

            // Record value types are given through an address, but later **copied** into the
            // arguments, so the ownership does not matter.
            // TODO Access types: Ownership informations will be necessary when access types are
            // handled.
            Transfer transfer =
                    new Transfer(
                            paramSpec
                                            .pFormalType(Libadalang.AdaNode.NONE)
                                            .pIsAccessType(Libadalang.AdaNode.NONE)
                                    ? RequiredOwner.LIBRARY
                                    : RequiredOwner.ANY);

            // For each parameter declared in the spec, add a parameter.
            for (var p : paramSpec.pDefiningNames())
                parameters.add(
                        new SubpParam(
                                paramSpec,
                                Name.fromLower(p.pCanonicalText().text),
                                transfer,
                                types[typeIndex++]));
        }

        Libadalang.BaseTypeDecl returnType = spec.pReturnType(Libadalang.AdaNode.NONE);
        if (!returnType.isNone()) queuedDecls.add(returnType);

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

        Name name = AdaAPI.functionProxyName(spec.pName().pCanonicalText().text);

        Subprogram subProg = new Subprogram(node, name, parameters, symbol, role, returnOwner);
        if (role != null && role.kind == RoleKind.METHOD) {
            ((Record) mappedTypes.get(primitiveType)).methods.add(subProg);
        }
        // Create a similar subprogram for "/=" when the current subprogram is "="
        if (name.equals(Name.operatorEq)) {
            processSubprogram(node.pCorrespondingNeqSubprogram());
        }
        declarations.add(subProg);
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
    public Void visit(Libadalang.ConcreteTypeDecl node) {
        AdaDeclaration decl = mappedTypes.get(node);
        // The type could have been (partially) processed when processing a child type.
        // IFF it was not already processed and is not a derived type, then process it.
        if (decl == null || node.fTypeDef() instanceof Libadalang.DerivedTypeDef) {
            node.fTypeDef().accept(this);
        } else {
            declarations.add(decl);
        }
        return null;
    }

    public Record makeRecord(
            Libadalang.BaseRecordDef recordDef, Libadalang.ConcreteTypeDecl parentDecl) {
        Record rec = (Record) mappedTypes.get(parentDecl);
        if (rec != null) return rec;
        // Get the components of the record.
        ArrayList<Component> components = new ArrayList<>();
        if (!recordDef.isNone() && !(recordDef instanceof Libadalang.NullRecordDef)) {
            for (var c : recordDef.fComponents().fComponents()) {
                // If there is a null component decl, there should not be any component.
                if (c instanceof Libadalang.NullComponentDecl) break;

                Libadalang.ComponentDecl componentDecl = (Libadalang.ComponentDecl) c;
                queuedDecls.add(componentDecl.pFormalType(Libadalang.AdaNode.NONE));
                for (var name : componentDecl.fIds()) {
                    components.add(new Component(componentDecl, AdaAPI.getName(name)));
                }
            }
        }
        rec = new Record(parentDecl, AdaAPI.getName(parentDecl.pDefiningName()), components);
        mappedTypes.put(parentDecl, rec);
        return rec;
    }

    @Override
    public Void visit(Libadalang.PrivateTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();
        // Add a new record containing the private type.
        Record res = makeRecord(Libadalang.BaseRecordDef.NONE, parentDecl);
        declarations.add(res);

        return null;
    }

    @Override
    public Void visit(Libadalang.RecordTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();

        // Add a new Record containing the ada record type.
        declarations.add(makeRecord(node.fRecordDef(), parentDecl));

        return null;
    }

    @Override
    public Void visit(Libadalang.DerivedTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();

        if (parentDecl.pIsTaggedType(Libadalang.AdaNode.NONE)) {
            // The parent type could be located in an ada unit that was not already processed. If
            // that is the case, then process the parent type, but do not register it.
            Record rec = makeRecord(node.fRecordExtension(), parentDecl);
            Libadalang.ConcreteTypeDecl parentType =
                    (Libadalang.ConcreteTypeDecl)
                            node.fSubtypeIndication()
                                    .pDesignatedTypeDecl()
                                    .pBaseSubtype(Libadalang.AdaNode.NONE);
            if (!parentType.isNone()) {
                if (parentType.fTypeDef() instanceof Libadalang.DerivedTypeDef derived)
                    rec.parent = makeRecord(derived.fRecordExtension(), parentType);
                else if (parentType.fTypeDef() instanceof Libadalang.RecordTypeDef subrec)
                    rec.parent = makeRecord(subrec.fRecordDef(), parentType);
                else if (parentType.pIsPrivate())
                    rec.parent = makeRecord(BaseRecordDef.NONE, parentType);
                else throw new RuntimeException("Could not process parent type");
                queuedDecls.add(parentType);
            }
            declarations.add(rec);
        } else if (parentDecl.pIsRecordType(Libadalang.AdaNode.NONE)) {
            // If the parent is a non-tagged record, simply copy the fields of the root type.
            Libadalang.TypeDecl rootType =
                    (Libadalang.TypeDecl) parentDecl.pRootType(Libadalang.AdaNode.NONE);
            if (rootType.fTypeDef() instanceof Libadalang.RecordTypeDef recordDef) {
                declarations.add(makeRecord(recordDef.fRecordDef(), parentDecl));
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
            mappedTypes.put(parentDecl, array);
            queuedDecls.add(parentDecl.pCompType(false, Libadalang.AdaNode.NONE));
        } else if (AdaTypeMatcher.isEnum(parentDecl)) {
            EnumType enumType = createEnumType(parentDecl);
            declarations.add(enumType);
            mappedTypes.put(parentDecl, enumType);
            for (var prim : parentDecl.pGetPrimitives(true, false)) {
                if (!(prim instanceof Libadalang.EnumLiteralDecl)) processSubprogram(prim);
            }
        } else if (!parentDecl.pIsScalarType(Libadalang.AdaNode.NONE))
            throw new IllegalArgumentException("Unsupported derivation of types " + node);

        return null;
    }

    @Override
    public Void visit(Libadalang.ArrayTypeDef node) {
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();
        Libadalang.BaseTypeDecl componentType =
                node.fComponentType().fTypeExpr().pDesignatedTypeDecl();
        if (!parentDecl.pGetAspectAssoc(Libadalang.Symbol.create("pack")).isNone()) {
            // TODO: create a record
        } else {
            Array array = new Array(parentDecl);
            declarations.add(array);
            mappedTypes.put(parentDecl, array);
            queuedDecls.add(componentType);
        }
        return null;
    }

    @Override
    public Void visit(Libadalang.ExceptionDecl node) {
        for (var dn : node.pDefiningNames()) {
            declarations.add(new AdaException(AdaAPI.getName(dn), node, dn, exceptionNumber++));
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
        Libadalang.ConcreteTypeDecl parentDecl =
                (Libadalang.ConcreteTypeDecl) node.pParentBasicDecl();

        EnumType enumType = createEnumType(parentDecl);
        declarations.add(enumType);
        mappedTypes.put(parentDecl, enumType);
        return null;
    }

    public Void visit(Libadalang.ObjectDecl node) {
        if (!(node.pParentBasicDecl() instanceof Libadalang.PackageDecl)) return null;
        for (var id : node.fIds()) {
            declarations.add(new GlobalVariable(node, AdaAPI.getName(id)));
        }
        return null;
    }

    public Void visit(Libadalang.GenericPackageDecl node) {
        throw new UnbindableDeclException(node, "Generic packages are not supported");
    }

    public Void visit(Libadalang.GenericSubpDecl node) {
        throw new UnbindableDeclException(node, "Generic subprograms are not supported");
    }
}
