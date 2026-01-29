# Limitations

This document introduces all limitations encountered when trying to bind
specific language constructs or types.

# Ada2Proxy

## Controlled types

During the destruction of a C++ object, the vtable is also progressively
destroyed. At the start of any destructor execution, it is no longer possible
to downcast the object to its original type, meaning that calling any virtual
function would not call the deepest override either.

In Ada -> C++ bindings, when an Ada class wide copy of a controlled type
occurs, the C++ object is also cloned, and owned by the copy. Upon scope exit,
the C++ `finalize` override may be called on the object, then it will be
destroyed by the `Shadow_Data.Finalize` function.

However, when a C++ object is destroyed, it will call the destructor of the
child type first, until it reaches the Ada `Free` function, that will call
`finalize`. Since all the previous destructors were called, it will not be able
to call the original type's `finalize` function. In order to avoid this
inconsistency, overriding the subprograms of controlled types was disabled.

```cpp
#include <iostream>

void ada_finalize_foo() {}

// Ada type
struct Foo {
    virtual ~Foo();

    virtual void finalize() { /** ... */ }
};

// User type
struct Bar : public Foo {
    void intialize() {
        some_ptr = new int;
    }
    void finalize() override{
        delete some_ptr;
    }

    int *some_ptr;
};

Foo::~Foo() {
    std::cout << dynamic_cast<Bar *>(this)
                 // Will **always** return nullptr:
                 // The destructor was called *after* `~Bar()` which also
                 // cleared its own vtable: at this point the object is no
                 // longer a `Bar`, even if it was initialized as one.
              << "\n";

    ada_finalize_foo();
    // When calling the ada Finalize subprogram, it will *never* call
    // `Bar::finalize` since the finalize function is called in `~Foo`'s
    // frame: as shown by the dynamic_cast above, the object is no longer a
    // `Bar`
    //
    // Bar::some_ptr will unexpectedly leak.
}

int main() {
    Bar b;
}
```

# Inheritable types

In Ada, tagged type primitives can have multiple controlling parameters, and
can also have a controlling return type (dynamic dispatch based on return
type).

```ada
type Root is tagged private;
function F(R1, R2: Root) return Root;

type Child is new Root with private;
overriding function F(R1, R2: Child) return Child;
```

Since there is no corresponding capability in most other programming languages,
any tagged type with a primitive that makes use of such language features will
be marked as `final` in the proxy.

```cpp
class Root {
    // No shadow constructor
    Root();

    // Not virtual
    Root f(const Root &r1, const Root &r2);
};
```

If one of the tagged type's primitives is not bindable, then the type will also
be marked as final.

# Proxy2Cpp

## C++ objet construction

The parent constructor is called first. it will initialize its own vtable
*only*.

It is not possible to know at this point if the ctor was called by a child or
not, since the child's vtable is not initialized yet, making it not possible to
use RTTI to select between the `alloc` and `shadow_alloc` ctor automatically.

```cpp
class B;
class A {
public:
    A() {
        // Will always be true
        if (dynamic_cast<B*>(this) == nullptr)
            std::cout << "Not a `B`" << "\n";
    }
};

class B : public A { };

int main() {
    B b;
}
```

Instead, the `shadow_alloc` constructors are generated with an additional
argument, that is the `this` argument passed to the shadow object to perform
dynamic dispatch back to C++.

```cpp
class Foo {
protected:
    // Shadow alloc ctor
    Foo(int a, Foo *self);

public:
    Foo(int a);
}
```

In order for the binded library to make use of the overridden functions of, it
becomes necessary to call the constructor with the extra parameter:

```cpp
class Bar : public Foo {
    // Correct: will constructor a shadow type.
    Bar(int a) : Foo(a, this) { }

    // Incorrect: will constructor a simple `Foo` object in the library.
    Bar(int a) : Foo(a) { }
}
```

## Polymorphic copies

Some binded languages may be able to perform Polymorphic copies (e.g Ada). When
they occur on Shadow types, it is necessary to also make a clone of the C++
object to which they have a reference. C++ does not provide a way to perform
polymorphic copies out of the box, so when inheriting a binded types, it is
necessary to manually provide a way to perform such copies through the
overridable `internal_clone` function member:

```
class Child : public Root {
protected:
    Root *internal_clone(void *data) {
        return new Child(*this, data);
    }

    Child(const Child &other, void *data) : Root(data) { }
}
```
