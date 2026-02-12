*********
Ada2Proxy
*********

Creates functions that use the C ABI as a common way to communicate
outside of the Ada world.

Types
-----

Scalars
~~~~~~~

All scalars are bound as regular fixed-sized integers (e.g. u16, i32,...)
of their matching size.

====================================== =========================
Ada Type                               Proxy Type
====================================== =========================
Integer type <= 8, 16, 32, 64, 128 bit int 8, 16, 32, ...
Mod type <= 8, 16, 32, 64, 128 bit     unsigned int 8, 16, 32, ...
Boolean                                bool
Fixed point                            double
Floating point <= 32, 64, 128 bit      Float 32, 64, 128
====================================== =========================

When converting a scalar from C to an Ada type, contraints are checked
and may raise a ``Constraint_Error`` if they are not respected. This
exception will be propagated back to the caller.

Enumeration types
~~~~~~~~~~~~~~~~~

Enumerations are bound one-to-one in the proxy. The proxy holds the
integer value of each enumeration literal.

Subtyping or derivating from an enumeration creates a new enumeration,
applying any constraint that could be determined for the list of
literals of the new enumeration. Representation clauses for enumerations
are also supported.

.. code:: ada

   package Example is
      type Enum is (A, B, C, D);

      type Derivation is new Enum (B .. C);
   end Example;

.. code:: json

   [
     {
       "kind": "enum",
       "name": { "names": ["example", "enum"] },
       "items": [
         { "name": "a", "value": 0, "doc": "" },
         { "name": "b", "value": 1, "doc": "" },
         { "name": "c", "value": 2, "doc": "" },
         { "name": "d", "value": 3, "doc": "" }
       ]
     },
     {
       "kind": "enum",
       "name": { "names": ["example", "derivation"] },
       "items": [
         { "name": "b", "value": 1, "doc": "" },
         { "name": "c", "value": 2, "doc": "" }
       ]
     }
   ]

Record
~~~~~~

Simple record types are bound as final classes in the proxy. Each
component of the record definition will have a corresponding generated
getter, and setter when possible.

Generated getters return a reference when supported, so the setter may
not always be necessary.

As a limitation, Polyglot does not yet support discriminant components.

Private types
^^^^^^^^^^^^^

Any private type is bound as a class. Any private component will not
have a corresponding getter or setter generated.

Tagged types
~~~~~~~~~~~~

A tagged type is bound as a non-final type and can be inherited in
generated bindings through the use of a shadow type.

Shadow types
^^^^^^^^^^^^

Shadow types are tagged types generated for each inheritable type. It
inherits a tagged type and overrides each of its primitives to
redispatch to the proper override.

It is not directly exposed to the user. Instead, a protected constructor
is added. The shadow object constructor takes two additional implicit
arguments: the address of the object from the target language, and a
vtable.

This type is able to perform classwide copies, which would also create a
new instance of the inheriting child type.

.. code:: ada

   type Foo is tagged null record;

   -- Generated type
   type Foo_Shadow is new Foo with record
      SD: Polyglot.Ada.Shadow_Data;
   end record;

   procedure P(Obj : Foo'Class) is
      Clone : Foo'Class := Obj;
   begin
      null;
   end P;

Abstract types
^^^^^^^^^^^^^^

No base constructor is generated for abstract types. However, a shadow
type is still generated. Only inheriting types should be able to call it
to construct the underlying shadow object which will be able to reach
the user’s overriding functions

Interfaces
^^^^^^^^^^

Interfaces are not yet supported.

Limited types
~~~~~~~~~~~~~

Limited types are bound as regular types with the exception that no
additional copy and clone functions are generated.

Arrays
~~~~~~

Unconstrained arrays
^^^^^^^^^^^^^^^^^^^^

All unconstrained arrays are bound as generic arrays. Bounds
information is kept (First & Last), so care for constraint errors.

.. code:: ada

   type Arr1 is array (Positive range <>) of Integer;
   type Arr2 is array (Positive range <>) of Integer;
   -- Both types will be usable under a single "array of integer" type.

Constrained arrays, or pragma’d
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

TODO: Not yet supported, will be bound as regular records since their
internal representation may differ from unconstrained arrays, with
regular getter and setter for nth component.

Strings
~~~~~~~

All unconstrained arrays of character are bound as regular strings.
Since there are incompatibilities with Ada strings and strings from
other languages, an explicit copy is necessary to convert an Ada string.

Similarly to arrays, all bounds informations are kept.

Access types
~~~~~~~~~~~~

Only one level of indirection is implemented; Polyglot does not support
access to access types and getters that may return an access component
do not return references. However, passing access values by reference
(``out`` parameter) are supported.

Subprograms
-----------

All subprograms are bound as regular functions in the proxy. Non
dispatchable subprograms are marked as ``final``.

Parameter types
~~~~~~~~~~~~~~~

By default, all non scalar and pointer types parameters are bound as
constant references. This is to avoid unecessary copies on the caller
side. ``out`` or ``in out`` parameters are all bound as non-constant
references.

Dot-callable Primitives
~~~~~~~~~~~~~~~~~~~~~~~

When a primitive of a type has at least one parameter, and the first
parameter is of the primitive’s types, it is given a role ``method`` to
this type in the proxy. This allows the subprogram to become a member
function/method of the type in the generated proxy, even to non-tagged
types (similarly to the dot notation while using the ``-gnatX``
extension).

Primitives that do not match these criteria will remain as free
functions.

Operators
~~~~~~~~~

Operators are bound as regular functions, but are given a placeholder
name that may be matched to later be translated to an operator
overloading function when the target language supports it. If the target
language does not support operator overloading, then the placeholder
name will be kept in the resulting interface.

========= ====================
Operator  Proxy name
========= ====================
``+``     ``operator_plus``
``-``     ``operator_minus``
``*``     ``operator_mult``
``/``     ``operator_div``
``**``    ``operator_pow``
``mod``   ``operator_mod``
``rem``   ``operator_rem``
``abs``   ``operator_abs``
``&``     ``operator_concat``
``=``     ``operator_eq``
``/=``    ``operator_ne``
``<``     ``operator_lt``
``<=``    ``operator_le``
``>``     ``operator_gt``
``>=``    ``operator_ge``
``and``   ``operator_bit_and``
``or``    ``operator_bit_or``
``xor``   ``operator_bit_xor``
``not``   ``operator_bit_not``
========= ====================

Global variables and constants
------------------------------

A getter is generated for each global variable. A setter may also be
generated if the entity is not marked as ``constant``.

Exceptions
----------

Exceptions are bound in the proxy. When an exception is raised in a
called function, it is caught before returning to the caller, and
identified. Only exceptions in specification files can be identified,
otherwise they are considered anonymous exceptions.

The predefined exceptions of the Ada language (``Constraint_Error``,
``Program_Error``, ``Storage_Error`` and ``Tasking_Error``) are also
bound.
