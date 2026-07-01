all: ada2proxy proxy2cpp cpp

cpp:
	$(MAKE) -C cpp/

cpp_gpp:
	$(MAKE) -C cpp/ gpp

java:
	$(MAKE) -C java/

rust:
	$(MAKE) -C rust/

ada2proxy:
	gnatpolyglot ada2proxy -P ex0.gpr -o 2proxy

proxy2cpp:
	gnatpolyglot proxy2cpp 2proxy/proxy.json -o 2cpp

proxy2java:
	gnatpolyglot proxy2java 2proxy/proxy.json -o 2java

proxy2rust:
	gnatpolyglot proxy2rust 2proxy/proxy.json -o 2rust

clean:
	$(RM) -r 2proxy 2cpp 2java 2rust
	$(RM) -r obj lib
	$(RM) -r cpp/bin cpp/obj rust/target

.PHONY: ada2proxy proxy2cpp proxy2rust all gpp clean java cpp rust
