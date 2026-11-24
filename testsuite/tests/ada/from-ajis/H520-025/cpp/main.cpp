#include <iostream>
#include "test.h"

int main() {
    // No default constructor: use the aggregate-like one with
    // default-ish values.
    test::ARecord first(nullptr, 0);
    test::ARecord second(nullptr, 0);

    second.set_v(20);

    first.set_next(gnatpolyglot::polyglot_ptr<test::ARecord>(second));

    std::cout << "Second value is: " << second.get_v() << "\n";
}
