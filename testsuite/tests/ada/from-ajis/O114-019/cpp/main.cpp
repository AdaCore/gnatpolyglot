#include "hello.h"
#include "gnatpolyglot_ada_strings.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    hello::say(ps::from_string("OK"));
    return 0;
}
