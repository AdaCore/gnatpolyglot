#include <iostream>

#include "polyglot_ptr.h"
#include "test.h"

int main() {
    polyglot::polyglot_ptr<test::Rec> acc(new test::Rec(1));
    test::set_global_acc(acc);
    std::cout << acc->get_i() << "\n";
    test::foo();
    test::foo();
    std::cout << acc->get_i() << "\n";
    test::foo();
    std::cout << test::get_global_acc()->get_i() << "\n";
    test::bar();
    std::cout << test::get_global_acc().get() << "\n";
    acc.set_owner(polyglot::memory_owner::USER);
}
