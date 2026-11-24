#include "hello.h"
#include "gnatpolyglot_ada_strings.h"

int main() {
    hello::say(gnatpolyglot::ada::strings::from_string("Hello World!"));
}
