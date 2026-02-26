all: ada2proxy proxy2cpp main

main:
	gprbuild2 main.gpr

ada2proxy:
	polyglot.py ada2proxy -P ex1.gpr -o 2proxy

proxy2cpp:
	polyglot.py proxy2cpp 2proxy/proxy.json -o 2cpp

clean:
	$(RM) -r main 2proxy 2cpp obj lib bin

# Example: compiling without a gnat environment
gpp:
	# Build the bindings
	gprbuild2 2proxy/ex1-proxy-agg.gpr
	# Build the Ada2Cpp polyglot runtime: This library is standalone and only
	# requires a C++ compiler
	gprbuild2 2cpp/runtimes/ada/polyglot_ada2cpp.gpr
	# Build the executable
	g++ main.cpp -o bin/gppmain \
		2cpp/animals.cpp 2cpp/polyglot_ada_arrays.cpp 2cpp/polyglot_exceptions_ex1.cpp \
		-I 2cpp/include/ \
		-I 2cpp/runtimes/ada/src2cpp/ \
		-Wall -Wextra -Werror -pedantic \
		-L2proxy/lib_agg/static/dev/ \
		-lex1_proxy_agg \
		-L2cpp/runtimes/ada/lib2cpp/static/dev/ \
		-lpolyglotada2cpp \
		-ldl -lpthread



.PHONY = ada2proxy proxy2cpp all gpp clean
