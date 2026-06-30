all: ada2proxy proxy2cpp main

cpp:
	$(MAKE) -C cpp/

cpp_gpp:
	$(MAKE) -C cpp/ gpp

java:
	$(MAKE) -C java/

ada2proxy:
	gnatpolyglot ada2proxy -P ex1.gpr -o 2proxy

proxy2cpp:
	gnatpolyglot proxy2cpp 2proxy/proxy.json -o 2cpp

proxy2java:
	gnatpolyglot proxy2java 2proxy/proxy.json -o 2java

clean:
	$(RM) -r 2proxy 2cpp 2java
	$(RM) -r obj lib
	$(RM) -r cpp/bin cpp/obj

.PHONY: ada2proxy proxy2cpp all gpp clean java cpp
