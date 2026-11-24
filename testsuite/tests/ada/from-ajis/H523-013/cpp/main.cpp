#include <iostream>

#include "test.h"

int main() {
    test::T v1(0);
    test::T v2(0);
    v1.set_v(1);
    v2.set_v(2);

    gnatpolyglot::polyglot_ptr<test::T> v3 = test::get_t_child(1);
    gnatpolyglot::polyglot_ptr<test::T> v4 = test::get_t_child(2);

    test::T r1 = v1.f(v2);
    test::T r2 = v3->f(*v4);

    std::cout << "r1 = " << r1.get_v() << std::endl;
    std::cout << "r2 = " << r2.get_v() << std::endl;
    return 0;
}
