#include "test.h"
#include "base.h"

class CppChild : public test::AType {
public:
    CppChild() : test::AType(this) {}
    base::AReturnedType get_something(const base::AReturnedType& x) const override {
        return x;
    }
};

int main() {
    CppChild child;
    test::AType &t = child;
    t.get_something(base::AReturnedType());

    //  This doesn't actually do anything, but should work without
    //  compilation errors.
}
