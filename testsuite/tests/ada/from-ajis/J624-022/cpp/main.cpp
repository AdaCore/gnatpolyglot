#include <iostream>

#include "test.h"
#include "test_cont.h"

class Child : public test::Root {
public:
    Child() : test::Root(this) {}

    // Ada copies the Root'Class value given to Set: Child objects must be
    // clonable from Ada.
    Child(const Child &other, gnatpolyglot::data *data)
        : test::Root(data), x(other.x) {}

    Root *internal_clone_(gnatpolyglot::data *data) override {
        return new Child(*this, data);
    }

    int x = 0;
};

int main() {
    Child c;
    c.x = 50;

    test::cont::R v;

    v.set(c);

    std::cout << dynamic_cast<Child &>(*v.get()).x << std::endl;
    return 0;
}
