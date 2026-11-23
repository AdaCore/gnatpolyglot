#include <iostream>

#include "test.h"

int main() {
    try {
        std::cout << "start...\n";
        test::raise_something();
    } catch (const gnatpolyglot::exceptions::polyglot_exception &e) {
        std::cout << "Caught regular exception\n";
    }
    // finally
    std::cout << "finished\n";
}
