#include <iostream>

#include "test.h"

int main() {

    std::cout << test::is_even(1) << "\n";
    std::cout << test::is_even(2) << "\n";

    bool is_odd = false;
    test::is_odd(is_odd, 1);
    std::cout << is_odd << "\n";
    test::is_odd(is_odd, 2);
    std::cout << is_odd << "\n";

    test::print_bool(true);
    test::print_bool(false);

    test::print_my_bool(test::MyBool::TRUE);
    test::print_my_bool(test::MyBool::FALSE);
}
