#include <iostream>

#include "../2cpp/include/example.h"
#include "gnatpolyglot_ada_arrays.h"

using namespace gnatpolyglot::ada::arrays;

int main() {
    polyglot_array<example::Counter> arr{1, 4};
    for (auto &c : arr) {
        c.increment();
    }
    for (auto &c : arr) {
        std::cout << c.get_count() << ", ";
    }
    std::cout << "\n";
    example::increment(arr);
    for (auto &c : arr) {
        std::cout << c.get_count() << ", ";
    }
    std::cout << "\n";
    polyglot_array<int> int_arr = example::to_int_arr(arr);
    for (auto &i : int_arr) {
        std::cout << i << ", ";
    }
    std::cout << "\n";

}
