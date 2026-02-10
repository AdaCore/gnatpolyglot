Polyglot
========

Polyglot is a high-level bindings generator.

The generation of bindings is done in a two step process:

1.  Generation of the Proxy IR, and glue code to expose an interface
    for binded constructs that relies on the C ABI.

2.  Generation of a high level
    interface in a given target language

This lets us avoid having dependencies on the compiler from the input
language.

Generating Proxy IR
===================

Generating Ada Proxy layer
--------------------------

.. code:: sh

   $> polyglot ada2proxy -P<project> -o<output_path>

The ``ada2proxy`` command takes a project file as an input to fetch the
list of sources to bind.

Generates Ada code that interfaces with the C ABI, and a ``proxy.json``
file

2 GPR projects files are generated:

*  proxy : Contains all the code that uses the C ABI

* proxy aggregate: Aggregate of the binded library,
  polyglot runtime and bindings. Can be compiled as an ESAL to avoid
  future dependencies with Ada runtime, etc. Danger: read limitations of
  ESAL (gpr doc).

.. code:: sh

   $> polyglot -Ptest.gpr -o./2proxy
   $> find ./2proxy
   2proxy/
   2proxy/proxy.json
   2proxy/test-proxy-agg.gpr
   2proxy/test-proxy.gpr
   2proxy/src
   2proxy/src/*.ad[sb]

Generating Language specific interfaces
=======================================

Generating C++ interfaces
-------------------------

.. code:: sh

   $> polyglot proxy2cpp <proxy.json file> -o<output_path>
   $> find <output_path>

Generates all .cpp files that call the functions described in the json
proxy IR. Include files are located in the ``<output_path>/include``
directory.

.. code:: sh

   $> polyglot proxy2cpp 2proxy/proxy.json -o./2cpp
   $> find /2cpp
   2cpp/
   2cpp/*.cpp
   2cpp/include
   2cpp/include/*.h

Use build system of choice to build these.

Example: Generating Ada to C++ bindings
=======================================

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
   $> gprbuild 2proxy/test-proxy-agg.gpr -f -ggdb
   $> g++ main.cpp \
          2cpp/*.cpp \
          -o main \
          -I2cpp/include \
          -Wall -Wextra \
          -L2proxy/lib_agg/static/dev/ -lfoo_proxy_agg \
          -ldl -lpthread # May be necessary on Linux systems
   $> ./main
   ...
