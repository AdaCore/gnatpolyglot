#include "test.h"
#include <iostream>

// "/" is abstract (disabled) and not bound.
// "*" is bound as operator_mult(int32_t, int32_t) -> int32_t.

int main() {
    std::cout << test::operator_mult(0, 0) << "\n";
    return 0;
}
