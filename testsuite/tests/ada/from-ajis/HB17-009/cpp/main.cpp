#include "test.h"
#include <iostream>

// Call takes an anonymous access-to-procedure parameter — not yet bound.

int main() {
    test::call([](int v1, int& v2, int& v3) {
        std::cout << v1 << ", " << v3 << "\n";
        v2 = 10;
        v3 = 11;
    });
    return 0;
}
