#include <iostream>

#include "test.h"

using gnatpolyglot::polyglot_ptr;
using gnatpolyglot::ada::arrays::polyglot_array;

int main() {
    polyglot_ptr<polyglot_array<test::R>> v(
        new polyglot_array<test::R>(1, 2), gnatpolyglot::memory_owner::LIBRARY);

    test::set_v5(v);
    test::get_v5()->get(1)->set_f(999);
    std::cout << test::get_v5()->get(1)->get_f() << std::endl;

    test::get_v6()->get(2)->set_f(888);
    std::cout << test::get_v6()->get(2)->get_f() << std::endl;
    return 0;
}
