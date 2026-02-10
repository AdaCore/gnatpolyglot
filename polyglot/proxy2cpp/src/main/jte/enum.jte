<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.EnumerationDecl
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param EnumerationDecl enumDecl

    enum class ${enumDecl.getLastName().toPascal()}: ${api.getEnumSizeType(enumDecl)} {
    @for(var lit : enumDecl.items)
        ${lit.name.toUpper()} = ${String.valueOf(lit.value)},
    @endfor
    };

#if __cplusplus >= 201103L
    constexpr const
    std::array<${enumDecl.getLastName().toPascal()}, ${enumDecl.items.size()}>
    ${enumDecl.getLastName().toLower()}_values = {
    @for(var lit : enumDecl.items)
        ${enumDecl.getLastName().toPascal()}::${lit.name.toUpper()},
    @endfor
    };
#endif
