#include <iostream>
#include "test.h"

int main() {
    test::get_v()->set_f1(5);
    test::get_v()->set_f2(10);
    std::cout << "F1, F2 = " << test::get_v()->get_f1() << ", " << test::get_v()->get_f2() << "\n";
    test::R new_object(0, 0);
    new_object.set_f1(15);
    new_object.set_f2(20);
    test::set_v(new_object);
    std::cout << "F1, F2 = " << test::get_v()->get_f1() << ", " << test::get_v()->get_f2() << "\n";
}
