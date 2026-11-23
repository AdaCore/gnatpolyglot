#include <type_traits>

#include "test.h"

// Type of the parameter of a procedure with a single parameter
template <typename T> T param_type(void (*)(T));

int main() {
    test::Root r;

    test::print1(r.get_str());

    // Ada raises Constraint_Error when Print2 and Print4 are called on the
    // access constant returned by Get_Str: C++ rejects these calls statically.
    static_assert(!std::is_convertible<decltype(r.get_str()),
                                       decltype(param_type(&test::print2))>::value,
                  "print2 must reject an access constant");

    test::print3(*r.get_str());

    static_assert(!std::is_convertible<decltype(*r.get_str()),
                                       decltype(param_type(&test::print4))>::value,
                  "print4 must reject an access constant");

    return 0;
}
