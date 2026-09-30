#include <iostream>
#include "test.h"

using gnatpolyglot::polyglot_ptr;

test::R obj(2007);

class Child : public test::T {
public:
    Child() : test::T(this) {}

    int p(polyglot_ptr<test::R> &v) const override {
        std::cout << "X is " << v->get_v() << "\n";
        v.set_owner(gnatpolyglot::memory_owner::USER);
        v = polyglot_ptr<test::R>(obj);
        return 0;
    }
};

int main() {
    test::call_p([](polyglot_ptr<test::R> &x) {
        std::cout << "X is " << x->get_v() << "\n";
        x.set_owner(gnatpolyglot::memory_owner::USER);
        x = polyglot_ptr<test::R>(obj);
        return 0;
    });

    Child c;
    return test::call_p(c);
}
