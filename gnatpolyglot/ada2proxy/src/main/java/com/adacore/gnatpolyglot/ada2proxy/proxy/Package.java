//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.ada2proxy.proxy;

import com.adacore.gnatpolyglot.ada2proxy.AdaAPI;
import com.adacore.gnatpolyglot.proxy.FullyQualifiedName;
import com.adacore.libadalang.Libadalang;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Package implements AdaProxyObject {

    /** Origin node in the LAL tree. */
    private final Libadalang.BasePackageDecl origin;

    /** Declarations contained in the package. */
    public List<AdaDeclaration> declarations;

    public Package(Libadalang.BasePackageDecl origin, List<AdaDeclaration> declarations) {
        this.origin = origin;
        this.declarations = declarations;
    }

    /** Return the fully qualified name of the package */
    public String getFullyQualifiedName() {
        return origin.pFullyQualifiedName();
    }

    @Override
    public <T> T accept(AdaProxyVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static boolean isAdaRuntimePackage(Libadalang.BasePackageDecl pack) {
        return switch (pack.pFullyQualifiedNameArray(false)[0].text) {
            case "ada" -> true;
            case "interfaces" -> true;
            case "system" -> true;
            default -> false;
        };
    }

    public boolean isAdaRuntimePackage() {
        return isAdaRuntimePackage(origin);
    }

    public static boolean isGenericInstantiation(Libadalang.BasePackageDecl pack) {
        return pack.pGenericInstantiations().length != 0;
    }

    /** Return whether ``pack`` is declared inline inside another package. */
    public static boolean isNestedPackage(Libadalang.BasePackageDecl pack) {
        Libadalang.AdaNode node = pack;
        Libadalang.GenericInstantiation[] instantiations = pack.pGenericInstantiations();
        if (instantiations.length != 0) {
            node = instantiations[0];
        }
        return Arrays.stream(node.parents(false))
                .anyMatch(p -> p instanceof Libadalang.PackageDecl);
    }

    public boolean isNestedPackage() {
        return isNestedPackage(origin);
    }

    /**
     * Return the fully qualified name of the library-level compilation unit that contains ``pack``.
     * For a nested package, this is the outermost library-level ancestor.
     */
    public static String getLibraryLevelAncestorFQN(Libadalang.BasePackageDecl pack) {
        Libadalang.GenericInstantiation[] instantiations = pack.pGenericInstantiations();
        Libadalang.AdaNode node = pack;
        if (instantiations.length != 0) {
            node = instantiations[0];
        }
        if (node.getUnit().getRoot() instanceof Libadalang.CompilationUnit cu
                && cu.fBody() instanceof Libadalang.LibraryItem li
                && li.pTopLevelDecl(node.getUnit()) instanceof Libadalang.BasicDecl outerDecl) {
            return outerDecl.pFullyQualifiedName();
        }
        return pack.pFullyQualifiedName();
    }

    /**
     * Return the package name to use in a {@code with} clause for ``decl``. This skips over nested
     * packages found in semantic parents, until a library-level PackageDecl or
     * GenericPackageInstantiation is found.
     */
    public static String getWithPackageName(Libadalang.BasicDecl decl) {
        Libadalang.BasicDecl current = decl;
        while (!(isLibraryLevelPackage(current)
                && (current instanceof Libadalang.PackageDecl
                        || current instanceof Libadalang.GenericPackageInstantiation))) {
            current = current.pParentBasicDecl();
        }
        return current.pFullyQualifiedName();
    }

    /**
     * Return the package nearest package that can be extended to create the proxy package for
     * ``decl``.
     */
    public static Libadalang.BasicDecl getExtensiblePackage(Libadalang.BasicDecl decl) {
        Libadalang.BasicDecl current = decl;
        while (!(isLibraryLevelPackage(current) && current instanceof Libadalang.PackageDecl)) {
            current = current.pParentBasicDecl();
        }
        return current;
    }

    /** Return whether ``decl`` is a top-level package declaration. */
    private static boolean isLibraryLevelPackage(Libadalang.BasicDecl decl) {
        if (decl.getUnit().getRoot() instanceof Libadalang.CompilationUnit cu
                && cu.fBody() instanceof Libadalang.LibraryItem li) {
            Libadalang.BasicDecl topDecl = li.pTopLevelDecl(decl.getUnit());
            return topDecl.pGetUninstantiatedNode().equals(decl.pGetUninstantiatedNode());
        }
        return false;
    }

    /** Instance variant of {@link #getWithPackageName(Libadalang.BasePackageDecl)}. */
    public String getWithPackage() {
        return getWithPackageName(origin);
    }

    public static String getProxyUnitName(Libadalang.BasePackageDecl pack) {
        String name = pack.pFullyQualifiedName();
        // User-defined descendants of package from the Ada runtime are not allowed: instead, make
        // them descendants of `X_Runtime`
        if (isAdaRuntimePackage(pack)) {
            if (name.startsWith("Ada")) return name.replaceFirst("Ada", "Ada_Runtime");
            if (name.startsWith("Interfaces"))
                return name.replaceFirst("Interfaces", "Interfaces_Runtime");
            if (name.startsWith("System")) return name.replaceFirst("System", "System_Runtime");
        }
        // Nested packages cannot be library units, so generate a child of the library-level
        // ancestor with underscores replacing the intermediate dots.
        if (isNestedPackage(pack) || isGenericInstantiation(pack)) {
            Libadalang.BasicDecl libraryParent = getExtensiblePackage(pack);
            if (libraryParent.getUnit().equals(libraryParent.pStandardUnit())) {
                return name.replace(".", "_") + "_Proxy_Package";
            }
            String libraryParentFQN = libraryParent.pFullyQualifiedName();
            String nestedPart = name.substring(libraryParentFQN.length() + 1);
            return libraryParentFQN + "." + nestedPart.replace(".", "_") + "_Proxy_Package";
        }
        return name.concat(".Proxy_Package");
    }

    public String getProxyUnitName() {
        return getProxyUnitName(origin);
    }

    public FullyQualifiedName getProxyFullyQualifiedName() {
        return AdaAPI.makeProxyFullyQualifiedName(origin);
    }

    public List<Libadalang.CompilationUnit> getUnitDependencies(boolean withSelf) {
        if (origin.getUnit().getRoot() instanceof Libadalang.CompilationUnit unit) {
            return Stream.concat(Stream.of(unit.pUnitDependencies()), Stream.of(unit)).toList();
        }
        return List.of();
    }

    public List<String> getRenamedDependencies() {
        if (origin.getUnit().getRoot() instanceof Libadalang.CompilationUnit unit) {
            return Stream.of(unit.pImportedUnits(false))
                    .filter(u -> u.pDecl() instanceof Libadalang.PackageRenamingDecl)
                    .map(u -> (Libadalang.PackageRenamingDecl) u.pDecl())
                    .map(u -> u.pFinalRenamedPackage().pFullyQualifiedName())
                    .toList();
        }
        return List.of();
    }
}
