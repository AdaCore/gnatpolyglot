#include "test.h"

int main() {
    test::B b;
    b.foo();
    test::C c;
    c.foo();
    c.bar();
    test::E e;
    e.foo();
}
