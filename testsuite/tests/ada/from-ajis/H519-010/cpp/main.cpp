#include <iostream>
#include "test.h"
int main() {
    // An_Enum_Array (enum-indexed) and V are not bound.
    // Sum and F (ordinary array functions) ARE bound.
    auto arr = test::f();
    std::cout << "Sum = " << test::sum(arr) << "\n";
}
