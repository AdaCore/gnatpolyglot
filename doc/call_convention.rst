***
ABI
***

Every function must use the C ABI as a common interface.

Argument passing
----------------

Scalars
~~~~~~~

Scalars are bound 1-to-1

+-------------------+--------------------------+----------------+------------+
| Type              | Convention               | C++            | Internal C |
|                   |                          | Type           | type       |
+===================+==========================+================+============+
| Scalar            | Copy                     | ``int``        | ``int``    |
+-------------------+--------------------------+----------------+------------+
| Ref { Scalar }    | Address to the scalar    | ``int &``      | ``int *``  |
|                   |                          |                |            |
+-------------------+--------------------------+----------------+------------+
| Ptr { Scalar }    | Copy of the address      | ``ptr <int>``  | ``int *``  |
|                   |                          |                |            |
+-------------------+--------------------------+----------------+------------+
| Ref { Ptr {       | Address to copy of the   | ``ptr<int> &`` | ``int **`` |
| Scalar } }        | pointer                  |                |            |
+-------------------+--------------------------+----------------+------------+

Classes
~~~~~~~

Classes are bound as class wrappers around a dynamically allocated
internal object from the source

+-----------------------+------------------------------------+-------------------+-------------+
| Type                  | Convention                         | C++               | Internal    |
|                       |                                    |                   | C type      |
+=======================+====================================+===================+=============+
| Class                 | Address of the *internal* object   | ``const class &`` | ``void *``  |
+-----------------------+------------------------------------+-------------------+-------------+
| Ref { Class }         | Address of the *internal* object   | ``class &``       | ``void *``  |
+-----------------------+------------------------------------+-------------------+-------------+
| Ptr { Class }         | Address of the *internal* object   | ``ptr<class>``    | ``void *``  |
+-----------------------+------------------------------------+-------------------+-------------+
| Ref { Ptr { Class } } | Address to copy of the pointer to  | ``ptr<class> &``  | ``void **`` |
|                       | the *internal* object              |                   |             |
+-----------------------+------------------------------------+-------------------+-------------+

Strings and arrays
~~~~~~~~~~~~~~~~~~

Strings and arrays have an inlined structure holding information about
the array: address, bounds,...

+----------------+--------------------------+----------------------+------------------+
| Type           | Convention               | C++ Type             | Internal C type  |
+================+==========================+======================+==================+
| Array          | Copy of the array data   | ``const array<T> &`` | ``array_data``   |
+----------------+--------------------------+----------------------+------------------+
| Ptr { Array }  | Copy of the array data   | ``ptr<array<T>>``    | ``array_data``   |
+----------------+--------------------------+----------------------+------------------+
| Ref { Array }  | Copy of the array data   | ``array<T> &``       | ``array_data``   |
+----------------+--------------------------+----------------------+------------------+
| Ref { Ptr {    | Address to copy of the   | ``ptr<class> &``     | ``array_data *`` |
| Array } }      | array data               |                      |                  |
+----------------+--------------------------+----------------------+------------------+

Pointers
~~~~~~~~

Pointers to pointers are not supported.

References
~~~~~~~~~~

References cannot be contained inside other type (``Ptr { Ref {} }``) as
they are implementation defined.

Returning values
----------------

.. _scalars-1:

Scalars
-------

+----------------+------------------------+--------------+-----------------+
| Type           | Convention             | C++ Type     | Internal C type |
+================+========================+==============+=================+
| Scalar         | Copy                   | ``int``      | ``int``         |
+----------------+------------------------+--------------+-----------------+
| Ref { Scalar } | Address to the scalar  | ``int &``    | ``int *``       |
+----------------+------------------------+--------------+-----------------+
| Ptr { Scalar } | Copy of the address    | ``ptr<int>`` | ``int *``       |
+----------------+------------------------+--------------+-----------------+

.. _classes-1:

Classes
~~~~~~~

Classes (and arrays) use a ``view`` type to return them as a reference
in order to avoid returning an object whose lifetime depends on the
stack. View types can be copied and hold no ownership on the pointer to
the internal data. They do not free *anything* when they are destroyed.

+-----------+------------------------------+-----------------+------------+
| Type      | Convention                   | C++ Type        | Internal C |
|           |                              |                 | type       |
+===========+==============================+=================+============+
| Class     | Address of the *internal*    | ``class``       | ``void *`` |
|           | object                       |                 |            |
+-----------+------------------------------+-----------------+------------+
| Ptr {     | Address of the *internal*    | ``ptr<class>``  | ``void *`` |
| Class }   | object                       |                 |            |
+-----------+------------------------------+-----------------+------------+
| Ref {     | Address of the *internal*    | ``view<class>`` | ``void *`` |
| Class }   | object                       |                 |            |
+-----------+------------------------------+-----------------+------------+

.. _strings-and-arrays-1:

Strings and arrays
~~~~~~~~~~~~~~~~~~

+------------+----------------------+----------------------+----------------+
| Type       | Convention           | C++ Type             | Internal C     |
|            |                      |                      | type           |
+============+======================+======================+================+
| Array      | Copy of the array    | ``const array<T> &`` | ``array_data`` |
|            | data                 |                      |                |
+------------+----------------------+----------------------+----------------+
| Ptr {      | Copy of the array    | ``ptr<array<T>>``    | ``array_data`` |
| Array }    | data                 |                      |                |
+------------+----------------------+----------------------+----------------+
| Ref {      | Copy of the array    | ``view<array<T>>``   | ``array_data`` |
| Array }    | data                 |                      |                |
+------------+----------------------+----------------------+----------------+

Ref to Pointers
~~~~~~~~~~~~~~~

Cannot be returned.
