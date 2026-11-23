#include "test.h"
#include <iostream>

// P(V1: R; V2: T) abstract and P(V1: R; V2: T2) concrete become methods on R.
// But this makes R's view struct fail to instantiate an abstract R.

int main() {
    test::R r(876);
    test::T2 t2;
    r.p(t2);
    return 0;
}
