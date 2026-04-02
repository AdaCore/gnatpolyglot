all: ada2proxy proxy2cpp main

main:
	gprbuild2 main.gpr -ggdb

ada2proxy:
	gnatpolyglot ada2proxy -P ex3.gpr -o 2proxy

proxy2cpp:
	gnatpolyglot proxy2cpp 2proxy/proxy.json -o 2cpp

clean:
	$(RM) -r main 2proxy 2cpp obj lib bin

# Example: compiling without a gnat environment
gpp:
	# Build the bindings
	gprbuild2 2proxy/ex3-proxy-agg.gpr
	# Build the Ada2Cpp GNATpolyglot runtime: This library is standalone and only
	# requires a C++ compiler
	gprbuild2 2cpp/runtimes/ada/gnatpolyglot_ada2cpp.gpr
	# Build the executable
	g++ main.cpp -o bin/gppmain \
		2cpp/exceptions.cpp 2cpp/gnatpolyglot_ada_arrays.cpp 2cpp/gnatpolyglot_exceptions_ex3.cpp \
		-I 2cpp/include/ \
		-I 2cpp/runtimes/ada/src2cpp/ \
		-Wall -Wextra -Werror -pedantic -ggdb \
		-L2proxy/lib_agg/static/dev/ \
		-lex3_proxy_agg \
		-L2cpp/runtimes/ada/lib2cpp/static/dev/ \
		-lgnatpolyglotada2cpp \
		-ldl -lpthread



.PHONY = ada2proxy proxy2cpp all gpp clean
