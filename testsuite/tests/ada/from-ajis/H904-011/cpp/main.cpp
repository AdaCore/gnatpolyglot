#include "test.h"
#include <iostream>

int main() {
    // An_Unconstrained_Array and A_Constrained_Array are enum-indexed arrays
    // and are not yet bindable. Display is therefore not bound either.
    // This test exercises the Enum type and calls Display as it should work
    // once enum-indexed arrays are supported.
    test::An_Unconstrained_Array arr1(test::Enum::A, test::Enum::B);
    test::A_Constrained_Array arr2;

    arr1.set_element_at(test::Enum::A, 'A');
    arr1.set_element_at(test::Enum::B, 'B');

    arr2.set_element_at(test::Enum::A, 'a');
    arr2.set_element_at(test::Enum::B, 'b');
    arr2.set_element_at(test::Enum::C, 'c');
    arr2.set_element_at(test::Enum::D, 'd');
    arr2.set_element_at(test::Enum::E, 'e');
    arr2.set_element_at(test::Enum::F, 'f');

    test::display(arr1, arr2);

    return 0;
}
