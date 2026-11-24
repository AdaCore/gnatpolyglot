#include "test.h"
#include <cstdint>
#include <iostream>

int main() {
    test::call_t([](int32_t v) {
        std::cout << "GOT V = " << v << "\n";
    });
    return 0;
}
