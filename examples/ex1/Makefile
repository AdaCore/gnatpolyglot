all: ada2proxy proxy2cpp main

main:
	gprbuild2 main.gpr

ada2proxy:
	polyglot.py ada2proxy -P ex1.gpr -o 2proxy

proxy2cpp:
	polyglot.py proxy2cpp 2proxy/proxy.json -o 2cpp

clean:
	$(RM) -r main 2proxy 2cpp obj lib

# Example: compiling without a gnat environment
gpp:
	gprbuild2 2proxy/libex1-proxy-agg.gpr
	g++ main.cpp -o bin/gppmain \
		2cpp/animals.cpp 2cpp/polyglot_ada_arrays.cpp 2cpp/polyglot_exceptions_ex1.cpp \
		-I 2cpp/include/ \
		-Wall -Wextra -Werror -pedantic \
		-L2proxy/lib_agg/static/dev/ \
		-lex1_proxy_agg \
		-ldl -lpthread



.PHONY = ada2proxy proxy2cpp all gpp clean
