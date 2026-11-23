#include <iostream>

#include "test.h"

class Child : public test::Bla {
public:
    Child() : test::Bla(this) {}

    void prim(int32_t param1, const test::SomeRecord &) const override {
        std::cout << "GOT FROM JAVA : " << param1 << std::endl;
    }
};

int main() {
    test::Bla x;
    Child y;
    test::SomeRecord rec(10);
    test::call_prim(x, 1, rec);
    test::call_prim(y, 2, rec);
    return 0;
}
