#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <cassert>

using namespace gnatpolyglot::ada::arrays;
using namespace gnatpolyglot;

int main() {
    // C++ permits borrowed STATIC pointers for these non-retaining procedures;
    // AJIS-specific allocator queries and escape exceptions do not apply.
    auto v = test::get_v();
    polyglot_ptr<polyglot_array<int32_t>> pv(*v);
    assert(pv.get_owner() == memory_owner::STATIC);
    test::print1(*v);
    test::print2(pv);
    test::print3(pv);

    auto v2 = test::get_v2();
    assert(v2.get_owner() == memory_owner::LIBRARY);
    test::print1(*v2);
    test::print2(v2);
    test::print3(v2);

    test::R r;
    auto f = r.get_f();
    polyglot_ptr<polyglot_array<int32_t>> pf(*f);
    assert(pf.get_owner() == memory_owner::STATIC);
    test::print1(*f);
    test::print2(pf);
    test::print3(pf);

    auto f2 = r.get_f2();
    assert(f2.get_owner() == memory_owner::LIBRARY);
    test::print1(*f2);
    test::print2(f2);
    test::print3(f2);

    polyglot_array<int32_t> a(1, 2);
    a.set(1, 9);
    a.set(2, 10);
    polyglot_ptr<polyglot_array<int32_t>> pa(a);
    assert(pa.get_owner() == memory_owner::STATIC);
    test::print1(a);
    test::print2(pa);
    test::print3(pa);
    return 0;
}
