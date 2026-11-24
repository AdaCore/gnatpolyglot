#include <iostream>
#include "gnatpolyglot_ptr.h"
#include "test.h"

class ChildRec : public test::Rec {
public:
    int p;

    ChildRec() : test::Rec(this), p(999) {}
};

int main() {
    gnatpolyglot::polyglot_ptr<ChildRec> v1(new ChildRec());

    v1->p = 5;

    auto v2 = test::get_same_rec(true, v1);
    v2 = test::get_same_rec(true, v1);
    v2 = test::get_same_rec(true, v1);
    v2 = test::get_same_rec(true, v1);

    ChildRec *c = dynamic_cast<ChildRec *>(v2.get());
    if (c != nullptr) {
        if (c == v1.get()) {
            std::cout << "references are the same\n";
        }

        std::cout << "extracted P is " << c->p << "\n";
    } else {
        std::cout << "the same object is NO v2\n";
    }
}
