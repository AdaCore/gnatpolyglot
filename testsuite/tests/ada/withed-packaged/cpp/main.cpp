#include <iostream>

#include "gnatpolyglot_ptr.h"
#include "test.h"
#include "renamed.h"

using namespace gnatpolyglot::ada::arrays;

int main() {
    test::T value{rec1::R1()};
    std::cout << value.get_a()->get_i() << "\n";
    rec2::R2 r2;
    test::p1(r2);
    polyglot_array<rec3::R3> arr(1, 3);
    test::p2(arr);

    test::p3(renamed::T());

    rec4::R4 r4;
    test::p4(gnatpolyglot::polyglot_ptr<rec4::R4>(r4));

    test::p6(rec6::inst::Typ{1});
}
