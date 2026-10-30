#include <iostream>
#include "pkg.h"

namespace ps = gnatpolyglot::ada::strings;
namespace pa = gnatpolyglot::ada::arrays;
using gnatpolyglot::polyglot_ptr;

int main() {
    // After each setter, Clobber reuses the stack area of the generated
    // setter, then Ada prints the bounds it stored.

    // Package-level String_Access variable
    ps::polyglot_string s("Hello");
    pkg::set_v(polyglot_ptr<ps::polyglot_string>(s));
    pkg::clobber();
    std::cout << "V bounds:" << ps::to_string(pkg::v_bounds()) << "\n";

    // Package-level access-to-unconstrained-array variable
    pa::polyglot_array<int32_t> a(10, 12);
    pkg::set_a(polyglot_ptr<pa::polyglot_array<int32_t>>(a));
    pkg::clobber();
    std::cout << "A bounds:" << ps::to_string(pkg::a_bounds()) << "\n";

    // Record component of type String_Access
    pkg::Rec r(polyglot_ptr<ps::polyglot_string>(nullptr));
    ps::polyglot_string c("World!");
    r.set_c(polyglot_ptr<ps::polyglot_string>(c));
    pkg::clobber();
    std::cout << "C bounds:" << ps::to_string(r.c_bounds()) << "\n";

    // Null value
    pkg::set_v(nullptr);
    std::cout << "V is null: " << std::boolalpha << pkg::v_is_null() << "\n";
}
