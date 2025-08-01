#include <iostream>

#include "test.h"

int main() {
    std::cout << "Got " << test::f() << " from Ada.\n";
    std::cout << "Got " << test::f_expr() << " from Ada.\n";
}
