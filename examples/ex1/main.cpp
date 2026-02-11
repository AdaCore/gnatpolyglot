#include <iostream>

#include "2cpp/include/animals.h"
#include "polyglot_ada_arrays.h"
#include "polyglot_ada_strings.h"
#include "polyglot_ptr.h"

using namespace polyglot;
using namespace polyglot::ada::arrays;

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
    p.repeat(polyglot::ada::strings::from_string("Ada"));
    p.repeat("C++"); // the implicit constructor also exists.

    std::cout << "p is " 
              << polyglot::ada::strings::to_string(animals::image(p.get_c()))
              << "\n";

    Dog d;
    d.shout();

    animals::call_shout(d);

    polyglot_array<polyglot_ptr<animals::Parrot>> flock(1, 2);
    polyglot_ptr<animals::Parrot> p_ptr(p);
    polyglot_ptr<animals::Parrot> other_parrot
        (new animals::Parrot(animals::Color::GREEN),
         polyglot::memory_owner::LIBRARY);
    flock.set(1, p_ptr);
    flock.set(2, other_parrot);
    animals::shout(flock);

    // Claim ownership in order to free memory
    other_parrot.set_owner(polyglot::memory_owner::USER);
}
