#include <iostream>

#include "pkg.h"

using gnatpolyglot::ada::arrays::polyglot_array;
namespace ps = gnatpolyglot::ada::strings;

// An override returning the record it received
class Child : public pkg::T {
public:
    Child() : pkg::T(this) {}

    pkg::Rec echo(const pkg::Rec &r) const override {
        return r;
    }
};

// An override returning a record that remains in use after the call
class Child_Field : public pkg::T {
public:
    pkg::Rec field;

    Child_Field() : pkg::T(this), field(6) {}

    pkg::Rec echo(const pkg::Rec &) const override {
        return field;
    }
};

int main() {
    // A callback returning the record it received
    pkg::call([](const pkg::Rec &r) { return r; });

    Child c;
    pkg::call_echo(c);

    // A callback returning a record that remains in use after the call
    pkg::Rec outer(5);
    pkg::call([&outer](const pkg::Rec &) { return outer; });
    std::cout << "after callback: " << outer.get_i() << "\n";

    Child_Field c_field;
    pkg::call_echo(c_field);
    std::cout << "after override: " << c_field.field.get_i() << "\n";

    // The same for arrays: callbacks returning the array they received,
    // then an array that remains in use after the call.
    pkg::call_int_array([](const polyglot_array<int32_t> &a) { return a; });
    polyglot_array<int32_t> outer_array(1, 1);
    outer_array.set(1, 7);
    pkg::call_int_array(
        [&outer_array](const polyglot_array<int32_t> &) { return outer_array; });
    std::cout << "after callback: " << outer_array.get(1) << "\n";

    pkg::call_rec_array([](const polyglot_array<pkg::Rec> &a) { return a; });
    polyglot_array<pkg::Rec> outer_rec_array(1, 1);
    outer_rec_array.set(1, pkg::Rec(8));
    pkg::call_rec_array([&outer_rec_array](const polyglot_array<pkg::Rec> &) {
        return outer_rec_array;
    });
    std::cout << "after callback: " << outer_rec_array.get(1)->get_i() << "\n";

    // The same for strings: a callback returning the string it received,
    // then a string that remains in use after the call.
    pkg::call_string([](const ps::polyglot_string &s) { return s; });
    ps::polyglot_string outer_string = ps::from_string("B");
    pkg::call_string(
        [&outer_string](const ps::polyglot_string &) { return outer_string; });
    std::cout << "after callback: " << ps::to_string(outer_string) << "\n";
    return 0;
}
