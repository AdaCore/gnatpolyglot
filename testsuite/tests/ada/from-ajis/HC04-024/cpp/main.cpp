#include "test.h"
#include <iostream>

// Strange, Strange2, Strange3, Strange4 all take P_Acc* (access-to-procedure) — not bound.
// AJIS NativeException ownership checks are not applicable in gnatpolyglot.

int main() {
    test::strange([](test::RecAcc& v) {
        std::cout << "[CPP - 1] " << (v ? "not null" : "null") << "\n";
        v = nullptr;
    });
    test::strange2([](test::Rec2Acc& v) {
        std::cout << "[CPP - 2] " << (v ? "not null" : "null") << "\n";
        v = nullptr;
    });
    test::strange3([](test::Rec3Acc& v) {
        std::cout << "[CPP - 3] " << (v ? "not null" : "null") << "\n";
        v = new test::Rec3();
    });
    test::strange4([](test::Rec4Acc& v) {
        std::cout << "[CPP - 4] " << (v ? "not null" : "null") << "\n";
        v = new test::Rec4();
    });
    test::strange3([](test::Rec3Acc& v) {
        std::cout << "[CPP - 3] " << (v ? "not null" : "null") << "\n";
        v = new test::Rec3();
    });
    test::strange4([](test::Rec4Acc& v) {
        std::cout << "[CPP - 4] " << (v ? "not null" : "null") << "\n";
        v = new test::Rec4();
    });
    return 0;
}
