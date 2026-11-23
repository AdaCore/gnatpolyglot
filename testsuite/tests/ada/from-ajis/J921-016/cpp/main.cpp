#include "test.h"

// C++ transposition of AJIS J921-016: the Java anonymous subclass of R
// becomes a C++ subclass overriding Initialize.

class RChild : public test::R {
public:
    RChild() : test::R(this) {}

    void initialize(test::T &v, test::E &) const override {
        v.set_v(789);
    }
};

int main() {
    test::make_calls(test::R());
    test::make_calls(RChild());
    return 0;
}
