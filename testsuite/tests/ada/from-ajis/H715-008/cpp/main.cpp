#include "test.h"

int main() {
    //  T has no default value for A, so no parameterless constructor is
    //  generated: build it with an arbitrary value, then set A.
    test::T v(0);

    v.set_a(999);

    v.p();
}
