package com.adacore.polyglot.ada2proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.libadalang.Libadalang.BaseTypeDecl;
import com.adacore.polyglot.NativeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BindableDeclChecker {

    private Set<Libadalang.BasicDecl> unbindableDecls = new HashSet<>();

    private Set<Libadalang.BasicDecl> bindableDecls = new HashSet<>();

    private void checkType(Libadalang.BaseTypeDecl decl) {

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

        if (decl.pParentBasicDecl() instanceof Libadalang.GenericPackageDecl)
            throw new UnbindableDeclException(
                    decl, "Instantiated generic packages are not yet supported");
        else if (decl.pIsInterfaceType(Libadalang.AdaNode.NONE))
            throw new UnbindableDeclException(decl, "Interfaces are not yet supported");

        // Check that we are able to compute the size of a given scalar type.
        else if (AdaTypeMatcher.isNumber(decl)) {
            if (decl.pIsFixedPoint(Libadalang.AdaNode.NONE))
                throw new UnbindableDeclException(decl, "Fixed-point types are not supported");
            try {
                NativeType nativeType = AdaAPI.checkNativeType(decl);
                if (nativeType.equals(NativeType.UINT128) || nativeType.equals(NativeType.SINT128))
                    throw new UnbindableDeclException(decl, "Unsupported integer size (128)");
            } catch (Libadalang.LangkitException e) {
                throw new UnbindableDeclException(decl, e);
            }
        } else if (decl instanceof Libadalang.AnonymousTypeDecl)
            throw new UnbindableDeclException(
                    decl, "Anonymous type declarations are not yet supported");
        else if (decl.pIsAccessType(Libadalang.AdaNode.NONE)) {
            if (decl.pRootType(decl) instanceof Libadalang.TypeDecl typeDecl
                    && typeDecl.fTypeDef() instanceof Libadalang.AccessToSubpDef)
                throw new UnbindableDeclException(
                        decl, "Access to subprograms are not yet supported");
            Libadalang.BaseTypeDecl accessedType = decl.pAccessedType(decl);
            if (accessedType.pIsClasswide())
                throw new UnbindableDeclException(
                        decl, "Access to classwide types are not yet supported");
            if (accessedType.pIsAccessType(Libadalang.AdaNode.NONE))
                throw new UnbindableDeclException(
                        decl, "Access to access types are not yet supported");

            checkUse(decl, accessedType);
        } else if (decl.pIsArrayType(Libadalang.AdaNode.NONE)) {
            checkUse(decl, decl.pCompType(false, decl));
        } else if (decl.equals(decl.pStdWideWideCharType())
                || decl.equals(decl.pStdWideCharType())) {
            throw new UnbindableDeclException(
                    decl, "Wide and Wide_Wide characters types are not supported");
        }
    }

    private void checkDecl(Libadalang.BasicDecl decl) {
        if (bindableDecls.contains(decl) || unbindableDecls.contains(decl)) return;

        // assume that decl is bindable (to avoid infinite loops)
        bindableDecls.add(decl);

        if (Arrays.stream(decl.pDefiningNames())
                .filter(d -> !d.isNone())
                .anyMatch(Libadalang.DefiningName::pIsGhostCode)) {
            throw new UnbindableDeclException(decl, "Ghost code declarations are not bindable");
        }

        if (decl instanceof Libadalang.GenericDecl gen) {
            throw new UnbindableDeclException(decl, "Generic declarations are not bindable");
        }

        if (decl.pIsSubprogram()) {
            Libadalang.BaseSubpSpec spec = decl.pSubpSpecOrNull(true);
            for (var paramType : spec.pParamTypes(decl)) {
                checkUse(decl, paramType);
            }
            Libadalang.BaseTypeDecl returnType = spec.pReturnType(decl);
            if (!returnType.isNone()) {
                if (returnType.pIsClasswide())
                    throw new UnbindableDeclException(
                            decl, "Returning class wide object is not yet supported");
                checkUse(decl, returnType);
            }
        }

        if (decl instanceof Libadalang.ObjectDecl obj) {
            checkUse(decl, obj.fTypeExpr().pDesignatedTypeDecl());
        }

        if (decl instanceof Libadalang.BaseTypeDecl typeDecl) {
            checkType(typeDecl);
        }
    }

    private void checkUse(Libadalang.BasicDecl decl, Libadalang.BaseTypeDecl use) {
        Throwable cause = null;
        try {
            checkIsBindable(use);
        } catch (Throwable t) {
            cause = t;
        } finally {
            if (unbindableDecls.contains(use))
                throw new UnbindableDeclException(
                        decl,
                        "Used type `%s` is not bindable".formatted(AdaAPI.getDisplayName(use)),
                        cause);
        }
    }

    public void checkIsBindable(Libadalang.BasicDecl decl) {
        // There is not enough information in incomplete types, so we must skip them.
        if (decl instanceof Libadalang.IncompleteTypeDecl) return;

        try {
            checkDecl(decl);
        } catch (UnbindableDeclException e) {
            // decl may have been assumed bindable
            bindableDecls.remove(decl);
            unbindableDecls.add(decl);
            throw e;
        }
        bindableDecls.add(decl);
    }

    public boolean seenUnbindable(Libadalang.BasicDecl decl) {
        return unbindableDecls.contains(decl);
    }
}
