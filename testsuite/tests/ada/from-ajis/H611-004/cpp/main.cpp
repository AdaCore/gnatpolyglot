#include <iostream>
#include <string>
#include "p.h"
#include <gnatpolyglot_ada_exceptions.h>

// Stand-in for Java's e.getClass(): name the exception class, if known.
static std::string class_of(const std::exception &e) {
    if (dynamic_cast<const p::AnException *>(&e))
        return "p::AnException";
    if (dynamic_cast<const gnatpolyglot::ada::exceptions::ConstraintError *>(&e))
        return "gnatpolyglot::ada::exceptions::ConstraintError";
    return "unknown";
}

int main() {
    try {
        p::raise_an_exception();
    } catch (const p::AnException &e) {
        std::cout << "An_Exception caught from Java (1): [" << e.what() << "]\n";
    }

    try {
        p::call_the_callback([]() {
            throw p::AnException(
                gnatpolyglot::ada::strings::polyglot_string(
                    "This is a message from Java"));
        });
    } catch (const std::exception &e) {
        std::cout << "exception caught from Java (2): [" << class_of(e)
                  << ", " << e.what() << "]\n";
    }

    try {
        p::raise_constraint_error();
    } catch (const std::exception &e) {
        std::cout << "An_Exception caught from Java (3): [" << class_of(e)
                  << ", " << e.what() << "]\n";
    }
}
