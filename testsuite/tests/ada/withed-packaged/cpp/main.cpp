#include "test.h"

using namespace polyglot::ada::arrays;

int main() {
    test::T value{rec1::R1()};
    std::cout << value.get_a()->get_i() << "\n";
    rec2::R2 r2;
    test::p1(r2);
    polyglot_array<rec3::R3> arr(1, 3);
    test::p2(arr);
}
