#include <iostream>

#include "test.h"

using gnatpolyglot::polyglot_ptr;

int main() {
    static_assert(std::is_same<gnatpolyglot::polyglot_ptr<const test::T>,
                               decltype(test::id(nullptr))>::value,
                  "type is not const");
    // We mostly need to make sure that member functions used by polyglot_ptr
    // do no prevent compilation when T is const.
    gnatpolyglot::polyglot_ptr<const test::T> ptr = test::id(new const test::T{});
    ptr.set_owner(gnatpolyglot::memory_owner::USER);

    std::cout << std::boolalpha;

    // Out parameter
    polyglot_ptr<const test::T> t(nullptr);
    test::get(t);
    std::cout << bool(t) << "\n";

    // Callback parameter
    test::call([](polyglot_ptr<const test::T> a) {
        std::cout << bool(a) << "\n";
    });

    // Callback out parameter
    test::call_out([](polyglot_ptr<const test::T> &a) { test::get(a); });

    // Non-tagged type: global variable getter
    polyglot_ptr<const test::R> x = test::get_x();
    std::cout << x->get_v() << "\n";

    // Function result
    std::cout << test::id_r(x)->get_v() << "\n";

    // Out parameter
    polyglot_ptr<const test::R> r(nullptr);
    test::get_r(r);
    std::cout << r->get_v() << "\n";

    // Component getter
    test::Holder h(x);
    std::cout << h.get_acc()->get_v() << "\n";

    // Callback parameter
    test::call_r([](polyglot_ptr<const test::R> a) {
        std::cout << a->get_v() << "\n";
    });

    // Callback out parameter
    test::call_out_r([](polyglot_ptr<const test::R> &a) { a = test::get_x(); });
    return 0;
}
