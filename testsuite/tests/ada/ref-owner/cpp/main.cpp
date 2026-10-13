#include <cstdint>
#include <iostream>

#include "pkg.h"

using gnatpolyglot::memory_owner;
using gnatpolyglot::polyglot_ptr;
using gnatpolyglot::ada::arrays::polyglot_array;
namespace ps = gnatpolyglot::ada::strings;

int main() {
    // Each time, Ada stores a value it allocated in the pointer: it is owned
    // by the library, so the pointer can be passed back to Ada. Once done,
    // the value is given back to the user, so that it is freed.
    std::cout << std::boolalpha;

    // String
    polyglot_ptr<ps::polyglot_string> str(nullptr);
    pkg::replace(str);
    pkg::replace(str);
    std::cout << ps::to_string(*str) << "\n";
    str.set_owner(memory_owner::USER);

    // Arrays of native types
    polyglot_ptr<polyglot_array<int32_t>> i(nullptr);
    pkg::replace(i);
    pkg::replace(i);
    std::cout << i->get(1) << "\n";
    i.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<int8_t>> b(nullptr);
    pkg::replace(b);
    pkg::replace(b);
    std::cout << static_cast<int>(b->get(1)) << "\n";
    b.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<int16_t>> s(nullptr);
    pkg::replace(s);
    pkg::replace(s);
    std::cout << s->get(1) << "\n";
    s.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<int64_t>> l(nullptr);
    pkg::replace(l);
    pkg::replace(l);
    std::cout << l->get(1) << "\n";
    l.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<float>> f(nullptr);
    pkg::replace(f);
    pkg::replace(f);
    std::cout << f->get(1) << "\n";
    f.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<double>> d(nullptr);
    pkg::replace(d);
    pkg::replace(d);
    std::cout << d->get(1) << "\n";
    d.set_owner(memory_owner::USER);

    polyglot_ptr<polyglot_array<bool>> o(nullptr);
    pkg::replace(o);
    pkg::replace(o);
    std::cout << o->get(1) << "\n";
    o.set_owner(memory_owner::USER);

    // Array of a record type
    polyglot_ptr<polyglot_array<pkg::Rec>> r(nullptr);
    pkg::replace(r);
    pkg::replace(r);
    std::cout << r->get(1)->get_v() << "\n";
    r.set_owner(memory_owner::USER);
    return 0;
}
