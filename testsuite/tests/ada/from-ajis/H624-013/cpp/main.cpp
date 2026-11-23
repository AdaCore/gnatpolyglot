#include <iostream>
#include "test.h"

int main() {
    test::R obj(0);
    gnatpolyglot::polyglot_ptr<test::R> r(nullptr);
    test::p1(r);

    std::cout << "AFTER P1, r is " << r->get_v() << "\n";

    obj.set_v(2007);
    r = gnatpolyglot::polyglot_ptr<test::R>(obj);
    test::p2(r);
    std::cout << "AFTER P2, obj is " << obj.get_v() << "\n";
    std::cout << "AFTER P2, r is " << r->get_v() << "\n";

    test::call_p([&obj](gnatpolyglot::polyglot_ptr<test::R> &x) {
        std::cout << "IN P_ACC, X is " << x->get_v() << "\n";
        x = gnatpolyglot::polyglot_ptr<test::R>(obj);
    });

    test::RT obj_t(0);
    gnatpolyglot::polyglot_ptr<test::RT> r_t(nullptr);
    test::p1_t(r_t);

    std::cout << "AFTER P1_T, r is " << r_t->get_v() << "\n";

    obj_t.set_v(2007);
    r_t = gnatpolyglot::polyglot_ptr<test::RT>(obj_t);
    test::p2_t(r_t);
    std::cout << "AFTER P2_T, obj is " << obj_t.get_v() << "\n";
    std::cout << "AFTER P2_T, r is " << r_t->get_v() << "\n";

    test::call_p_t([&obj_t](gnatpolyglot::polyglot_ptr<test::RT> &x) {
        std::cout << "IN P_T_ACC, X is " << x->get_v() << "\n";
        x = gnatpolyglot::polyglot_ptr<test::RT>(obj_t);
    });
}
