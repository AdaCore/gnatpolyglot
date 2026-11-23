#include <iostream>

#include "test.h"

using gnatpolyglot::memory_owner;
using gnatpolyglot::polyglot_ptr;
using gnatpolyglot::ada::strings::polyglot_string;
using gnatpolyglot::ada::strings::to_string;

class Child : public test::AType {
public:
    Child() : test::AType(this) {}

    polyglot_ptr<polyglot_string> return_string() const override {
        //  This used to be called several times due to poor lock checking
        //  mechanism

        std::cout << "CALLED FROM C++\n";

        // Ownership given to the library (AJIS: setOwner (NATIVE))
        return polyglot_ptr<polyglot_string>(
            new polyglot_string("YO FROM C++"), memory_owner::LIBRARY);
    }
};

int main() {
    test::AType a;
    std::cout << to_string(*test::callback_return_string(a)) << "\n";

    Child c;
    std::cout << to_string(*test::callback_return_string(c)) << "\n";
}
