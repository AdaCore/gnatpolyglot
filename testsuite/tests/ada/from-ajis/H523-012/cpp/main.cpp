#include "other.h"

int main() {
    test::T v(99);
    other::p_1(v);
    other::p_2(gnatpolyglot::polyglot_ptr<test::T>(v));
    other::p_3(gnatpolyglot::polyglot_ptr<test::T>(v));
}
