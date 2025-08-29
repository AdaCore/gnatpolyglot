<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.EnumerationDecl
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param EnumerationDecl enumDecl

    enum class ${enumDecl.getLastName().toPascal()} {
    @for(var lit : enumDecl.items)
        ${lit.name.toUpper()} = ${String.valueOf(lit.value)},
    @endfor
    };
