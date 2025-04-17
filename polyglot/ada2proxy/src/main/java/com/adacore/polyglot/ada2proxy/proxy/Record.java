package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.TypeDef;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
import com.adacore.polyglot.proxy.FunctionDecl;
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
import java.util.List;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    /** List of all components. */
    public List<Component> components;

    /** TypeExpr to the record. */
    private NameTypeExpr ref;

    /** Default freeing function of the type. */
    private FunctionDecl freeFunction;

    /** Default allocating function of the type. */
    private FunctionDecl allocFunction;

    /** Default cloning function of the type. */
    private FunctionDecl cloneFunction;

    /** Default getter and setter functions of the type. */
    private ArrayList<FunctionDecl> componentAccessors;

    public Record(Libadalang.TypeDecl origin, Name name, List<Component> components) {
        super(name);
        this.origin = origin;
        this.components = components;
    }

    /** Return the fully qualified name of the type. */
    public String getFullyQualifiedName() {
        return this.origin.pFullyQualifiedName();
    }

    /** Get a {@link TypeExpr} to the current type. */
    public NameTypeExpr getTypeExpr() {
        if (this.ref == null) this.ref = AdaAPI.makeTypeExpr(this.origin);
        return this.ref;
    }

    /** Return the component with the given name. */
    public Component getComponent(Name name) {
        return components.stream().filter(c -> c.name.equals(name)).findFirst().get();
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
                            new Role(RoleKind.FREE, getTypeExpr().name, null),
                            buildMemberSymbol("_Default_Free"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            getTypeExpr().makePointer(false, false),
                                            new Transfer(RequiredOwner.USER))),
                            NativeType.VOID.typeExpr,
                            Owner.UNKNOWN,
                            false,
                            false,
                            false);
        return this.freeFunction;
    }

    /** Return the allocating function of the type, or generate a new one if necessary. */
    public FunctionDecl getAllocFunction() {
        if (this.allocFunction == null) {
            NameTypeExpr type = getTypeExpr();
            this.allocFunction =
                    new FunctionDecl(
                            getProxyFullyQualifiedName()
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type.name, null),
                            buildMemberSymbol("_Default_Alloc"),
                            List.of(),
                            type,
                            Owner.USER,
                            false,
                            false,
                            false);
        }
        return this.allocFunction;
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
                            new Role(RoleKind.ALLOC, type.name, null),
                            buildMemberSymbol("_Default_Clone"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            type.makeReference(true),
                                            new Transfer(RequiredOwner.USER))),
                            type,
                            Owner.USER,
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
                                new Role(RoleKind.GETTER, getTypeExpr().name, c.name),
                                buildMemberSymbol("_Getter_" + c.name.toPascalWithUnderscore()),
                                List.of(
                                        new Parameter(
                                                Name.fromLower("self"),
                                                getTypeExpr().makeReference(false),
                                                new Transfer(RequiredOwner.ANY))),
                                componentTypeRef.makeReference(false),
                                Owner.STATIC,
                                false,
                                false,
                                false));

                // Get the type of the setter's new value.
                TypeExpr setterType = componentTypeRef;
                if (!c.getType().pIsScalarType(Libadalang.AdaNode.NONE))
                    // If the argument is not a scalar, get a const reference to the new value.
                    setterType = setterType.makeReference(true);
                // Create the setter function.
                componentAccessors.add(
                        new FunctionDecl(
                                getProxyFullyQualifiedName()
                                        .append(Name.fromLower("set").concat(c.name)),
                                "Sets the value of " + c.name.toPascalWithUnderscore(),
                                new Role(RoleKind.SETTER, getTypeExpr().name, c.name),
                                buildMemberSymbol("_Setter_" + c.name.toPascalWithUnderscore()),
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
                                Owner.UNKNOWN,
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
}
