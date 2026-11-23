#include "test.h"
#include <iostream>

class RecChild : public test::ARecord {
public:
    int f = 999;

    RecChild() : test::ARecord(this) {}

    void proc() override {
        test::ARecord::proc();
        f = 1024;
    }
};

int main() {
    gnatpolyglot::polyglot_ptr<test::ARecord> r1 = test::get_obj();
    test::ARecord r2;
    RecChild r3_obj;
    test::ARecord &r3 = r3_obj;

    test::call_proc(*r1);
    test::call_proc(r2);
    test::call_proc(r3);

    std::cout << r1->get_a_value() << "\n";
    std::cout << r2.get_a_value() << "\n";
    std::cout << r3.get_a_value() << "\n";
    std::cout << static_cast<RecChild &>(r3).f << "\n";
    return 0;
}
