
LIB_NAME=ex3

INPUT_PROXY=$(CURDIR)/../2proxy/
INPUT_PROXY_LIB=$(INPUT_PROXY)/lib_agg/relocatable/dev/

JAVA_PROXY=$(CURDIR)/../2java/

ifeq ($(OS), Windows_NT)
JNI_LIBS:=$(JAVA_PROXY)/lib;$(JAVA_PROXY)/runtimes/ada/lib/relocatable;$(JAVA_PROXY)/runtimes/proxy2java/lib/relocatable
export PATH:=$(JNI_LIBS);$(INPUT_PROXY_LIB);$(PATH)
else
JNI_LIBS:=$(JAVA_PROXY)/lib:$(JAVA_PROXY)/runtimes/ada/lib/relocatable:$(JAVA_PROXY)/runtimes/proxy2java/lib/relocatable
export LD_LIBRARY_PATH:=$(JNI_LIBS):$(INPUT_PROXY_LIB):$(LD_LIBRARY_PATH)
endif

java:
	gprbuild2 $(INPUT_PROXY)/${LIB_NAME}-proxy-agg.gpr \
		-XLIBRARY_KIND=relocatable -XLIBRARY_STANDALONE=standard
	gprbuild2 -f $(JAVA_PROXY)/${LIB_NAME}_jni.gpr \
		-XLIBRARY_KIND=relocatable -XLIBRARY_STANDALONE=standard \
		-XPROXY_LIB=${LIB_NAME}_proxy_agg -XPROXY_LIB_LOCATION=$(INPUT_PROXY_LIB)
	mvn install -f $(JAVA_PROXY)/runtimes/proxy2java/pom.xml
	mvn install -f $(JAVA_PROXY)/runtimes/ada/src2java/pom.xml
	mvn package -f $(JAVA_PROXY)/pom.xml
	java --enable-native-access=ALL-UNNAMED \
		--class-path $(JAVA_PROXY)/target/lib/${LIB_NAME}.jar \
		Main.java

.PHONY: java
