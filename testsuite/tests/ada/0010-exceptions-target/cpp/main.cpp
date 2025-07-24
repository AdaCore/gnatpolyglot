#include <iostream>

#include "test.h"
#include "polyglot_ada_exceptions.h"

class Deriv : public test::Tag {
public:
    Deriv(int i) : test::Tag(i, this, &vtable) {}

    void raise_exc() const {
        switch (this->get_i()) {
            case 1:
                throw test::Exc1("Foo");
            case 2:
                throw test::Exc2("Bar");
            case 3:
                throw polyglot::ada::exceptions::ConstraintError("Baz");
            case 4:
                throw polyglot::ada::exceptions::ProgramError("FooBar");
        }
    }

private:
    static test::Tag::vtable vtable;
};

test::Tag::vtable
Deriv::vtable (static_cast<void (test::Tag::*)() const>(&Deriv::raise_exc));

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
    } catch (const polyglot::ada::exceptions::ConstraintError &e) {
        std::cout << "C++ caught Constraint_Error: " << e.what() << "\n";
    }
    try {
        d.set_i(4);
        test::get_exception(d);
    } catch (const polyglot::ada::exceptions::ProgramError &e) {
        std::cout << "C++ caught Program_Error: " << e.what() << "\n";
    }
}
