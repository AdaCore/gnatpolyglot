#include <iostream>

#include "../2cpp/include/example.h"
#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ptr.h"

void escape_acc() {
    gnatpolyglot::polyglot_ptr<example::P> acc(new example::P);
    acc.set_owner(gnatpolyglot::memory_owner::LIBRARY);
    example::set_acc(acc);
    example::print();
    // Acc's owner is LIBRARY: The C++ object will be freed, but not the ada
    // record.
}

int main() {
    escape_acc();
    example::print();
    gnatpolyglot::polyglot_ptr<example::P> acc = example::get_acc();
    example::unchecked_free(acc);

    example::set_acc(nullptr);
    example::print();

    escape_acc();
    example::print();
    acc = example::get_acc();
    acc.set_owner(gnatpolyglot::memory_owner::USER);
    example::set_acc(nullptr);
}
