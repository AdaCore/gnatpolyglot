#include <iostream>
#include <string>

#include "test.h"
#include "gnatpolyglot_ada_strings.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    test::Rec r(0);
    r.set_v(52);
    r.print_f_acc([](const test::Rec &R) {
        return ps::from_string(std::to_string(R.get_v()));
    });

    test::RecTagged t;

    std::cout << ps::to_string(t.f(r)) << std::endl;
    return 0;
}
