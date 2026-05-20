********
GNATpolyglot
********

GNATpolyglot is a multi-language, high-level bindings generator.

The generation of bindings for a given library is done in a two step process:

1.  Generation of the *Proxy* intermediate representation which
    describes the content of the library in a language-agnostic way,
    together with glue code wrapping the original library and adhering
    to a standard interface over the C ABI.

2.  Generation of a high level interface in a given target language,
    relying exclusively on the *Proxy* intermediate representation and
    glue code.

The key takeaway is that the second step can be done independently of
the first one. In fact, it doesn't even need to be aware of the input
language of the original library, and does not require a compiler for
the input language.

Moreoever, unlike writing a dedicated tool for each input-output pair of
languages, this decoupling through an intermediate representation
implies that when a language frontend is added to GNATpolyglot, it
immediately allows generating bindings from that language to all
supported output languages. Similarly, once a new language backend is
added, it immediately allows creating bindings for that language from
all the input languages supported by GNATpolyglot.

Generating Proxy IR
-------------------

Generating Ada Proxy layer
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: sh

   $> gnatpolyglot ada2proxy -P<project> -o<output_path>

The ``ada2proxy`` subcommand takes a GPR project file as an input to fetch the
list of sources to bind, and generates Ada code that interfaces with the C ABI,
as well as a ``proxy.json`` file that describes the content of the library.

See the dedicated :ref:`ada2proxy` chapter for more information on the subcommand's usage.

In order to bind these library, 2 GPR projects files are generated:

* Proxy: Contains all the code that uses the C ABI

* Proxy aggregate: Aggregate of the bound library, gnatpolyglot runtime and
  bindings. This project can be compiled as an Encapsulated Standalone
  Aggregate Library (ESAL) to avoid future dependencies with the Ada
  runtime, and other transitive dependencies from the bound project.

  .. danger::

     When building using an ESAL, the resulting library should be the
     only ada project. Learn more `here
     <https://docs.adacore.com/live/wave/gprbuild/html/gprbuild_ug/gprbuild_ug/gnat_project_manager.html#encapsulated-stand-alone-library-projects>`__.

.. code:: sh

   $> gnatpolyglot ada2proxy -Ptest.gpr -o./2proxy
   $> find ./2proxy
   2proxy/
   2proxy/proxy.json
   2proxy/test-proxy-agg.gpr
   2proxy/test-proxy.gpr
   2proxy/src
   2proxy/src/*.ad[sb]

.. warning::

   It is necessary to use ``gprbuild2`` to build the generated projects.
   Refer to the `GPR documentation
   <https://docs.adacore.com/live/wave/gprbuild/html/gprbuild_ug/gprbuild_ug/building_with_gprbuild.html#how-to-use-our-new-builder>`__
   to use the new builder.

Generating Language specific interfaces
---------------------------------------

Generating C++ interfaces
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code:: sh

   $> gnatpolyglot proxy2cpp <proxy.json file> -o<output_path>
   $> find <output_path>

The ``proxy2cpp`` subcommand generates all source files that call the functions
described in the json proxy IR, and all the headers that contain functions and type
declarations from the bound library. Header files are located in the
``<output_path>/include`` directory.

See the dedicated :ref:`proxy2cpp` chapter for more information on the subcommand's usage.

.. code:: sh

   $> gnatpolyglot proxy2cpp 2proxy/proxy.json -o./2cpp
   $> find /2cpp
   2cpp/
   2cpp/*.cpp
   2cpp/include
   2cpp/include/*.h

Use your preferred build system to build these.

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
   $> gnatpolyglot ada2proxy -P input/test.gpr -o 2proxy
   $> gnatpolyglot proxy2cpp 2proxy/proxy.json -o 2cpp

.. code:: cpp

   // 2cpp/include/test.h

   namespace test {

   void hello();

   int do_double(int i);

   } // namespace test

.. code:: sh

   $> gprbuild 2proxy/test-proxy-agg.gpr -f -ggdb --gpr=2
   $> gprbuild 2cpp/runtimes/ada/gnatpolyglot_ada2cpp.gpr -f -ggdb --gpr=2
   $> g++ main.cpp \
          2cpp/*.cpp \
          -o main \
          -I2cpp/include \
          -Wall -Wextra \
          -L2cpp/runtimes/ada/lib2cpp/static/dev/ -lgnatpolyglotada2cpp \
          -L2proxy/lib_agg/static/dev/ -lfoo_proxy_agg \
          -ldl -lpthread # May be necessary on Linux systems
   $> ./main
   Hello!
   20
