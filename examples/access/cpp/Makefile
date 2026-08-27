
LIB_NAME=ex4

INPUT_PROXY=$(CURDIR)/../2proxy/

CPP_PROXY=$(CURDIR)/../2cpp/

main:
	gprbuild2 main.gpr
	bin/main

# Example: compiling without a gnat environment
gpp:
	# Build the bindings
	gprbuild2 $(INPUT_PROXY)/$(LIB_NAME)-proxy-agg.gpr
	# Build the Ada2Cpp GNATpolyglot runtime: This library is standalone and only
	# requires a C++ compiler
	gprbuild2 $(CPP_PROXY)/runtimes/ada/gnatpolyglot_ada2cpp.gpr
	# Build the executable
	g++ main.cpp -o bin/gppmain \
		$(wildcard $(CPP_PROXY)/*.cpp) \
		-I $(CPP_PROXY)/include/ \
		-I $(CPP_PROXY)/runtimes/ada/src2cpp/ \
		-Wall -Wextra -Werror -pedantic \
		-L$(INPUT_PROXY)/lib_agg/static/dev/ \
		-l$(LIB_NAME)_proxy_agg \
		-L$(CPP_PROXY)/runtimes/ada/lib2cpp/static/dev/ \
		-lgnatpolyglotada2cpp \
		-ldl -lpthread
	bin/gppmain
