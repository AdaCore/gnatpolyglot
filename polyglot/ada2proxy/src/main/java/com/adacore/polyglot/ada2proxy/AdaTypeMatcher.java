package com.adacore.polyglot.ada2proxy;

import static com.adacore.libadalang.Libadalang.AdaNode.NONE;

import com.adacore.libadalang.Libadalang;
import java.util.stream.Stream;

/** Collection of static methods to help identify complex types from Libadalang. */
public class AdaTypeMatcher {

    /** Return whether the BaseTypeDecl is an access type to an array. */
    public static boolean isArrayAccess(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsAccessType(NONE) && typeDecl.pAccessedType(NONE).pIsArrayType(NONE);
    }

    /** Return whether the BaseTypeDecl is a controlled type. */
    public static boolean isControlledType(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(Libadalang.AdaNode.NONE)
                .pFullyQualifiedName()
                .startsWith("Ada.Finalization.");
    }

    /** Return whether the subprogram is a controlled type primitive. */
    public static boolean isControlledPrimitve(Libadalang.BasicDecl subprogram) {
        return Stream.of(subprogram.pBaseSubpDeclarations(false))
                .anyMatch(
                        d ->
                                d.pFullyQualifiedName().startsWith("Ada.Finalization.")
                                        && !(d instanceof Libadalang.SyntheticSubpDecl));
    }

    /** Return whether the BaseTypeDecl is the Standard.String type, or an array of character. */
    public static boolean isStringType(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsArrayType(Libadalang.AdaNode.NONE)
                && isCharacter(typeDecl.pCompType(false, NONE));
    }

    /** Return whether the BaseTypeDecl is the Standard.Character type. */
    public static boolean isCharacter(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(Libadalang.AdaNode.NONE).equals(typeDecl.pStdCharType());
    }

    /**
     * Return whether the BaseTypeDecl is an enumeration type, excluding the Standard.Character
     * type, since they are treated differently by polyglot.
     */
    public static boolean isEnum(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsEnumType(Libadalang.AdaNode.NONE) && !isCharacter(typeDecl);
    }

    public static boolean isReturnedAsAddress(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsPrivate()
                || typeDecl.pIsRecordType(Libadalang.AdaNode.NONE)
                || typeDecl.pIsAccessType(Libadalang.AdaNode.NONE);
    }

    public static boolean isNonClassWideTagged(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsTaggedType(NONE) && !(typeDecl instanceof Libadalang.ClasswideTypeDecl);
    }

    public static boolean isNumber(Libadalang.BaseTypeDecl type) {
        return type.pIsScalarType(NONE) && !isEnum(type);
    }
}
