#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace gnatpolyglot::ada::arrays;
using namespace gnatpolyglot;

// C++ subclass of abstract Root: Set_To_Null sets the access to null
class CppRoot : public test::Root {
public:
    CppRoot() : test::Root(this) {}

    void set_to_null(polyglot_ptr<polyglot_array<int32_t>> & v) const override {
        v = nullptr;
    }
};

static void print_ptr(const polyglot_ptr<polyglot_array<int32_t>> & v) {
    if (!v) {
        std::cout << "null\n";
    } else {
        std::cout << v->get(1) << "\n";
    }
}

int main() {
    polyglot_ptr<polyglot_array<int32_t>> v = nullptr;

    // set_to_new: Ada prints current (null), then sets v to (1=>42, 2=>43)
    test::set_to_new(v);
    // C++ prints element 1 => 42
    std::cout << v->get(1) << "\n";

    // set_to_new: Ada prints current (42 43), then sets v to (1=>44, 2=>45)
    test::set_to_new(v);
    // C++ prints element 1 => 44
    std::cout << v->get(1) << "\n";

    // set_to_null: Ada prints current (44 45), then sets v to null
    test::set_to_null(v);
    // C++ prints null
    print_ptr(v);

    // set_to_null: Ada prints current (null), then keeps null
    test::set_to_null(v);
    // C++ prints null
    print_ptr(v);

    // Create array (1=>998, 2=>999), set v to it
    polyglot_array<int32_t> *a = new polyglot_array<int32_t>(1, 2);
    a->set(1, 998);
    a->set(2, 999);
    v.reset(a, memory_owner::LIBRARY);

    // call_set_to_null_and_print: Ada prints 998 999, dispatches Set_To_Null
    // (CppRoot sets v=null), Ada prints null
    CppRoot r;
    test::call_set_to_null_and_print(r, v);
    // C++ prints null
    print_ptr(v);

    return 0;
}
