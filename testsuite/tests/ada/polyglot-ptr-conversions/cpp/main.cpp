#include <type_traits>

#include "test.h"

using gnatpolyglot::polyglot_ptr;

// Conversions to a parent type or to a const type are allowed
static_assert(std::is_convertible<polyglot_ptr<test::Child>,
                                  polyglot_ptr<test::Parent>>::value,
              "child to parent");
static_assert(std::is_convertible<polyglot_ptr<test::Parent>,
                                  polyglot_ptr<const test::Parent>>::value,
              "non-const to const");
static_assert(std::is_assignable<polyglot_ptr<const test::Parent> &,
                                 polyglot_ptr<test::Child>>::value,
              "child to const parent");

// Others are rejected
static_assert(!std::is_convertible<polyglot_ptr<const test::Parent>,
                                   polyglot_ptr<test::Parent>>::value,
              "const to non-const");
static_assert(!std::is_assignable<polyglot_ptr<test::Parent> &,
                                  polyglot_ptr<const test::Parent>>::value,
              "const to non-const");
static_assert(!std::is_convertible<polyglot_ptr<test::Parent>,
                                   polyglot_ptr<test::Child>>::value,
              "parent to child");

int main() {
    polyglot_ptr<test::Child> c = test::get_child();

    polyglot_ptr<test::Parent> p = c;
    test::print(p);

    polyglot_ptr<const test::Parent> cst(nullptr);
    test::print(cst);
    cst = c;
    test::print(cst);
    return 0;
}
