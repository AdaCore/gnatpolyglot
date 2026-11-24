#include "test.h"
#include "gnatpolyglot_ptr.h"

int main() {
    // R has no default constructor: R(int32_t x)
    test::R r(999);
    gnatpolyglot::polyglot_ptr<test::R> ptr(r);
    test::proc_with_owned_allowed(ptr);
    test::proc_with_owned_not_allowed(ptr);
    return 0;
}
