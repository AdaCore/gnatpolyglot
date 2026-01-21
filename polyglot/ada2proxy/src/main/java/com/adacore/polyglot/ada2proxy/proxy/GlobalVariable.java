package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.NativeType;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FunctionDecl;
import com.adacore.polyglot.proxy.FunctionTypeExpr;
import com.adacore.polyglot.proxy.Name;
import com.adacore.polyglot.proxy.Owner;
import com.adacore.polyglot.proxy.Parameter;
import com.adacore.polyglot.proxy.Transfer;
import com.adacore.polyglot.proxy.Transfer.RequiredOwner;
import com.adacore.polyglot.proxy.TypeExpr;
import java.util.List;

public class GlobalVariable extends AdaDeclaration {

    /** Origin node in the LAL tree. */
    private final Libadalang.ObjectDecl origin;

    /**
     * Specific defining name of this global variable.
     *
     * <p>Useful when the ``origin`` ObjectDecl has multiple defining names, to identify the
     * specific one that this instance represents.
     */
    private final Libadalang.DefiningName definingName;

    private FunctionDecl getter;

    private FunctionDecl setter;

    public GlobalVariable(Libadalang.ObjectDecl origin, Libadalang.DefiningName dn, Name name) {
        super(name);
        this.origin = origin;
        this.definingName = dn;
    }

    /** Create a symbol for generated member functions. */
    private String buildMemberSymbol(String suffix) {
        // The 3rd character of symbols for polyglot genererated functions is "G" (for Generated)
        StringBuilder symbolBuilder = new StringBuilder("_PG");
        symbolBuilder.append(getFullyQualifiedName().replace(".", "_")).append(suffix);
        return symbolBuilder.toString();
    }

    public String getFullyQualifiedName() {
        return definingName.pFullyQualifiedName();
    }

    public Libadalang.BaseTypeDecl getType() {
        return origin.fTypeExpr().pDesignatedTypeDecl();
    }

    public FunctionDecl getGetter() {
        if (getter == null) {
            getter =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(definingName)
                                    .append(Name.fromLower("get").concat(name)),
                            "Return a reference to " + getFullyQualifiedName(),
                            null,
                            buildMemberSymbol("_Getter"),
                            new FunctionTypeExpr(
                                    List.of(),
                                    AdaAPI.makeTypeExpr(origin.fTypeExpr())
                                            .makeReference(origin.pIsConstantObject()),
                                    Owner.STATIC),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC);
        }
        return getter;
    }

    public FunctionDecl getSetter() {
        if (setter == null && !origin.pIsConstantObject()) {
            // Get the type of the setter's new value.
            TypeExpr setterType = AdaAPI.makeTypeExpr(getType());
            if (!getType().pIsScalarType(Libadalang.AdaNode.NONE))
                // If the argument is not a scalar, get a const reference to the new value.
                setterType = setterType.makeReference(true);
            setter =
                    new FunctionDecl(
                            AdaAPI.makeProxyFullyQualifiedName(definingName)
                                    .append(Name.fromLower("set").concat(name)),
                            "Set the value of " + getFullyQualifiedName(),
                            null,
                            buildMemberSymbol("_Setter"),
                            new FunctionTypeExpr(
                                    List.of(
                                            new Parameter(
                                                    Name.fromLower("new_value"),
                                                    setterType,
                                                    new Transfer(RequiredOwner.ANY))),
                                    NativeType.VOID.typeExpr,
                                    Owner.STATIC),
                            FunctionDecl.Visibility.PUBLIC,
                            FunctionDecl.Overridability.FINAL,
                            FunctionDecl.Staticness.NON_STATIC);
        }
        return setter;
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getDoc() {
        return origin.pDoc();
    }
}
