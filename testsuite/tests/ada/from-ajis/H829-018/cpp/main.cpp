#include "test.h"

int main() {
    test::T v;
    v.p1();
    v.p2();        // not bound: anonymous access T parameter
    test::p3(v);
    test::p4(v);   // not bound: anonymous access T'Class parameter
}
