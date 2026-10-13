#include "test.h"
#include "test_nested.h"
#include <cassert>

int main() {
    gnatpolyglot::polyglot_ptr<test::T> tclass = test::test_t_class_make();
    test::T t = test::test_t_make();

    test::nested::foo({});
    test::nested::foo_1({});

    assert(test::T{} == test::T{});
}
