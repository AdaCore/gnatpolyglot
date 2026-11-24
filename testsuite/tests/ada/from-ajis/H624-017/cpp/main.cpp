#include <iostream>
#include "test.h"
#include <gnatpolyglot_ada_exceptions.h>

int main() {
    try {
        (void)test::raise_exception();
    } catch (const gnatpolyglot::ada::exceptions::ConstraintError& e) {
        std::cout << "EXCEPTION GOT IN JAVA\n";
        std::cout << e.what() << "\n";
    }
}
