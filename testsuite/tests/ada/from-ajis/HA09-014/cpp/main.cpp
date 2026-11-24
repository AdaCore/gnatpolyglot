#include "test.h"

int main() {
    test::An_Unconstrained_Array arr1(test::Enum::a, test::Enum::b);
    test::A_Constrained_Array arr2;

    arr1.set_element_at(test::Enum::a, 'A');
    arr1.set_element_at(test::Enum::b, 'B');

    arr2.set_element_at(test::Enum::a, 'a');
    arr2.set_element_at(test::Enum::b, 'b');
    arr2.set_element_at(test::Enum::c, 'c');
    arr2.set_element_at(test::Enum::d, 'd');
    arr2.set_element_at(test::Enum::e, 'e');
    arr2.set_element_at(test::Enum::f, 'f');

    test::display(arr1, arr2);

    return 0;
}
