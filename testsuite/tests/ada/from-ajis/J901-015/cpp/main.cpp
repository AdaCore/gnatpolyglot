#include "test.h"
#include <iostream>

// Ptr_F, Ptr_P, Call_F, Call_P not bound (access-to-subprogram types).
// T is abstract; shadow subclass provides C++ implementations.
// test_all dispatches through T'Class, catching exceptions internally.

class MyT : public test::T {
public:
    MyT() : test::T(this) {}

    test::R f1() const override {
        return test::R(1, 2);
    }

    gnatpolyglot::polyglot_ptr<test::R> f2() const override {
        return gnatpolyglot::polyglot_ptr<test::R>(new test::R(3, 4));
    }

    void p(gnatpolyglot::polyglot_ptr<test::R> &x) const override {
        x = gnatpolyglot::polyglot_ptr<test::R>(new test::R(5, 6));
    }

    test::T *internal_clone_(gnatpolyglot::data *) override {
        return new MyT();
    }
};

int main() {
    MyT t;
    test::test_all(t);
    return 0;
}
