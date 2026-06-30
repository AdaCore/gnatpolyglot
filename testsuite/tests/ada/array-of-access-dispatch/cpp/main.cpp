#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <cassert>
#include <iostream>

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::arrays;

class Child : public test::T {
public:
    Child() : test::T(this) {}

    void foo(polyglot_ptr<polyglot_array<polyglot_ptr<::test::Rec>>> &a) const override {
       polyglot_ptr<polyglot_array<polyglot_ptr<::test::Rec>>> res{
           new polyglot_array<polyglot_ptr<::test::Rec>>{test::inc_arr(*a)}};
       a = res;
    }

};

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

    polyglot_ptr<polyglot_array<polyglot_ptr<::test::Rec>>> ptr{arr};
    Child c{};
    test::call_foo(c, ptr);
    assert(ptr->data_().data != arr.data_().data);
    for (int i = 1; i <= 3; i++) {
       std::cout << arr.get(i)->get_i() << ", ";
    }
    std::cout << "\n";
    for (int i = 1; i <= 3; i++) {
       std::cout << ptr->get(i)->get_i() << ", ";
    }
    std::cout << "\n";
    ptr.set_owner(memory_owner::USER);
}
