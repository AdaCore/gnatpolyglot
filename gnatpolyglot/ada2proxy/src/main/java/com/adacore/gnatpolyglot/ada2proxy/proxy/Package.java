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
    private final Libadalang.PackageDecl origin;

    /** Declarations contained in the package. */
    public List<AdaDeclaration> declarations;

    public Package(Libadalang.PackageDecl origin, List<AdaDeclaration> declarations) {
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

    /** Return whether ``pack`` is declared inline inside another package. */
    public static boolean isNestedPackage(Libadalang.BasePackageDecl pack) {
        return Arrays.stream(pack.parents(false))
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
        if (pack.getUnit().getRoot() instanceof Libadalang.CompilationUnit cu
                && cu.fBody() instanceof Libadalang.LibraryItem li
                && li.pTopLevelDecl(pack.getUnit()) instanceof Libadalang.PackageDecl outerDecl) {
            return outerDecl.pFullyQualifiedName();
        }
        return pack.pFullyQualifiedName();
    }

    /**
     * Return the package name to use in a {@code with} clause for ``pack``. For nested packages
     * this is the library-level ancestor; for library-level packages it is the FQN itself.
     */
    public static String getWithPackageName(Libadalang.BasePackageDecl pack) {
        if (isNestedPackage(pack)) return getLibraryLevelAncestorFQN(pack);
        return pack.pFullyQualifiedName();
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
        if (isNestedPackage(pack)) {
            String libraryParentFQN = getLibraryLevelAncestorFQN(pack);
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
