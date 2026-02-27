********
Polyglot
********

Polyglot is a high-level bindings generator.

The generation of bindings is done in a two step process:

1.  Generation of the Proxy IR, and glue code to expose an interface
    for bound constructs that relies on the C ABI.

2.  Generation of a high level
    interface in a given target language

This lets us avoid having dependencies on the compiler from the input
language.

Generating Proxy IR
-------------------

Generating Ada Proxy layer
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: sh

   $> polyglot ada2proxy -P<project> -o<output_path>

The ``ada2proxy`` command takes a project file as an input to fetch the
list of sources to bind.

The subcommand processes Ada specification files according to input
parameters, and generates Ada code that interfaces with the C ABI, and a ``proxy.json``
file.

In order to bind these library, 2 GPR projects files are generated:

* Proxy: Contains all the code that uses the C ABI

* Proxy aggregate: Aggregate of the bound library, polyglot runtime and
  bindings. This project can be compiled as an Encapsulated Standalone
  Aggregate Library (ESAL) to avoid future dependencies with the Ada
  runtime, and other transitive dependencies from the bound project.

  .. danger::

     When building using an ESAL, the resulting library should be the
     only ada project. Learn more `here
     <https://docs.adacore.com/live/wave/gprbuild/html/gprbuild_ug/gprbuild_ug/gnat_project_manager.html#encapsulated-stand-alone-library-projects>`__.

.. code:: sh

   $> polyglot ada2proxy -Ptest.gpr -o./2proxy
   $> find ./2proxy
   2proxy/
   2proxy/proxy.json
   2proxy/test-proxy-agg.gpr
   2proxy/test-proxy.gpr
   2proxy/src
   2proxy/src/*.ad[sb]

.. warning::

   It is necessary to use ``gprbuild2`` to build the generated projects.
   Refer to the `GPR documenation
   <https://docs.adacore.com/live/wave/gprbuild/html/gprbuild_ug/gprbuild_ug/building_with_gprbuild.html#how-to-use-our-new-builder>`__
   to use the new builder.

Generating Language specific interfaces
---------------------------------------

Generating C++ interfaces
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: sh

   $> polyglot proxy2cpp <proxy.json file> -o<output_path>
   $> find <output_path>

Generates all source files that call the functions described in the json
proxy IR, and all the headers that contain functions and type
declarations from the bound library. Header files are located in the
``<output_path>/include`` directory.

.. code:: sh

   $> polyglot proxy2cpp 2proxy/proxy.json -o./2cpp
   $> find /2cpp
   2cpp/
   2cpp/*.cpp
   2cpp/include
   2cpp/include/*.h

Use build system of choice to build these.

Example: Generating Ada to C++ bindings
---------------------------------------

.. code:: ada

   -- input/src/test.ads
   package Test is

      procedure Hello;

      function Do_Double (I: Integer) return Integer is (I * 2);

   begin

.. code:: cpp

   // cpp/main.cpp
   #include <iostream>

   #include "test.h"

   int main() {
       test::hello();
       std::cout << test::do_double(10) << "\n";
   }


.. code:: sh

   $> find .
   .
   ./input
   ./input/test.gpr
   ./input/src
   ./input/src/test.ads
   ./input/src/test.adb
   ./cpp
   ./cpp/main.cpp
   $> polyglot ada2proxy -P input/test.gpr -o 2proxy
   $> polyglot proxy2cpp 2proxy/proxy.json -o 2cpp

.. code:: cpp

   // 2cpp/include/test.h

   namespace test {

   void hello();

   int do_double(int i);

   } // namespace test

.. code:: sh

   $> gprbuild 2proxy/test-proxy-agg.gpr -f -ggdb --gpr=2
   $> gprbuild 2cpp/runtimes/ada/polyglot_ada2cpp.gpr -f -ggdb --gpr=2
   $> g++ main.cpp \
          2cpp/*.cpp \
          -o main \
          -I2cpp/include \
          -Wall -Wextra \
          -L2cpp/runtimes/ada/lib2cpp/static/dev/ -lpolyglotada2cpp \
          -L2proxy/lib_agg/static/dev/ -lfoo_proxy_agg \
          -ldl -lpthread # May be necessary on Linux systems
   $> ./main
   Hello!
   20
