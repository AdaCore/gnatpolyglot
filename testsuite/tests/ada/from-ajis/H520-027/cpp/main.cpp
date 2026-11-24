#include <iostream>
#include "test.h"

int main() {
    // test::AnArray is not bound (enum-indexed arrays not supported)
    test::AnArray arr;
    arr.set_element_at(test::Enum::A, 90);
    std::cout << "VALUE OF A IS " << arr.get_element_at(test::Enum::A) << "\n";
}
