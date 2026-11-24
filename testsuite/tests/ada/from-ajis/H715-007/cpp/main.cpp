#include "test.h"
#include <iostream>

class TChild : public test::T {
protected:
    // Needed for Get_Copy (T'Class copy of a C++-derived object).
    TChild *internal_clone_(gnatpolyglot::data *data) override {
        return new TChild(*this, data);
    }

    TChild(const TChild &, gnatpolyglot::data *data) : test::T(data) {}

public:
    TChild() : test::T(this) {}
};

int main() {
    // Set stores V in a library global: pass ownership to the LIBRARY.
    gnatpolyglot::polyglot_ptr<test::T> v(new TChild(), gnatpolyglot::memory_owner::LIBRARY);

    test::set(true, v);

    gnatpolyglot::polyglot_ptr<test::T> w = test::get_copy();
    gnatpolyglot::polyglot_ptr<test::T> x = test::get_reference();

    v->set_f(5);
    w->set_f(6);
    x->set_f(7);

    std::cout << std::boolalpha;
    std::cout << (v.get() == w.get()) << "\n";
    std::cout << (v.get() == x.get()) << "\n";
    std::cout << v->get_f() << "\n";
    std::cout << w->get_f() << "\n";
    std::cout << x->get_f() << "\n";
    return 0;
}
