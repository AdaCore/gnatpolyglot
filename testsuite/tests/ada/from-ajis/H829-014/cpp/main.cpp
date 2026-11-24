#include <cstdint>
#include <iostream>

#include "test.h"

int main() {
    int32_t v1 = test::f1();
    int64_t v2 = test::f2();

    std::cout << v1 << "\n";
    std::cout << v2 << "\n";

    int32_t r1 = v1;
    int64_t r2 = v2;

    test::minus_one_int(r1);
    test::minus_one_long(r2);

    std::cout << r1 << "\n";
    std::cout << r2 << "\n";
}
