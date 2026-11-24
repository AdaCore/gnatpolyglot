#include <iostream>

#include "test.h"

int main() {
    std::cout << test::get_c1()->get(1) << "\n";
    test::get_c1()->set(1, 2001);
    std::cout << test::get_c1()->get(1) << "\n";
    std::cout << test::get_c2()->get(1) << "\n";
    test::get_c2()->set(1, 2001);
    std::cout << test::get_c2()->get(1) << "\n";
}
