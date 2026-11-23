#include "test.h"
#include <gnatpolyglot_ada_exceptions.h>
#include <iostream>
#include <stdexcept>

int main() {
    try {
        test::raise_exception();
    } catch (const gnatpolyglot::ada::exceptions::AdaException &e) {
        std::cout << "EXCEPTION GOT IN JAVA\n";
        std::cout << e.what() << "\n";
    }

    // Stand-in for Java's NullPointerException: a non-Ada host exception.
    test::call_a_proc([]() {
        throw std::logic_error("null pointer");
    });
}
