#include <iostream>

#include "test.h"

int main() {
    test::set_a(1);
    test::set_b(2);
    test::set_c(3);
    std::cout << test::get_a() << ", " << test::get_b() << ", " << test::get_c() << "\n";
}
