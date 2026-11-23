#include "test.h"
#include <gnatpolyglot_ada_strings.h>
#include <iostream>

namespace ps = gnatpolyglot::ada::strings;

int main() {
    ps::polyglot_string s1 = ps::from_string("toto");
    // polyglot_string has no (first, last) constructor: allocate a
    // 10-character string instead.
    ps::polyglot_string s2 = ps::from_string("          ");

    test::print(s1);
    std::cout << ps::to_string(s1) << "\n";

    test::modify(s1);
    test::modify(s2);

    test::print(s1);
    std::cout << ps::to_string(s1) << "\n";

    test::print(s2);
    std::cout << ps::to_string(s2) << "\n";

    return 0;
}
