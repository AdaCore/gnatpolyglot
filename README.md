# Polyglot

Multi-language binding interface generator.

## Build and usage

Polyglot depends on [Libadalang](https://github.com/AdaCore/libadalang). You must first follow its instructions and install the java bindings.

In order to build Polyglot, you must then run:

```sh
$[polyglot]> mvn package -f polyglot/pom.xml
```

You can then run Polyglot using the uber jar:

```
$[polyglot]> java \
        -cp polyglot/cli/target/cli.jar \
        --add-exports org.graalvm.truffle/com.oracle.truffle.api.strings=ALL-UNNAMED \
        com.adacore.polylot.cli.PolyglotMain
```

To enable the native-image build, use the `native` profile:
```sh
$[polyglot]> mvn package -f polyglot/pom.xml -Pnative
```

You can then run the native application using:

```
$[polyglot]> ./polyglot/bin/polyglot
```

