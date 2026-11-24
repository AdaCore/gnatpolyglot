#include <cstdint>
#include <iostream>
#include "test.h"

int main() {
    std::cout << test::call_char([](char v) { return v; }, 'c') << std::endl;

    std::cout << test::call_long([](int64_t v) { return v; }, 99) << std::endl;
    return 0;
}
