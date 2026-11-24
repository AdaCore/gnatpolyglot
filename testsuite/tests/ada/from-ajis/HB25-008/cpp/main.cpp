#include "test.h"
#include <iostream>

// An_Array (array indexed by Enum) is not yet bound.
// Print_Contents not generated.

int main() {
    test::AnArray arr;
    arr.set_element_at(test::Enum::A, 90);
    std::cout << "VALUE OF A IS " << arr.get_element_at(test::Enum::A) << "\n";
    return 0;
}
