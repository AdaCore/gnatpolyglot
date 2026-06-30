#include <iostream>

#include "../2cpp/include/exceptions.h"
#include "gnatpolyglot_ada_exceptions.h"

class Deriv : public exceptions::Tag {
public:
    Deriv() : exceptions::Tag(this) {}

    void raise_exc() const {
        throw exceptions::Exc2("Foo");
    }
};

int main() {
    Deriv d;
    try {
        exceptions::raise_exc(false);
    } catch (const exceptions::Exc1 &e) {
        std::cout << "C++ caught Exc1: " << e.what() << "\n";
    }
    try {
        exceptions::raise_exc(true);
    } catch (const gnatpolyglot::ada::exceptions::ProgramError &e) {
        std::cout << "C++ caught Program_Error: " << e.what() << "\n";
    }
    try {
        exceptions::call_raise_exc(d);
    } catch (const exceptions::Exc2 &e) {
        std::cout << "C++ caught Exc2: " << e.what() << "\n";
    }
    try {
        exceptions::raise_constraint();
    } catch (const gnatpolyglot::ada::exceptions::ConstraintError &e) {
        std::cout << "C++ caught ConstraintError: " << e.what() << "\n";
    }
}
