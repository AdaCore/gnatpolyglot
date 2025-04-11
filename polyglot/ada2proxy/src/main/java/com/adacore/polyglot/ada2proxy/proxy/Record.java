package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.ClassDecl;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Reference;
import com.adacore.polyglot.proxy.Role;
import com.adacore.polyglot.proxy.Role.RoleKind;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import java.util.ArrayList;
import java.util.List;

public class Record extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.TypeDecl origin;

    /** List of all components. */
    public List<Component> components;

    /** Reference to the record. */
    private Reference ref;

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

    /** Create a reference to the current type. */
    public Reference getReference() {
        if (this.ref == null) this.ref = AdaAPI.makeReferenceTo(this.origin, false);
        return this.ref;
    }

    /** Return the component with the given name. */
    public Component getComponent(Name name) {
        return components.stream().filter(c -> c.name.equals(name)).findFirst().get();
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        StringBuilder symbolBuilder = new StringBuilder("_P_");
        symbolBuilder.append(getFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    /** Return the freeing function of the type, or generate a new one if necessary. */
    public FunctionDecl getFreeFunction() {
        if (this.freeFunction == null)
            this.freeFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_free"))),
                            "Generated function to free ``Self``",
                            new Role(RoleKind.FREE, getReference(), null),
                            buildMemberSymbol("_Default_Free"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            getReference().withIsPointer(true),
                                            new Transfer(RequiredOwner.USER))),
                            NativeType.VOID.reference,
                            Owner.UNKNOWN,
                            false,
                            false,
                            false);
        return this.freeFunction;
    }

    /** Return the allocating function of the type, or generate a new one if necessary. */
    public FunctionDecl getAllocFunction() {
        if (this.allocFunction == null) {
            Reference type = getReference();
            this.allocFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_alloc"))),
                            "Generated function to alloc a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
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
            Reference type = getReference();
            this.cloneFunction =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                    .append(name.concat(Name.fromLower("default_clone"))),
                            "Generated function to clone a " + name.toPascalWithUnderscore(),
                            new Role(RoleKind.ALLOC, type, null),
                            buildMemberSymbol("_Default_Clone"),
                            List.of(
                                    new Parameter(
                                            Name.fromLower("self"),
                                            type.withIsReference(true).withIsConst(true),
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
                Reference componentTypeRef = AdaAPI.makeReferenceTo(c.getType(), false);
                // Create the getter function.
                componentAccessors.add(
                        new FunctionDecl(
                                AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                        .append(Name.fromLower("get").concat(c.name)),
                                "Return the value of " + c.name.toPascalWithUnderscore(),
                                new Role(RoleKind.GETTER, getReference(), c.name),
                                buildMemberSymbol("_Getter_" + c.name.toPascalWithUnderscore()),
                                List.of(
                                        new Parameter(
                                                Name.fromLower("self"),
                                                getReference()
                                                        .withIsReference(true)
                                                        .withIsConst(true),
                                                new Transfer(RequiredOwner.ANY))),
                                componentTypeRef,
                                Owner.USER,
                                false,
                                false,
                                false));

                // Get the type of the setter's new value.
                Reference setterType = componentTypeRef;
                if (!c.getType().pIsScalarType(Libadalang.AdaNode.NONE))
                    // If the argument is not a scalar, get a const reference to the new value.
                    setterType = setterType.withIsConst(true).withIsReference(true);
                // Create the setter function.
                componentAccessors.add(
                        new FunctionDecl(
                                AdaAPI.makeProxyFullyQualifiedName(origin, false)
                                        .append(Name.fromLower("set").concat(c.name)),
                                "Sets the value of " + c.name.toPascalWithUnderscore(),
                                new Role(RoleKind.SETTER, getReference(), c.name),
                                buildMemberSymbol("_Setter_" + c.name.toPascalWithUnderscore()),
                                List.of(
                                        new Parameter(
                                                Name.fromLower("self"),
                                                getReference().withIsReference(true),
                                                new Transfer(RequiredOwner.ANY)),
                                        new Parameter(
                                                Name.fromLower("new").concat(c.name),
                                                setterType,
                                                new Transfer(RequiredOwner.ANY))),
                                NativeType.VOID.reference,
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

    @Override
    public ClassDecl toPolyglotProxy() {
        if (this.origin.fTypeDef() instanceof Libadalang.PrivateTypeDef def) {
            return new ClassDecl(
                    AdaAPI.makeProxyFullyQualifiedName(origin, false),
                    this.origin.pDoc(),
                    null,
                    8,
                    false,
                    List.of());
        }
        if (this.origin.fTypeDef() instanceof Libadalang.RecordTypeDef def) {
            return new ClassDecl(
                    AdaAPI.makeProxyFullyQualifiedName(origin, false),
                    this.origin.pDoc(),
                    null,
                    8,
                    false,
                    this.components.stream().map(Component::toPolyglotProxy).toList());
        }
        throw new UnsupportedOperationException(
                "Unsupported Ada type:" + this.origin.fTypeDef().getImage());
    }
}
