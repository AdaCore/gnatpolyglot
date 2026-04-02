#include <iostream>

#include "gnatpolyglot_ptr.h"
#include "test.h"

int main() {
    gnatpolyglot::polyglot_ptr<test::Rec> acc(new test::Rec(1));
    test::set_global_acc(acc);
    std::cout << acc->get_i() << "\n";
    test::foo();
    test::foo();
    std::cout << acc->get_i() << "\n";
    test::foo();
    std::cout << test::get_global_acc()->get_i() << "\n";
    test::bar();
    std::cout << test::get_global_acc().get() << "\n";
    acc.set_owner(gnatpolyglot::memory_owner::USER);
}
