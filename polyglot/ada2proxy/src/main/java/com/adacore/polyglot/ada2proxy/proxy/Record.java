package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.TypeDef;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
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

    /** Default cloning function of the type. */
    private FunctionDecl cloneFunction;

    /** Default getter and setter functions of the type. */
    private ArrayList<FunctionDecl> componentAccessors;

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
                            false,
                            false,
                            false);
        return this.freeFunction;
    }

    /** Return the allocating functions of the type, or generate a new one if necessary. */
    public List<FunctionDecl> getAllocFunctions() {
        if (this.allocFunctions != null) return this.allocFunctions;
        this.allocFunctions = new ArrayList<>(2);
        if (!isAbstract()) {
            NameTypeExpr type = getTypeExpr();
            List<Component> allComponents = getAllComponents();
            this.allocFunctions.add(
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Alloc"),
                            new FunctionTypeExpr(
                                    allComponents.stream()
                                            .map(
                                                    c ->
                                                            new Parameter(
                                                                    c.name,
                                                                    c.getSetterType(),
                                                                    new Transfer(
                                                                            RequiredOwner.USER)))
                                            .toList(),
                                    type,
                                    Owner.USER),
                            false,
                            false,
                            false));

            // Private types and types that do not thave default values for any of their component
            // do not need a second specialized constructor.
            if (!origin.pIsPrivate() && allComponents.stream().anyMatch(c -> c.hasDefaultValue())) {
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
                                        allComponents.stream()
                                                .filter(c -> !c.hasDefaultValue())
                                                .map(
                                                        c ->
                                                                new Parameter(
                                                                        c.name,
                                                                        c.getSetterType(),
                                                                        new Transfer(
                                                                                RequiredOwner
                                                                                        .USER)))
                                                .toList(),
                                        type,
                                        Owner.USER),
                                false,
                                false,
                                false));
            }
        }
        return this.allocFunctions;
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
                            false,
                            false,
                            false);
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
                                                        getTypeExpr().makeReference(false),
                                                        new Transfer(RequiredOwner.ANY))),
                                        componentTypeRef.makeReference(false),
                                        Owner.STATIC),
                                false,
                                false,
                                false));
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
                                false,
                                false,
                                false));
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
}
