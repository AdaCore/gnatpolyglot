#include <iostream>

#include "test.h"
#include "other.h"

int main() {

    try {
        test::get_exception(1);
    } catch (const test::Exc1 &e) {
        std::cout << "caught" << e.what() << "\n";
    }

    try {
        test::get_exception(2);
    } catch (const test::Exc2 &e) {
        std::cout << "caught" << e.what() << "\n";
    }

    try {
        test::get_exception(3);
    } catch (const polyglot::exceptions::polyglot_exception &e) {
        std::cout << "caught" << e.what() << "\n";
    }

    try {
        test::get_exception(0);
    } catch (const other::Exc1 &e) {
        std::cout << "caught" << e.what() << "\n";
    }

    // should not throw: verify that the exception was cleared.
    test::get_exception(4);
}
