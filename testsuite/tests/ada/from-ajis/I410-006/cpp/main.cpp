#include <iostream>

#include "ref.h"

int main() {
    gnatpolyglot::ada::arrays::polyglot_array<int32_t> b1(1, 2);
    gnatpolyglot::ada::arrays::polyglot_array<int32_t> b2(10, 11);

    b1.set(1, 1000);
    b1.set(2, 2000);
    b2.set(10, 3000);
    b2.set(11, 4000);

    auto b3 = ref::f(b1, b2);

    for (int i = b3.get_begin(); i <= b3.get_end(); i++) {
        std::cout << "[" << i << "] = " << b3.get(i) << "\n";
    }
}
