#include "gnatpolyglot_ada_arrays.h"
#include "test.h"

using namespace gnatpolyglot::ada::arrays;

int main() {
    {
        // Host allocation, element views, and finalization at scope exit.
        polyglot_array<test::Root> r(1, 2);
        r.get(1)->set_v(41);
        r.get(2)->set_v(42);
        test::print_all(r);

        // Ada allocation and copies during parameter passing.
        test::print_all(test::f());
    }

    return 0;
}
