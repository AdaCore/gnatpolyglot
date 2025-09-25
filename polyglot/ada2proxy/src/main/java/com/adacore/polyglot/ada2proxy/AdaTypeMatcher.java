package com.adacore.polyglot.ada2proxy;

import static com.adacore.libadalang.Libadalang.AdaNode.NONE;

import com.adacore.libadalang.Libadalang;
import java.util.stream.Stream;

/** Collection of static methods to help identify complex types from Libadalang. */
public class AdaTypeMatcher {

    /** Return whether the BaseTypeDecl is a controlled type. */
    public static boolean isControlledType(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(Libadalang.AdaNode.NONE)
                .pFullyQualifiedName()
                .startsWith("Ada.Finalization.");
    }

    /** Return whether the subprogram is a controlled type primitive. */
    public static boolean isControlledPrimitve(Libadalang.BasicDecl subprogram) {
        return Stream.of(subprogram.pBaseSubpDeclarations(false))
                .anyMatch(d -> d.pFullyQualifiedName().startsWith("Ada.Finalization."));
    }

    /** Return whether the BaseTypeDecl is the Standard.String type, or an array of character. */
    public static boolean isStringType(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.equals(typeDecl.pStdStringType())
                || typeDecl.pCompType(true, NONE).equals(typeDecl.pStdCharType());
    }

    /** Return whether the BaseTypeDecl is the Standard.Character type. */
    public static boolean isCharacter(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.equals(typeDecl.pStdCharType());
    }

    /**
     * Return whether the BaseTypeDecl is an enumeration type, excluding the Standard.Character
     * type, since they are treated differently by polyglot.
     */
    public static boolean isEnum(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsEnumType(Libadalang.AdaNode.NONE) && !isCharacter(typeDecl);
    }
}
