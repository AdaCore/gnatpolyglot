#include <iostream>

#include "test.h"

int main() {
    int32_t p1 = 0;
    bool p2 = false;
    float p3 = 0.0f;
    test::Enum p4 = test::Enum::A;

    test::p_1(p1, p2, p3, p4);
    std::cout << p1 << ", " << p2 << ", " << p3 << ", " << static_cast<int>(p4) << "\n";

    test::p_2(p1, p2, p3, p4);
    std::cout << p1 << ", " << p2 << ", " << p3 << ", " << static_cast<int>(p4) << "\n";
}
