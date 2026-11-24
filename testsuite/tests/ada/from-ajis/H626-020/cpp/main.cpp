#include "test.h"

int main() {
    test::T1 t1;
    test::T2 t2;
    // Use T1 references so that the call to p on v2 dispatches, as in AJIS.
    test::T1 &v1 = t1;
    test::T1 &v2 = t2;

    v1.p();
    v2.p();
}
