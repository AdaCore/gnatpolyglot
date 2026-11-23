#include "p.h"
#include <iostream>

// Overloaded subprograms disambiguated by gnatpolyglot.
// B(V : A_R) may or may not be bound (A_R = access all R).
// D(V : D_Type) disambiguated — AJIS Rename stripped.

int main() {
    p::a(0);

    p::R r;
    r.b();

    p::T t;
    t.c();

    p::TChild tc;
    tc.c();

    p::d(0);
    p::d_1(0);

    return 0;
}
