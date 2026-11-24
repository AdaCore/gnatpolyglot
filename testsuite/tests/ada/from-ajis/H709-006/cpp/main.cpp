#include "test.h"
#include <iostream>

class Child : public test::ATaggedRecord {
public:
    Child() : test::ATaggedRecord(this) {}

    void initialize() override {
        //  test::ATaggedRecord::initialize();

        std::cout << "CHILD INITIALIZE" << std::endl;
    }
};

int main() {
    test::ATaggedRecord rec;
    Child rec2_obj;
    test::ATaggedRecord &rec2 = rec2_obj;
    (void)rec2;
}
