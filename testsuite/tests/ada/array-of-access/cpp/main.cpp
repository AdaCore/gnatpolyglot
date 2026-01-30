#include "polyglot_ada_arrays.h"
#include "polyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace polyglot;
using namespace polyglot::ada::arrays;

int main() {
    test::Rec r1(0);
    test::Rec r2(1);
    test::Rec r3(2);
    polyglot_array<polyglot_ptr<test::Rec>> arr(1, 3);
    arr.set(1, polyglot_ptr<test::Rec>(r1));
    arr.set(2, polyglot_ptr<test::Rec>(r2));
    arr.set(3, polyglot_ptr<test::Rec>(r3));
    for (int i = 1; i <= 3; i++) {
       std::cout << arr.get(i)->get_i() << ", ";
    }
    std::cout << "\n";
    polyglot_array<polyglot_ptr<test::Rec>> arr2 = test::inc_arr(arr);
    for (int i = 1; i <= 3; i++) {
       std::cout << arr.get(i)->get_i() << ", ";
    }
    std::cout << "\n";
    for (int i = 1; i <= 3; i++) {
       std::cout << arr2.get(i)->get_i() << ", ";
    }
    std::cout << "\n";
}
