#include <iostream>

#include "pkg.h"

using gnatpolyglot::memory_owner;
using gnatpolyglot::polyglot_ptr;

int main() {
    // A C++ callback returning an access value, called by Ada. Ada may keep
    // the returned access: give the object to the library (default owner).
    polyglot_ptr<pkg::Rec> r(new pkg::Rec(42));
    pkg::call([&r] { return r; });
    pkg::call([] { return polyglot_ptr<pkg::Rec>(nullptr); });
    // Ada did not keep it: give it back to the user, so that it is freed.
    r.set_owner(memory_owner::USER);

    // An Ada callback returning an access value, called by C++
    std::cout << pkg::get()()->get_i() << "\n";
    return 0;
}
