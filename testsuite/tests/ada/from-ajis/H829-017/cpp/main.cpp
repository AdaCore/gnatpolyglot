#include <iostream>

#include "test.h"

int main() {
    // F1 has no default value, so the generated constructor requires it
    // (AJIS: new B ()); F2 keeps its default value.
    test::B v(test::A(0));

    v.get_f2()->set_v(991);

    test::get_g2()->set_v(992);

    std::cout << v.get_f2()->get_v() << "\n";
    std::cout << test::get_g2()->get_v() << "\n";
}
