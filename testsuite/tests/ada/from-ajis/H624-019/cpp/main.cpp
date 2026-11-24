#include <iostream>

#include "test.h"

int main() {
    std::cout << std::boolalpha;
    std::cout << test::is_a(test::Enum::A) << "\n";
    std::cout << test::is_a(test::Enum::B) << "\n";
}
