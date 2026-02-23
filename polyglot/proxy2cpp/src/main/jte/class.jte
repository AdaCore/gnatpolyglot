<%--
vim: filetype=jtecpp
--%>
@import com.adacore.polyglot.proxy.Role.RoleKind
@import com.adacore.polyglot.proxy.ClassDecl
@import com.adacore.polyglot.proxy.ClassDecl.Inheritability
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
    private:
    @if(!api.isCopyable(classDecl))
        ${className} &operator=(const ${className}&) = delete;
    @endif

    @if(!api.isCopyConstructible(classDecl))
        ${className}(const ${className}&) = delete;
    @endif

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

        /**
         * For internal use.
         * Returns the address of the bound object.
         */
        void *data_() const;

        /**
         * For internal use.
         * Returns the address of the bound object and releases the ownership
         * of the bound object.
         */
        void *release_();

        /**
         * For internal use.
         * Performs a polymorphic clone of the object. Must be overriden in
         * order for the bound library to perform polymorphic copies of the
         * object. The data parameter is the address of the bound object.
         *
         * Any inheriting type should have a `T(const &T, void*)` copy
         * constructor that should be called using the `::new` operator. This
         * function must return the resulting created object.
         */
        virtual ${className} *internal_clone_(void *data);

        template <typename U>
        friend class polyglot::polyglot_ptr;
    private:

        /**
         * For internal use.
         * Return whether this object is of a type that inherits ${className},
         * or if it is of the ${className} type.
         */
        bool is_shadow();

    public:

    @if(classDecl.inheritability != Inheritability.VIRTUAL)
        struct view;
    @endif

    @if(classDecl.parent == null)
    protected:
        void *_data;
    @endif
    };

    @if(classDecl.inheritability != Inheritability.VIRTUAL)
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

        ${className}& operator*() {
            return *(${className}*) &_data;
        }

        const ${className}& operator*() const {
            return *(${className}*) &_data;
        }
    private:
        char _data[sizeof(${className})];
    };
    @endif
@else
    @if(classDecl.inheritability != Inheritability.FINAL)
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
                @if(api.returnsVoid(method.functionType))
                    ${api.callDispatch(method)};
                @else
                    ${api.cppReturnTypename(method.functionType.returnType)} returned_value =
                      ${api.callDispatch(method)};
                    ${api.makeReturnFromDispatch(method.functionType, "returned_value")}
                @endif
                } catch (polyglot::ada::exceptions::AdaException &e) {
                    polyglot_get_kernel()->exc_info.exception_data = e.release_();
                    ${api.makeDispatchDefaultReturn(method.functionType)}
                }
                @for(var param : method.functionType.parameters.stream().skip(1).toList())
                ${api.checkDispatchPointerValue(param)};
                @endfor
            }
        @endfor

        void *${className}_do_internal_clone(${className} *self, void *data) {
            return self->internal_clone_(data);
        }

        void ${className}_do_internal_free(${className} *self) {
            // If this is called, thats means the destruction of the shadow data was
            // called from the library. The data is already being freed on that side if
            // necessary (it could have been copied on the stack, meaning that calling
            // the free function could lead to an invalid free)
            self->release_();
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

            void *data_() { return &this->_data; }

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

    ${className} *${className}::internal_clone_(void *) {
        assert(
            "Attempting to clone a derivation of ${className}:"
            " ${className}::internal_clone_ should be overriden"
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

    void *${className}::data_() const {
        return this->_data;
    }

    void *${className}::release_() {
        void *tmp = this->_data;
        this->_data = nullptr;
        return tmp;
    }

    bool ${className}::is_shadow() {
        return typeid(${className}).before(typeid(*this));
    }

@endif
