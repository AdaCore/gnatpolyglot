#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace gnatpolyglot::ada::arrays;
using namespace gnatpolyglot;

// C++ subclass that overrides Get_An_Array to return an array of 43s
class CppRoot : public test::Root {
public:
    CppRoot() : test::Root(this) {}

    polyglot_ptr<polyglot_array<int32_t>> get_an_array() const override {
        polyglot_array<int32_t> *a = new polyglot_array<int32_t>(1, 10);
        for (int j = 1; j <= 10; ++j) {
            a->set(j, 43);
        }
        return polyglot_ptr<polyglot_array<int32_t>>(a);
    }
};

int main() {
    // Free function: returns array of 42s
    auto a = test::get_an_array();
    std::cout << a->get(3) << "\n";

    // Method on plain Root: returns array of 42s
    test::Root r;
    auto a2 = r.get_an_array();
    std::cout << a2->get(3) << "\n";

    // Method on CppRoot override: returns array of 43s
    CppRoot r2;
    auto a3 = r2.get_an_array();
    std::cout << a3->get(3) << "\n";

    return 0;
}
