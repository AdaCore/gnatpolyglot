#include <iostream>

#include "test.h"
#include "gnatpolyglot_ada_exceptions.h"

class Deriv : public test::Tag {
public:
    Deriv(int i) : test::Tag(i, this) {}

    void raise_exc() const {
        switch (this->get_i()) {
            case 1:
                throw test::Exc1("Foo");
            case 2:
                throw test::Exc2("Bar");
            case 3:
                throw gnatpolyglot::ada::exceptions::AdaException("Baz");
            case 4:
                throw gnatpolyglot::ada::exceptions::ProgramError("FooBar");
            case 5:
                throw std::invalid_argument("host exception");
        }
    }
};

int main() {
    Deriv d(1);
    try {
        test::get_exception(d);
    } catch (const test::Exc1 &e) {
        std::cout << "C++ caught Exc1: " << e.what() << "\n";
    }
    try {
        d.set_i(2);
        test::get_exception(d);
    } catch (const test::Exc2 &e) {
        std::cout << "C++ caught Exc2: " << e.what() << "\n";
    }
    try {
        d.set_i(3);
        test::get_exception(d);
    } catch (const gnatpolyglot::ada::exceptions::AdaException &e) {
        std::cout << "C++ caught AdaException: " << e.what() << "\n";
    }
    try {
        d.set_i(4);
        test::get_exception(d);
    } catch (const gnatpolyglot::ada::exceptions::ProgramError &e) {
        std::cout << "C++ caught Program_Error: " << e.what() << "\n";
    }
    try {
        d.set_i(5);
        test::get_exception(d);
    } catch (const gnatpolyglot::ada::exceptions::AdaException &e) {
        std::cout << "C++ caught AdaException: " << e.what() << "\n";
    }
}
