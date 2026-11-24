#include <iostream>
#include "test.h"

using gnatpolyglot::memory_owner;
using gnatpolyglot::polyglot_ptr;

int main() {
    polyglot_ptr<test::R> r(new test::R(0, 0), memory_owner::USER);

    r->set_a(1);
    r->set_b(2);

    // AJIS: r.setOwner(Owner.NATIVE)
    r.set_owner(memory_owner::LIBRARY);

    r = test::f2(r);

    std::cout << r->get_a() << std::endl;
    std::cout << r->get_b() << std::endl;
    return 0;
}
