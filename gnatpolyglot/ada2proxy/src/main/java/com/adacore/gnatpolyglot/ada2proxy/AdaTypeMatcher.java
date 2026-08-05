//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy;

import static com.adacore.libadalang.Libadalang.AdaNode.NONE;

import com.adacore.libadalang.Libadalang;
import java.util.stream.Stream;

/** Collection of static methods to help identify complex types from Libadalang. */
public class AdaTypeMatcher {

    /** Return whether the BaseTypeDecl is an access type to an array. */
    public static boolean isArrayAccess(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsAccessType(NONE)
                && !typeDecl.pAccessedType(typeDecl).isNone()
                && typeDecl.pAccessedType(typeDecl).pIsArrayType(NONE);
    }

    /** Return whether the BaseTypeDecl is a controlled type. */
    public static boolean isControlledType(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(typeDecl).pFullyQualifiedName().startsWith("Ada.Finalization.");
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
        return typeDecl.pIsArrayType(NONE) && isCharacter(typeDecl.pCompType(false, typeDecl));
    }

    /** Return whether the BaseTypeDecl is the Standard.Character type. */
    public static boolean isCharacter(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(typeDecl).equals(typeDecl.pStdCharType());
    }

    /**
     * Return whether the BaseTypeDecl is an enumeration type, excluding the Standard.Character
     * type, since they are treated differently by GNATpolyglot.
     */
    public static boolean isEnum(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsEnumType(NONE) && !isCharacter(typeDecl);
    }

    public static boolean isReturnedAsAddress(Libadalang.BaseTypeDecl typeDecl) {
        return isBindedAsClass(typeDecl) || typeDecl.pIsAccessType(NONE);
    }

    public static boolean isBindedAsClass(Libadalang.BaseTypeDecl typeDecl) {
        return isPrivate(typeDecl) || typeDecl.pIsRecordType(NONE) || typeDecl.pIsTaggedType(NONE);
    }

    public static boolean isPrivate(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pRootType(typeDecl).pIsPrivate() && !typeDecl.pIsTaggedType(NONE);
    }

    public static boolean isNonClassWideTagged(Libadalang.BaseTypeDecl typeDecl) {
        return typeDecl.pIsTaggedType(NONE) && !(typeDecl instanceof Libadalang.ClasswideTypeDecl);
    }

    public static boolean isNumber(Libadalang.BaseTypeDecl type) {
        return type.pIsScalarType(NONE) && !isEnum(type);
    }

    public static boolean isAccessToSubp(Libadalang.BaseTypeDecl type) {
        return type.pRootType(type) instanceof Libadalang.TypeDecl typeDecl
                && typeDecl.fTypeDef() instanceof Libadalang.AccessToSubpDef;
    }
}
