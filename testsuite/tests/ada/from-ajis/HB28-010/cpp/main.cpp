#include <iostream>

#include "test.h"

int main() {
    std::cout << test::get_v1()->get_v() << "\n";
    std::cout << test::get_v2()->get_v() << "\n";
}
