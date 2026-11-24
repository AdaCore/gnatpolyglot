#include <iostream>

#include "test.h"

class Child : public test::ATaggedRecord {
public:
    Child(int32_t i) : test::ATaggedRecord(i, this) {}
};

int main() {
    test::ATaggedRecord t1(30);
    Child t2(40);
    test::ATaggedRecord &t2_ref = t2;
    std::cout << t1.get_i() << ", " << t2_ref.get_i() << std::endl;
    return 0;
}
