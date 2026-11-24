#include <iostream>
#include "test.h"

int main() {
    auto null_rec = test::create_null_rec();
    auto null_arr = test::create_null_an_array();

    std::cout << std::boolalpha;
    std::cout << !null_rec << "\n";
    std::cout << !null_arr << "\n";
    std::cout << test::is_null_rec(true, nullptr) << "\n";
    std::cout << test::is_null_an_array(true, nullptr) << "\n";
}
