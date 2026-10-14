#include "test.h"

int main() {
    static_assert(std::is_same<gnatpolyglot::polyglot_ptr<const test::T>,
                               decltype(test::id(nullptr))>::value,
                  "type is not const");
    // We mostly need to make sure that member functions used by polyglot_ptr
    // do no prevent compilation when T is const.
    gnatpolyglot::polyglot_ptr<const test::T> ptr = test::id(new const test::T{});
    ptr.set_owner(gnatpolyglot::memory_owner::USER);
}
