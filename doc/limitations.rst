***********
Limitations
***********

This document introduces all limitations encountered when trying to bind
specific language constructs or types.

GNATpolyglot ignores any constructs that may not be supported, cascading to
any other declarations that may use or refer to the unsupported feature.

Ada2Proxy
---------

Long Scalar types
~~~~~~~~~~~~~~~~~

Scalar (integers and floating points) types for sizes strictly greater
than 64bit are not supported.

Access types
~~~~~~~~~~~~

Access to scalar types
^^^^^^^^^^^^^^^^^^^^^^

Access to scalar types are not supported.

.. code:: ada

   type Unsupported is access all Integer;

Access to subprograms
^^^^^^^^^^^^^^^^^^^^^

Access to subprogram types are not supported.

.. code:: ada

   type Unsupported is access procedure;

Access to access
^^^^^^^^^^^^^^^^

Only one level of access indirection is supported

.. code:: ada

   type P is private;

   type Supported is access all P;

   type Unsupported is access all Supported;

Access to arrays
^^^^^^^^^^^^^^^^

Thin pointers to arrays are not supported.

.. code:: ada

   type Arr is array (Positive range <>) of Integer;

   type Unsupported is access all Arr
   with Size => Standard'Address_Size;

Anonymous type declarations
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Declarations that use anonymous declarations cannot be bound.

.. code:: ada

   type P is private;

   procedure Unsupported (Acc : access P);

Array Types
~~~~~~~~~~~

Arrays of arrays
^^^^^^^^^^^^^^^^

Arrays of arrays and arrays of access to arrays are not supported.

.. code:: ada

   type Arr is array (Positive range <>) of Integer;

   type Acc is access all Arr;

   type Unsupported_1 is array (Positive range <>) of Arr (1 .. 10);

   type Unsupported_2 is array (Positive range <>) of Acc;

.. _limitations-arrays-aspects-constrained:

Aspects
^^^^^^^

Arrays with the ``Component_Size`` aspect, or the ``Pack`` aspect, are
not supported.

.. code:: ada

   type Unsupported_1 is array (Positive range <>) of Integer
   with Pack;

   type Unsupported_2 is array (Positive range <>) of Integer
   with Component_Size => 64;

Constrained arrays
^^^^^^^^^^^^^^^^^^

Constrained arrays are not yet supported.

.. code:: ada

   type Unsupported is array (1 .. 10) of Integer;

Enumeration index
^^^^^^^^^^^^^^^^^

Arrays indexed by enumeration types are not bindable.

.. code:: ada

   type Enum is (A, B, C);

   type Unsupported is array (Enum range <>) of Integer;

Classwide objects
~~~~~~~~~~~~~~~~~

Any access to a classwide object is not supported. Moreover, functions
that return a classwide object cannot be bound either.

.. code:: ada

   type T is tagged null record;

   type Unsupported_T is access all T'Class;

   function Unsupported return T'Class;

   procedure Support (Obj: T'Class);

Controlled types
~~~~~~~~~~~~~~~~

During the destruction of a C++ object, the vtable is also progressively
destroyed. At the start of any destructor execution, it is no longer
possible to downcast the object to its original type, meaning that
calling any virtual function would not call the deepest override either.

In Ada → C++ bindings, when an Ada class wide copy of a controlled type
occurs, the C++ object is also cloned, and owned by the copy. Upon scope
exit, the C++ ``finalize`` override may be called on the object, then it
will be destroyed by the ``Shadow_Data.Finalize`` function.

However, when a C++ object is destroyed, it will call the destructor of
the child type first, until it reaches the Ada ``Free`` function, that
will call ``finalize``. Since all the previous destructors were called,
it will not be able to call the original type's ``finalize`` function.
In order to avoid this inconsistency, overriding the subprograms of
controlled types was disabled.

.. code:: cpp

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

Discriminated types
~~~~~~~~~~~~~~~~~~~

Discriminated types are not yet supported.

.. code:: ada

   type Unsupported (I: Integer) is null record;

Fixed point types
~~~~~~~~~~~~~~~~~

Fixed point types are not yet supported.

.. code:: ada

   type Unsupported is delta 10.0 ** (-1) digits 3;

Generic declarations
~~~~~~~~~~~~~~~~~~~~

Generic declarations cannot be bound and will be ignored.

.. code:: ada

   package Example is
      generic
         type T is <>;
      package Unsupported is
      end Gen;
   end Example;

Inheritable types
~~~~~~~~~~~~~~~~~

In Ada, tagged type primitives can have multiple controlling parameters,
and can also have a controlling return type (dynamic dispatch based on
return type).

.. code:: ada

   type Root is tagged private;
   function F(R1, R2: Root) return Root;

   type Child is new Root with private;
   overriding function F(R1, R2: Child) return Child;

Since there is no corresponding capability in most other programming
languages, any tagged type with a primitive that makes use of such
language features will be marked as ``final`` in the proxy.

.. code:: cpp

   class Root {
       // No shadow constructor
       Root();

       // Not virtual
       Root f(const Root &r1, const Root &r2);
   };

If one of the tagged type's primitives is not bindable, then the type
will also be marked as final.

Integer types
~~~~~~~~~~~~~

Integer types that require more than 64 bits are not supported.

.. code:: ada

   function Unsupported return Long_Long_Integer;

Interfaces
~~~~~~~~~~

Interfaces are not supported.

.. code:: ada

   type Unsupported is interface;

Limited Types
~~~~~~~~~~~~~

When creating a new object of a limited record type, a copy of all the
parameters is done to initialize the components of the record. In the
case where one of the component also has a limited type, it becomes
impossible to perform the copy: Ada2Proxy is unable to add a
constructor that would accept these parameters. Only a constructor that
takes default values in consideration will be generated.

.. code:: ada

   type Rec1 is limited record
      I : Integer := 1;
   end record;

   type Rec2 is limited record
      R : Rec1;
   end record;

.. code:: cpp

   class Rec1 {
       Rec1();
       Rec1(int);
   };

   class Rec2 {
       Rec2();
       // Rec2(const Rec1&) is not defined.
   };

Nested packages
~~~~~~~~~~~~~~~

Although child packages are supported, nested packages are not yet
supported.

.. code:: ada

   package Example is

      package Unsupported is
      end Unsupported;

   end Example;

   package Example.Supported is
   end Example.Supported;

Wide characters
~~~~~~~~~~~~~~~

The ``Wide_Character`` and ``Wide_Wide_Character`` types are not
supported.

.. code:: ada

   type Unsupported_Str is arrays (Positive range <>) of Wide_Character;

Proxy2Cpp
---------

C++ object construction
~~~~~~~~~~~~~~~~~~~~~~~

The constructor of the parent type is called first. It will initialize its own
vtable *only*.

It is not possible to know at this point if the ctor was called by a
child or not, since the child's vtable is not initialized yet, making it
not possible to use RTTI to select between the ``alloc`` and
``shadow_alloc`` ctor automatically.

.. code:: cpp

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

Instead, the ``shadow_alloc`` constructors are generated with an
additional argument, that is the ``this`` argument passed to the shadow
object to perform dynamic dispatch back to C++.

.. code:: cpp

   class Foo {
   protected:
       // Shadow alloc ctor
       Foo(int a, Foo *self);

   public:
       Foo(int a);
   }

In order for the bound library to make use of the overriding function
of the inheriting type, it becomes necessary to call the constructor with the extra
parameter:

.. code:: cpp

   class Bar : public Foo {
       // Correct: will construct an object of shadow type.
       Bar(int a) : Foo(a, this) { }

       // Incorrect: will construct a simple `Foo` object in the library.
       Bar(int a) : Foo(a) { }
   }

Polymorphic copies
~~~~~~~~~~~~~~~~~~

Some bound languages may be able to perform polymorphic copies (e.g
Ada). When they occur on Shadow types, it is necessary to also make a
clone of the C++ object to which they have a reference. C++ does not
provide a way to perform polymorphic copies out of the box, so when
inheriting a bound type, it is necessary to manually provide a way to
perform such copies through the overridable ``internal_clone`` function
member:

.. code:: ada

   type Root is tagged private;

.. code:: cpp

   class Child : public Root {
   protected:
       Root *internal_clone(void *data) {
           return new Child(*this, data);
       }

       Child(const Child &other, void *data) : Root(data) { }
   }

Proxy2Java
---------

Supported platforms
~~~~~~~~~~~~~~~~~~~

Currently, only 64bit Linux and Windows platforms are supported by Proxy2Java.
