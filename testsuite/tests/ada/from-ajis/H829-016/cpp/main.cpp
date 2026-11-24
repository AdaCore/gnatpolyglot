#include <iostream>
#include <stdexcept>

#include "test.h"

struct MyException : std::runtime_error {
    using std::runtime_error::runtime_error;
};

int main() {
    try {
        test::call_callback([] { throw MyException("callback failed"); });
        std::cout << "NO EXCEPTION\n";
    } catch (const MyException &e) {
        std::cout << "EXCEPTION MESSAGE: " << e.what() << "\n";
    } catch (const std::exception &e) {
        std::cout << "UNEXPECTED EXCEPTION: " << e.what() << "\n";
    }
}
