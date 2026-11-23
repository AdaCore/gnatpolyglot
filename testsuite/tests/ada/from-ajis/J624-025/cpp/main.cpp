#include "test.h"
#include <iostream>

class ConcreteRoot : public test::Root {
    ConcreteRoot(gnatpolyglot::data *d) : test::Root(d) {}
public:
    ConcreteRoot() {}
    test::Root *internal_clone_(gnatpolyglot::data *d) override { return new ConcreteRoot(d); }
};

class ConcreteOtherRoot : public test::OtherRoot {
    ConcreteOtherRoot(gnatpolyglot::data *d) : test::OtherRoot(d) {}
public:
    ConcreteOtherRoot() {}
    test::OtherRoot *internal_clone_(gnatpolyglot::data *d) override { return new ConcreteOtherRoot(d); }
};

int main() {
    ConcreteRoot r;
    ConcreteOtherRoot o;
    // P not bound: anonymous access Other_Root'Class parameter not yet supported.
    r.p(gnatpolyglot::polyglot_ptr<test::OtherRoot>(o));
    std::cout << "OK\n";
    return 0;
}
