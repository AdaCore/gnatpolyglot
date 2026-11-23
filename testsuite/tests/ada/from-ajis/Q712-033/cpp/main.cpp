#include <iostream>

#include "hello.h"

using gnatpolyglot::polyglot_ptr;

int main() {
    polyglot_ptr<hello::T> t1 = hello::f1();
    t1 = hello::f2();
    t1 = hello::fa1();
    t1 = hello::fa2();
    polyglot_ptr<hello::T> ref1 = t1;
    hello::p1(*t1, t1, t1, t1, ref1, ref1);
    hello::p2(*t1, t1, t1, t1, ref1, ref1);
    std::cout << "OK" << std::endl;
    return 0;
}
