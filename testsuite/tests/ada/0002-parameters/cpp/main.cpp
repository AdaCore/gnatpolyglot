#include <iostream>

#include "test.h"

int main() {
    test::p_param(42);
    std::cout << "Got " << test::f_param(2, 3, 4) << " from Ada.\n";
}
