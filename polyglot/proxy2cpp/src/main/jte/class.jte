<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.FunctionDecl.Visibility
@import com.adacore.polyglot.proxy.ProxyContext.FunctionMembersEntry
@import com.adacore.polyglot.proxy.VTableEntry
@import com.adacore.polyglot.proxy2cpp.CppAPI
@import java.util.HashMap
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

    protected:
    @for(var alloc : members.allocFunctions)
        @if(alloc.visibility == Visibility.PROTECTED)
            ${className}(${api.cppParameters(alloc)});
        @endif
    @endfor

    @for(var m : members.memberFunctions)
        @if(m.visibility == Visibility.PROTECTED)
            @template.function(api, m, isSource)
        @endif
    @endfor

    public:
        explicit ${className}(void *data);
    @for(var alloc : members.allocFunctions)
        @if(alloc.visibility == Visibility.PUBLIC)
            ${className}(${api.cppParameters(alloc)});
        @endif
    @endfor
        virtual ~${className}();

    @if(members.copyFunction != null)
        ${className} &operator=(${api.cppParameters(members.copyFunction)});
    @endif

    @for(var m : members.memberFunctions)
        @if(m.visibility == Visibility.PUBLIC)
            @template.function(api, m, isSource)
        @endif
    @endfor

        void *data() const;
        void *release();

        virtual ${className} *internal_clone(void *data);

        template <typename U>
        friend class polyglot::polyglot_ptr;
    private:

        bool is_shadow();

    public:
        struct view;

    @if(classDecl.parent == null)
    protected:
        void *_data;
    @endif
    };

    struct ${className}::view {
        view(void *data) {
            new (&this->_data) ${className}(data);
        }
    public:
        static inline view create(void *data) {
            return view(data);
        }

        static inline const view create(const void *data) {
            return view(const_cast<void *>(data));
        }

        ${className} *operator->() {
            return (${className}*) &_data;
        }

        ${className} const *operator->() const {
            return (${className}*) &_data;
        }

        operator ${className}&() {
            return *(${className}*) &_data;
        }

        operator const ${className}&() const {
            return *(${className}*) &_data;
        }
    private:
        char _data[sizeof(${className})];
    };
@else
    @if(!classDecl.isFinal)
        !{
            Integer i = 0;
            HashMap<VTableEntry, String> dispatchNames = new HashMap<>();
            for (var m : classDecl.vtable) {
                dispatchNames.put(m, m.name.toLower() + "_dispatch_" + i++);
            }
        }
        // Avoid external linkage but keep C linkage. These functions are only
        // refered localy and using a function pointer.
        extern "C" {
        <%-- Dispatching functions to call overriden methods --%>
        @for(var method : classDecl.vtable)
            ${api.cTypename(method.functionType.returnType)}
            ${className}_${dispatchNames.get(method)} (
                ${api.dispatchParameters(method.functionType)}
            ) {
                ${className} *__self = (${className} *) _self;
                @for(var param : method.functionType.parameters.stream().skip(1).toList())
                ${api.convertForDispatch(param)};
                @endfor
                try {
                return
                @if(!api.needsRelease(method.functionType))
                ${api.cTypename(method.functionType.returnType)}
                @endif(
                    ${api.callDispatch(method)}
                )${api.makeRelease(method.functionType)};
                } catch (polyglot::ada::exceptions::AdaException &e) {
                    polyglot_get_kernel()->exc_info.exception_data = e.release();
                    ${api.makeDispatchDefaultReturn(method.functionType)}
                }
            }
        @endfor

        void *${className}_do_internal_clone(${className} *self, void *data) {
            return self->internal_clone(data);
        }

        void ${className}_do_internal_free(${className} *self) {
            // If this is called, thats means the destruction of the shadow data was
            // called from the library. The data is already being freed on that side if
            // necessary (it could have been copied on the stack, meaning that calling
            // the free function could lead to an invalid free)
            self->release();
            delete self;
        }
        }

        struct ${className}_vtable {
        <%-- Constructor of the vtable --%>
        ${className}_vtable() {
            this->_data.clone_shadow_dispatch = &${className}_do_internal_clone;
            this->_data.free_shadow_dispatch = &${className}_do_internal_free;
        @for(var method : classDecl.vtable)
            !{String lastName = method.name.toLower();}
             this->_data.${dispatchNames.get(method)} =
                 &${className}_${dispatchNames.get(method)};
        @endfor
        }

            void *data() { return &this->_data; }

            struct table_data {
                void *(*clone_shadow_dispatch)(${className} *_self, void *_data);
                void (*free_shadow_dispatch)(${className} *_self);
            <%---
              -- Generate all the dispatch function pointers. They are
              -- functions with C linkage and they must overlay on the vtable
              -- generated by the scanner.
              --%>
            @for(var method : classDecl.vtable)
                ${api.cTypename(method.functionType.returnType)}
                (*${dispatchNames.get(method)})
                (${api.dispatchParameters(method.functionType)});
            @endfor
            } _data;
        } ${className}_vtable;
    @endif

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

    ${className} *${className}::internal_clone(void *) {
        assert(
            "Attempting to clone a derivation of ${className}:"
            " ${className}::internal_clone should be overriden"
            && false
        );
    }

    @if(members.copyFunction != null)
       extern "C" void ${members.copyFunction.symbol}
           (${api.cParameters(members.copyFunction)});
        ${className}
        &${className}::operator=(${api.cppParameters(members.copyFunction)}) {
            ${api.callCSymbol(members.copyFunction)};
            return *this;
        }
    @endif

    void *${className}::data() const {
        return this->_data;
    }

    void *${className}::release() {
        void *tmp = this->_data;
        this->_data = nullptr;
        return tmp;
    }

    bool ${className}::is_shadow() {
        return typeid(${className}).before(typeid(*this));
    }

@endif
