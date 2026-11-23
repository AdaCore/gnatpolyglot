#include "test.h"
#include <iostream>

// A_T (access to T'Class) and Copy (return A_T) are not bound.
// Only T itself is usable.
int main() {
    // T has no default constructor: T(int32_t f)
    test::T t(0);
    t.set_f(777);
    std::cout << t.get_f() << "\n";
    return 0;
}
