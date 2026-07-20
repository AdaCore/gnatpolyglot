//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy;

import com.adacore.gnatpolyglot.NativeType;
import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BindableDeclChecker {

    private enum CheckStatus {
        OK,
        UNSURE
    }

    private Set<Libadalang.BasicDecl> unbindableDecls = new HashSet<>();

    private Set<Libadalang.BasicDecl> bindableDecls = new HashSet<>();

    private Set<Libadalang.BasicDecl> visitedDecls = new HashSet<>();

    private void checkIsImplemented(Libadalang.BasicDecl decl) {
        if (decl instanceof Libadalang.PackageDecl) {
            if (decl.pHasAspect(Libadalang.Symbol.create("Unimplemented_Unit"), false, false))
                throw new UnbindableDeclException(decl, "Package is not implemented");
        } else {
            try {
                checkIsImplemented(decl.pParentBasicDecl());
            } catch (UnbindableDeclException e) {
                throw new UnbindableDeclException(decl, "Parent package is not implemented", e);
            }
        }
    }

    private CheckStatus checkType(Libadalang.BaseTypeDecl decl) {

        if (decl.pDiscriminantsList(Libadalang.BaseTypeDecl.NONE, Libadalang.AdaNode.NONE).length
                != 0) {
            throw new UnbindableDeclException(decl, "Discriminated types are not yet supported");
        }

        if (decl.pIsTaggedType(Libadalang.AdaNode.NONE)) {
            BaseTypeDecl baseType = decl.pBaseType(decl);
            if (!baseType.isNone()) checkUse(decl, baseType);
        }

        if (decl.pIsRecordType(Libadalang.AdaNode.NONE)) {
            for (var shape : decl.pShapes(false, decl)) {
                for (var comp : shape.components) {
                    checkUse(decl, comp.pFormalType(decl));
                }
            }
        }

        // For other type, we must ignore any type derivation
        decl = decl.pRootType(decl);

        if (decl.pIsInterfaceType(Libadalang.AdaNode.NONE))
            throw new UnbindableDeclException(decl, "Interfaces are not yet supported");

        // Check that we are able to compute the size of a given scalar type.
        else if (AdaTypeMatcher.isNumber(decl)) {
            if (decl.pIsFixedPoint(Libadalang.AdaNode.NONE))
                throw new UnbindableDeclException(decl, "Fixed-point types are not supported");
            try {
                NativeType nativeType = AdaAPI.checkNativeType(decl);
                if (nativeType.equals(NativeType.UINT128)
                        || nativeType.equals(NativeType.SINT128)
                        || nativeType.equals(NativeType.FLOAT128))
                    throw new UnbindableDeclException(decl, "Unsupported scalar size (128)");
            } catch (Libadalang.LangkitException e) {
                throw new UnbindableDeclException(decl, e);
            }
        } else if (decl instanceof Libadalang.AnonymousTypeDecl) {
            throw new UnbindableDeclException(
                    decl, "Anonymous type declarations are not yet supported");
        } else if (decl.pIsAccessType(Libadalang.AdaNode.NONE)) {
            if (decl.pRootType(decl) instanceof Libadalang.TypeDecl typeDecl
                    && typeDecl.fTypeDef() instanceof Libadalang.AccessToSubpDef)
                throw new UnbindableDeclException(
                        decl, "Access to subprograms are not yet supported");
            Libadalang.BaseTypeDecl accessedType =
                    (Libadalang.BaseTypeDecl)
                            decl.pAccessedType(decl).pMostVisiblePart(decl, false);
            if (accessedType.pIsClasswide())
                throw new UnbindableDeclException(
                        decl, "Access to classwide types are not yet supported");
            if (accessedType.pIsAccessType(Libadalang.AdaNode.NONE))
                throw new UnbindableDeclException(
                        decl, "Access to access types are not yet supported");
            if (accessedType.pIsScalarType(Libadalang.AdaNode.NONE))
                throw new UnbindableDeclException(
                        decl, "Access to scalar types are not yet supported");
            if (AdaTypeMatcher.isArrayAccess(decl)) {
                checkUse(decl, decl.pCompType(false, decl));
                if (decl.pHasAspect(Libadalang.Symbol.create("size"), false, false))
                    throw new UnbindableDeclException(
                            decl, "Array accesses with the Size aspect are not bindable");
            }

            return checkUse(decl, accessedType);
        } else if (decl.pIsArrayType(Libadalang.AdaNode.NONE)) {
            BaseTypeDecl compType = decl.pCompType(false, decl);
            checkUse(decl, compType);
            if (decl.pIndexType(0, decl).pIsEnumType(decl)) {
                throw new UnbindableDeclException(
                        decl, "Arrays indexed by enumeration types are not bindable");
            }
            if (!AdaTypeMatcher.isStringType(decl)) {
                if (decl.pHasAspect(Libadalang.Symbol.create("component_size"), false, false))
                    throw new UnbindableDeclException(
                            decl, "Arrays with the Component_Size aspect are not bindable");
                if (decl.pHasAspect(Libadalang.Symbol.create("pack"), false, false))
                    throw new UnbindableDeclException(decl, "Packed array are not bindable");
            }
            if (compType.pIsArrayType(decl) || AdaTypeMatcher.isArrayAccess(compType)) {
                throw new UnbindableDeclException(
                        decl, "Arrays of array or access to arrays are not yet supported");
            }
        } else if (decl.equals(decl.pStdWideWideCharType())
                || decl.equals(decl.pStdWideCharType())) {
            throw new UnbindableDeclException(
                    decl, "Wide and Wide_Wide characters types are not supported");
        }
        return CheckStatus.OK;
    }

    private CheckStatus checkDecl(Libadalang.BasicDecl decl) {
        if (bindableDecls.contains(decl) || unbindableDecls.contains(decl)) return CheckStatus.OK;

        visitedDecls.add(decl);

        checkIsImplemented(decl);

        for (var d : decl.pDefiningNames()) {
            if (d.isNone()) continue;
            Libadalang.Symbol symbol = Libadalang.Symbol.create("Import");
            Libadalang.PragmaNode pragma = d.pGetPragma(symbol);
            if (!pragma.isNone() && pragma.fId().pNameIs(symbol)) {
                if (((Libadalang.BaseAssoc) pragma.fArgs().getChild(0))
                        .pAssocExpr()
                        .getText()
                        .toLowerCase()
                        .equals("intrinsic"))
                    throw new UnbindableDeclException(decl, "Intrinsics are not bindable");
            }
        }

        if (Arrays.stream(decl.pDefiningNames())
                .filter(d -> !d.isNone())
                .anyMatch(Libadalang.DefiningName::pIsGhostCode)) {
            throw new UnbindableDeclException(decl, "Ghost code declarations are not bindable");
        }

        if (decl instanceof Libadalang.GenericDecl
                || decl instanceof Libadalang.GenericRenamingDecl) {
            throw new UnbindableDeclException(decl, "Generic declarations are not bindable");
        }

        if (decl.pIsSubprogram()) {
            Libadalang.BaseSubpSpec spec = decl.pSubpSpecOrNull(true);
            for (var paramType : spec.pParamTypes(decl)) {
                checkUse(decl, paramType);
            }
            for (var param : spec.pAbstractFormalParams()) {
                if (param instanceof Libadalang.ParamSpec paramSpec
                        && paramSpec.pFormalType(spec).pIsAccessType(spec)
                        && paramSpec.pFormalType(spec).pAccessedType(spec).pIsTaggedType(spec)
                        && (paramSpec.fMode() instanceof Libadalang.ModeOut
                                || paramSpec.fMode() instanceof Libadalang.ModeInOut)) {
                    throw new UnbindableDeclException(
                            decl,
                            "Returning access to tagged types through out parameters is not yet"
                                    + " supported");
                }
            }
            if ((decl.pHasAspect(Libadalang.Symbol.create("Pre'Class"), false, false)
                            || decl.pHasAspect(
                                    Libadalang.Symbol.create("Post'Class"), false, false))
                    && spec.pPrimitiveSubpTaggedType(false).pIsAbstractType())
                throw new UnbindableDeclException(
                        decl,
                        "Primitives of abstract types with classwide dynamic pre/post conditions"
                                + " are not bindable");
            Libadalang.BaseTypeDecl returnType = spec.pReturnType(decl);
            if (!returnType.isNone()) {
                if (returnType.pIsClasswide())
                    throw new UnbindableDeclException(
                            decl, "Returning class wide object is not yet supported");
                checkUse(decl, returnType);
                if (returnType.pIsAccessType(spec)
                        && returnType.pAccessedType(spec).pIsTaggedType(spec))
                    throw new UnbindableDeclException(
                            decl, "Returning access to tagged types is not yet supported");
            }
            if (decl instanceof Libadalang.AbstractSubpDecl
                    && spec.pPrimitiveSubpTaggedType(false).isNone()) {
                throw new UnbindableDeclException(decl, "Cannot bind disabled declarations");
            }
        }

        if (decl instanceof Libadalang.ObjectDecl obj) {
            checkUse(decl, obj.fTypeExpr().pDesignatedTypeDecl());
        }

        if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            return checkType(typeDecl);
        }
        return CheckStatus.OK;
    }

    private CheckStatus checkUse(Libadalang.BasicDecl decl, Libadalang.BaseTypeDecl use) {
        Throwable cause = null;
        if (use instanceof Libadalang.IncompleteTypeDecl) use = use.pNextPart();
        // Do NOT check types in the private part. Due to inheritance, it is possible to to visit
        // subprograms in the private part of packages. We do not want to have private visibility
        // over the types used, so if a previous part exists, use it instead.
        Libadalang.BaseTypeDecl previousPart = use.pPreviousPart(false);
        if (!previousPart.isNone()) use = previousPart;
        try {
            return checkIsBindableInternal(use);
        } catch (Throwable t) {
            cause = t;
            return CheckStatus.OK;
        } finally {
            if (unbindableDecls.contains(use))
                throw new UnbindableDeclException(
                        decl,
                        "Used type `%s` is not bindable".formatted(AdaAPI.getDisplayName(use)),
                        cause);
        }
    }

    public CheckStatus checkIsBindableInternal(Libadalang.BasicDecl decl) {
        if (visitedDecls.contains(decl)) return CheckStatus.UNSURE;
        try {
            if (checkDecl(decl) == CheckStatus.OK) bindableDecls.add(decl);
            return CheckStatus.OK;
        } catch (UnbindableDeclException e) {
            unbindableDecls.add(decl);
            throw e;
        } finally {
            visitedDecls.remove(decl);
        }
    }

    public void checkIsBindable(Libadalang.BasicDecl decl) {
        // There is not enough information in incomplete types, instead of getting the full view of
        // the type here, simply wait until we eventually encounter it.
        if (decl instanceof Libadalang.IncompleteTypeDecl) return;
        checkIsBindableInternal(decl);
    }

    public boolean seenUnbindable(Libadalang.BasicDecl decl) {
        return unbindableDecls.contains(decl);
    }
}
