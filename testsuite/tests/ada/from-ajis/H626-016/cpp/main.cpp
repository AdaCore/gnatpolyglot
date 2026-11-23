#include <iostream>

#include "test.h"
#include "gnatpolyglot_ada_strings.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    gnatpolyglot::polyglot_ptr<ps::polyglot_string> r = nullptr;

    test::create_string(r);

    gnatpolyglot::polyglot_ptr<ps::polyglot_string> s1 = r;

    std::cout << ps::to_string(*s1) << std::endl;

    test::create_and_append_string(r);

    gnatpolyglot::polyglot_ptr<ps::polyglot_string> s2 = r;

    std::cout << ps::to_string(*s1) << std::endl;
    std::cout << ps::to_string(*s2) << std::endl;
    return 0;
}
