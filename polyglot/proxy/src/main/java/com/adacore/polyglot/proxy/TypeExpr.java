package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NameTypeExpr.class, name = "typename"),
    @JsonSubTypes.Type(value = ReferenceTypeExpr.class, name = "reference"),
    @JsonSubTypes.Type(value = ArrayTypeExpr.class, name = "array"),
    @JsonSubTypes.Type(value = PointerTypeExpr.class, name = "pointer"),
    @JsonSubTypes.Type(value = FunctionTypeExpr.class, name = "function")
})
/** Base class to represent type expressions. */
public abstract class TypeExpr implements ProxyObject {

    /** Return the name of the type. */
    public abstract FullyQualifiedName getName();

    /** Create a {@link PointerTypeExpr} to the current type. */
    public PointerTypeExpr makePointer(boolean isConst, boolean isNonNull) {
        return new PointerTypeExpr(this, isConst, isNonNull);
    }

    /** Create a {@link ReferenceTypeExpr} to the current type. */
    public ReferenceTypeExpr makeReference(boolean isConst) {
        return new ReferenceTypeExpr(this, isConst);
    }

    /** Create an {@link ArrayTypeExpr} to the current type. */
    public ArrayTypeExpr makeArray() {
        return new ArrayTypeExpr(this);
    }

    /** Return whether the type is constant. */
    public boolean isConst() {
        return (this instanceof ReferenceTypeExpr ref && ref.isConst)
                || (this instanceof PointerTypeExpr ptr && ptr.isConst);
    }
}
