package com.adacore.polyglot.ada2proxy.proxy;

import com.adacore.libadalang.Libadalang;
import com.adacore.polyglot.ada2proxy.AdaAPI;
import com.adacore.polyglot.proxy.FullyQualifiedName;
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
        ;
        return name.concat(".Proxy");
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
