#include "test.h"
#include <iostream>

int main() {
    test::get_v_all()->set_y(10);
    test::get_v_all()->set_z(999);

    std::cout << test::get_v()->get_y() << "\n";
    std::cout << test::get_v()->get_z() << "\n";
    return 0;
}
