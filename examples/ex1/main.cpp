#include <iostream>

#include "2cpp/include/animals.h"
#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ptr.h"

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::arrays;

class Dog : public animals::Animal {
public:
    // Use the shadow object ctor (takes an extra `this` argument)
    Dog() : animals::Animal(this) {}

    void shout() const override {
        std::cout << "Woof!\n";
    }
};

int main() {
    animals::Parrot p(animals::Color::BLUE);
    // `Shout` and `Repeat` are bound as as member functions
    p.shout();
    p.repeat(gnatpolyglot::ada::strings::from_string("Ada"));
    p.repeat("C++"); // the implicit constructor also exists.

    std::cout << "p is " 
              << gnatpolyglot::ada::strings::to_string(animals::image(p.get_c()))
              << "\n";

    Dog d;
    d.shout();

    animals::call_shout(d);

    polyglot_array<polyglot_ptr<animals::Parrot>> flock(1, 2);
    polyglot_ptr<animals::Parrot> p_ptr(p);
    polyglot_ptr<animals::Parrot> other_parrot
        (new animals::Parrot(animals::Color::GREEN),
         gnatpolyglot::memory_owner::LIBRARY);
    flock.set(1, p_ptr);
    flock.set(2, other_parrot);
    animals::shout(flock);

    // Claim ownership in order to free memory
    other_parrot.set_owner(gnatpolyglot::memory_owner::USER);
}
