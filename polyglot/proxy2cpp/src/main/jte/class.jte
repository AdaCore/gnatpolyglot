<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.polyglot.proxy2cpp.CppAPI
@param CppAPI api
@param ClassDecl classDecl
@param Boolean isSource

!{
    FunctionMembersEntry members = api.getMembers(classDecl);
    String className = classDecl.getLastName().toPascal();
    String inherits = "";
    String parent = "";
    if (classDecl.parent != null) {
        parent = api.cppTypename(classDecl.parent.asTypeExpr());
        inherits = ": public " + parent;
    }
}

@if(!isSource)
    class ${className} ${inherits}{
    public:
        explicit ${className}(void *data);
    @for(var alloc : members.allocFunctions)
        ${className}(${api.cppParameters(alloc)});
    @endfor
        ~${className}();

        @for(var m : members.memberFunctions)
            @template.function(api, m, isSource)
        @endfor

        void *data() const;

    public:
        struct view {
            view(void *data) : _data(data) {}

            ${className} *operator->() {
                return (${className} *)this;
            }

            operator ${className}&() {
                return *(${className} *)this;
            }
        private:
            void *_data;
        };

    @if(classDecl.parent == null)
    protected:
        void *_data;
    @endif
    };
@else
    @for(var alloc : members.allocFunctions)
        extern "C" void *${alloc.symbol}
            (${api.cParameters(alloc)});
        ${className}::${className}(${api.cppParameters(alloc)})
        @if(classDecl.parent == null)
            : _data(${api.callCSymbol(alloc)})
        @else
            : ${parent}(${api.callCSymbol(alloc)})
        @endif
        {}
    @endfor

    ${className}::${className}(void *data)
    @if(classDecl.parent == null)
        : _data(data)
    @else
        : ${parent}(data)
    @endif
    {}

    extern "C" void ${members.freeFunction.symbol}
        (${api.cParameters(members.freeFunction)});
    ${className}::~${className}() {
        ${members.freeFunction.symbol}(_data);
        _data = nullptr;
    }

    void *${className}::data() const {
        return this->_data;
    }
@endif
