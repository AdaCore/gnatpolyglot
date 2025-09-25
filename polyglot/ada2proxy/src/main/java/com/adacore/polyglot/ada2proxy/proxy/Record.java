package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.TypeDef;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.ada2proxy.AdaTypeMatcher;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.NameTypeExpr;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import com.adacore.polyglot.proxy.TypeExpr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    /** List of all components. */
    public List<Component> components;

    /** Parent type. */
    public Record parent;

    /** TypeExpr to the record. */
    private NameTypeExpr ref;

    /** Default freeing function of the type. */
    private FunctionDecl freeFunction;

    /** Default allocating function of the type. */
    private ArrayList<FunctionDecl> allocFunctions;

    /** Default allocating function of the shadow type. */
    private ArrayList<FunctionDecl> shadowAllocFunctions;

    /** Default cloning function of the type. */
    private FunctionDecl cloneFunction;

    /** Default copying function of the type. */
    private FunctionDecl copyFunction;

    /** Default getter and setter functions of the type. */
    private ArrayList<FunctionDecl> componentAccessors;

    /** List of all the subprogams that have the METHOD role for this type. */
    public ArrayList<Subprogram> methods = new ArrayList<>();

    public Record(Libadalang.TypeDecl origin, Name name, List<Component> components) {
        super(name);
        this.origin = origin;
        this.components = components;
        this.parent = null;
    }

    /** Return the fully qualified name of the type. */
    public String getFullyQualifiedName() {
        return this.origin.pFullyQualifiedName();
    }

    /** Return the fully qualified name of the classwide version of the type. */
    public String getClasswideFullyQualifiedName() {
        return this.origin.pClasswideType().pFullyQualifiedName();
    }

    /** Get a {@link TypeExpr} to the current type. */
    public NameTypeExpr getTypeExpr() {
        if (this.ref == null) this.ref = (NameTypeExpr) AdaAPI.makeTypeExpr(this.origin);
        return this.ref;
    }

    /** Return the component with the given name. */
    public Component getComponent(Name name) {
        return getAllComponents().stream().filter(c -> c.name.equals(name)).findFirst().get();
    }

    /**
     * Return a list of all the components of the record, defined by the record and its parent type.
     */
    public List<Component> getAllComponents() {
        List<Component> res;
        if (parent != null) {
            res = parent.getAllComponents();
        } else res = new ArrayList<>(components.size());
        res.addAll(components);
        return res;
    }

    /** Return a list of all the non-overriding methods of the type and its parent type. */
    public List<Subprogram> getAllMethods() {
        List<Subprogram> res;
        if (parent != null) {
            res = parent.getAllMethods();
        } else res = new ArrayList<>(methods.size());
        methods.stream().filter(m -> !m.isOverriding()).forEach(m -> res.add(m));
        return res;
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        // The 3rd character of symbols for polyglot genererated functions is "G" (for Generated)
        StringBuilder symbolBuilder = new StringBuilder("_PG");
        symbolBuilder.append(getFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    /** Return the freeing function of the type, or generate a new one if necessary. */
    public FunctionDecl getFreeFunction() {
        if (this.freeFunction == null)
            this.freeFunction =
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_free"))),
                            "Generated function to free ``Self``",
                            new Role(RoleKind.FREE, getTypeExpr(), null),
                            buildMemberSymbol("_Default_Free"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    getTypeExpr().makePointer(false, false),
                                                    new Transfer(RequiredOwner.USER))),
                                    NativeType.VOID.typeExpr,
                                    Owner.UNKNOWN),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC);
        return this.freeFunction;
    }

    /** Return the copying function of the type, or generate a new one if necessary. */
    public FunctionDecl getCopyFunction() {
        if (this.copyFunction == null)
            this.copyFunction =
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_copy"))),
                            "Generated function to copy ``Other`` to ``Self``",
                            new Role(RoleKind.COPY, getTypeExpr(), null),
                            buildMemberSymbol("_Default_Copy"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    getTypeExpr().makeReference(false),
                                                    new Transfer(RequiredOwner.USER)),
                                            new Parameter(
                                                    Name.fromLower("other"),
                                                    getTypeExpr().makeReference(true),
                                                    new Transfer(RequiredOwner.USER))),
                                    NativeType.VOID.typeExpr,
                                    Owner.UNKNOWN),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC);
        return this.copyFunction;
    }

    private List<Parameter> constructorParameters(boolean withDefaults) {
        List<Parameter> res =
                getAllComponents().stream()
                        .filter(c -> withDefaults || !c.hasDefaultValue())
                        .map(
                                c ->
                                        new Parameter(
                                                c.name,
                                                c.getSetterType(),
                                                new Transfer(RequiredOwner.USER)))
                        .toList();
        return res;
    }

    /** Return the allocating functions of the type, or generate a new one if necessary. */
    public List<FunctionDecl> getAllocFunctions() {
        if (this.allocFunctions != null) return this.allocFunctions;
        this.allocFunctions = new ArrayList<>(2);
        if (!isAbstract()) {
            NameTypeExpr type = getTypeExpr();
            this.allocFunctions.add(
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Alloc"),
                            new FunctionTypeExpr(constructorParameters(true), type, Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Private types and types that do not thave default values for any of their component
            // do not need a second specialized constructor.
            if (!origin.pIsPrivate()
                    && getAllComponents().stream().anyMatch(c -> c.hasDefaultValue())) {
                this.allocFunctions.add(
                        new FunctionDecl(
                                getProxyFullyQualifiedName()
                                        .append(
                                                name.concat(
                                                        Name.fromLower(
                                                                "default_alloc_default_values"))),
                                "Generated function to alloc a "
                                        + name.toPascalWithUnderscore()
                                        + " with default values",
                                new Role(RoleKind.ALLOC, type, null),
                                buildMemberSymbol("_Default_Alloc_1"),
                                new FunctionTypeExpr(
                                        constructorParameters(false), type, Owner.USER),
                                FunctionDecl.Visibility.PUBLIC,
                                FunctionDecl.Overridability.FINAL,
                                FunctionDecl.Staticness.NON_STATIC));
            }
        }
        return this.allocFunctions;
    }

    /** Return the allocating function of the shadow type, or generate a new one if necessary. */
    public List<FunctionDecl> getShadowAllocFunctions() {
        // When the binded type is a tagged type, we need to be able to override it. In
        // order to allow for dynamic dispatch to dispatch to target language overriding
        // functions, we need to create shadow objects. These will contain the vtable to
        // know which Ada function is overriden for a given value.
        // These constructor are similar to the ones created above but also accept a pointer
        // to the object in the target, and a pointer to the vtable.
        if (shadowAllocFunctions == null) {
            shadowAllocFunctions = new ArrayList<>();
            NameTypeExpr type = getTypeExpr();
            this.shadowAllocFunctions.add(
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("shadow_default_alloc"))),
                            "Generated function to alloc a "
                                    + name.toPascalWithUnderscore()
                                    + "_Shadow",
                            new Role(RoleKind.SHADOW_ALLOC, type, null),
                            buildMemberSymbol("_Shadow_Default_Alloc"),
                            new FunctionTypeExpr(constructorParameters(true), type, Owner.USER),
                            // Shadow allocating functions should only be visible from children
                            // types.
                            FunctionDecl.Visibility.PROTECTED,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC));

            // Private types and types that do not thave default values for any of their component
            // do not need a second specialized constructor.
            if (!origin.pIsPrivate()
                    && getAllComponents().stream().anyMatch(c -> c.hasDefaultValue())) {
                this.shadowAllocFunctions.add(
                        new FunctionDecl(
                                getProxyFullyQualifiedName()
                                        .append(
                                                name.concat(
                                                        Name.fromLower(
                                                                "shadow_default_alloc_default_values"))),
                                "Generated function to alloc a "
                                        + name.toPascalWithUnderscore()
                                        + "_Shadow with default values",
                                new Role(RoleKind.SHADOW_ALLOC, type, null),
                                buildMemberSymbol("_Shadow_Default_Alloc_1"),
                                new FunctionTypeExpr(
                                        constructorParameters(false), type, Owner.USER),
                                // Shadow allocating functions should only be visible from children
                                // types.
                                FunctionDecl.Visibility.PROTECTED,
                                FunctionDecl.Overridability.FINAL,
                                FunctionDecl.Staticness.NON_STATIC));
            }
        }
        return shadowAllocFunctions;
    }

    /** Return the cloning function of the type, or generate a new one if necessary. */
    public FunctionDecl getCloneFunction() {
        if (this.cloneFunction == null) {
            NameTypeExpr type = getTypeExpr();
            this.cloneFunction =
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_clone"))),
                            "Generated function to clone a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Clone"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("self"),
                                                    type.makeReference(true),
                                                    new Transfer(RequiredOwner.USER))),
                                    type,
                                    Owner.USER),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC);
        }
        return this.cloneFunction;
    }

    /** Return the getters and setters of the type, or generate them if necessary. */
    public List<FunctionDecl> getGettersAndSetters() {
        if (componentAccessors == null) {
            componentAccessors = new ArrayList<>(components.size() * 2);

            for (var c : components) {
                TypeExpr componentTypeRef = AdaAPI.makeTypeExpr(c.getType());
                // Create the getter function.
                componentAccessors.add(
                        new FunctionDecl(
                                getProxyFullyQualifiedName()
                                        .append(Name.fromLower("get").concat(c.name)),
                                "Return the value of " + c.name.toPascalWithUnderscore(),
                                new Role(RoleKind.GETTER, getTypeExpr(), c.name),
                                buildMemberSymbol("_Getter_" + c.name.toPascalWithUnderscore()),
                                new FunctionTypeExpr(
                                        List.of(
                                                new Parameter(
                                                        Name.fromLower("self"),
                                                        getTypeExpr().makeReference(true),
                                                        new Transfer(RequiredOwner.ANY))),
                                        componentTypeRef.makeReference(false),
                                        Owner.STATIC),
                                FunctionDecl.Visibility.PUBLIC,
                                FunctionDecl.Overridability.FINAL,
                                FunctionDecl.Staticness.NON_STATIC));
                // Create the setter function.
                TypeExpr setterType = c.getSetterType();
                componentAccessors.add(
                        new FunctionDecl(
                                getProxyFullyQualifiedName()
                                        .append(Name.fromLower("set").concat(c.name)),
                                "Sets the value of " + c.name.toPascalWithUnderscore(),
                                new Role(RoleKind.SETTER, getTypeExpr(), c.name),
                                buildMemberSymbol("_Setter_" + c.name.toPascalWithUnderscore()),
                                new FunctionTypeExpr(
                                        List.of(
                                                new Parameter(
                                                        Name.fromLower("self"),
                                                        getTypeExpr().makeReference(false),
                                                        new Transfer(RequiredOwner.ANY)),
                                                new Parameter(
                                                        Name.fromLower("new").concat(c.name),
                                                        setterType,
                                                        new Transfer(RequiredOwner.ANY))),
                                        NativeType.VOID.typeExpr,
                                        Owner.UNKNOWN),
                                FunctionDecl.Visibility.PUBLIC,
                                FunctionDecl.Overridability.FINAL,
                                FunctionDecl.Staticness.NON_STATIC));
            }
        }
        return componentAccessors;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(origin);
    }

    public TypeDef getTypeDef() {
        return origin.fTypeDef();
    }

    @Override
    public String getDoc() {
        return origin.pDoc();
    }

    /**
     * Return a string of the extension aggrate necessary to construct a record when inheriting from
     * a private type, or an empty string.
     */
    public String extensionAggregate() {
        return Arrays.stream(origin.pFullView().pBaseTypes(Libadalang.AdaNode.NONE))
                .filter(b -> b.pIsPrivate())
                .findFirst()
                .map((b) -> b.pFullyQualifiedName() + " with")
                .orElse("");
    }

    public boolean isAbstract() {
        return origin.pIsAbstractType();
    }

    public boolean isTaggedType() {
        return origin.pIsTaggedType(Libadalang.AdaNode.NONE);
    }

    public boolean isControlled() {
        return AdaTypeMatcher.isControlledType(origin);
    }
}
