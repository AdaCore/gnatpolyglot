#include "gnatcoll_boyer_moore.h"
#include "gnatpolyglot_ada_strings.h"
#include <iostream>

using namespace gnatpolyglot::ada::strings;
using namespace gnatcoll::boyer_moore;

int main() {
    polyglot_string str = from_string("ABDEABCFGABC");
    Pattern key;
    key.compile(from_string("ABC"), true);
    std::cout << key.search(str) << "\n";
    key.free();
}
