#include "polyglot_ada_arrays.h"
#include "test.h"
#include <cassert>
#include <iostream>

using namespace polyglot;
using namespace polyglot::ada::arrays;

template <typename T>
void print(T arr, int size) {
    for (int i = 1; i <= size; i++) {
        std:: cout << arr.get(i) << ", ";
    }
    std::cout << "\n";
}

void inline_vec() {
    std::cout << "InlineVector\n";
    test::InlineVector v;
    polyglot_array<int>::view arr = v.get_data();
    v.push(1);
    v.push(2);
    v.push(3);
    print<test::InlineVector>(v, v.get_size());
    print<polyglot_array<int>&>(arr, arr->size());
    v.push(4);
    v.push(5);
    v.push(6);
    print<test::InlineVector>(v, v.get_size());
    print<polyglot_array<int>&>(arr, arr->size());
    v.pop();
    v.pop();
    v.pop();
    v.push(7);
    v.push(8);
    v.push(9);
    print<test::InlineVector>(v, v.get_size());
    print<polyglot_array<int>&>(arr, arr->size());
}

void vec() {
    std::cout << "Vector\n";
    test::Vector v;
    polyglot_ptr<polyglot_array<int>> arr = v.get_data();
    assert(!arr);
    v.push(1);
    v.push(2);
    v.push(3);
    print<test::Vector>(v, v.get_size());
    arr = v.get_data();
    print<polyglot_array<int>&>(*arr, arr->size());
    v.push(4);
    v.push(5);
    v.push(6);
    print<test::Vector>(v, v.get_size());
    arr = v.get_data();
    print<polyglot_array<int>&>(*arr, arr->size());
    v.pop();
    v.pop();
    v.pop();
    v.push(7);
    v.push(8);
    v.push(9);
    print<test::Vector>(v, v.get_size());
    print<polyglot_array<int>&>(*arr, arr->size());
    v.free();
}

int main() {
    inline_vec();
    vec();
}
