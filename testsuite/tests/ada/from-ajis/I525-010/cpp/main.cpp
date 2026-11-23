#include <iostream>

#include "p_i.h"

int main() {
    p::i::add(1, 2, [](int32_t v3) {
        std::cout << "TOTAL = " << v3 << std::endl;
    });

    p::i::add2(10, 20, [](int32_t v3) {
        std::cout << "TOTAL = " << v3 << std::endl;
    });
    return 0;
}
