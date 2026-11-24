#include <iostream>

#include "test.h"

int main() {
    test::T1 t1;
    // The discriminant of t2 keeps an access to t1
    test::T2 t2(gnatpolyglot::polyglot_ptr<test::T1>(t1));
    if (t2.get_i() == 1961) {
        std::cout << "OK" << std::endl;
    }
    return 0;
}
