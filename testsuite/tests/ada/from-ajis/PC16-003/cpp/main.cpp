#include "hello.h"
#include "gnatpolyglot_ptr.h"
#include <cassert>

int main() {
    // Several controlling parameters make AdaRecord final. Exercise every
    // parameter mode using Ada records; host subclass dispatch remains excluded.
    hello::AdaRecord v1(1), v2(2), a3(3), a4(4), v5(5), v6(6);
    hello::AdaRecord a7(7), a8(8), v9(9), v10(10), a11(11), a12(12);
    // Test neither retains nor frees these pointers. All owners outlive the calls.
    gnatpolyglot::polyglot_ptr<hello::AdaRecord> v3(a3), v4(a4);
    gnatpolyglot::polyglot_ptr<hello::AdaRecord> v7(a7), v8(a8);
    gnatpolyglot::polyglot_ptr<hello::AdaRecord> v11(a11), v12(a12);

    v1.test(v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
    assert(v5.get_value() == 1 && v6.get_value() == 2);
    assert(v7->get_value() == 3 && v8->get_value() == 4);
    assert(v9.get_value() == 5 && v10.get_value() == 6);
    assert(v11->get_value() == 7 && v12->get_value() == 8);

    v9.test(v10, v11, v12, v5, v6, v7, v8, v9, v10, v11, v12);
    // P1/P9 and P2/P10 alias tagged objects: the out assignments update
    // the input objects before Ada assigns P5/P6 from them.
    assert(v5.get_value() == 1 && v6.get_value() == 2);
    assert(v7->get_value() == 7 && v8->get_value() == 8);
    assert(v9.get_value() == 1 && v10.get_value() == 2);
    assert(v11->get_value() == 3 && v12->get_value() == 4);
    return 0;
}
