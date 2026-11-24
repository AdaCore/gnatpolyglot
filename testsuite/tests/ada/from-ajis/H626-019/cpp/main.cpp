#include <iostream>
#include "test.h"

class MyCppChild : public test::T2 {
public:
    MyCppChild() : test::T2(this) {}
    void p() const override {
        std::cout << "P_1 of My_Child\n";
    }
    void p_2() const override {
        std::cout << "P_2 of My_Child\n";
    }
};

int main() {
    MyCppChild child;
    test::call_p(child);
    test::call_p_2(child);
}
