#include "test.h"

int main() {
    auto v = test::f();
    test::p(v);
    v = test::get_x();
    test::p(v);
}
