#include "test.h"

int main() {
    test::T v;
    test::p(v);
    v.p1(0.0f);
    test::f();
    test::q1(1, v);
    test::q2(1, v);
}
