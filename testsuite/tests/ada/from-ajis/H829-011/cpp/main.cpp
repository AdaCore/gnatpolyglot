#include <iostream>

#include "test.h"

int main() {
    std::cout << test::get_lr()->get_v() << "\n";
    test::get_lr()->set_v(2002);
    test::get_lr()->get_v2()->set_x(666);
    std::cout << test::get_lr()->get_v() << "\n";
    std::cout << test::get_lr()->get_v2()->get_x() << "\n";
}
