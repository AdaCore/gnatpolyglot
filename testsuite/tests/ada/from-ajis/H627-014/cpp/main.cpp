#include "test.h"
#include <iostream>
#include <memory>

class CppChild : public test::AType {
public:
    ~CppChild() override {
        std::cout << "FINALIZE FROM C++" << std::endl;
    }
};

int main() {
    // Scope exit replaces Java GC, with deterministic destruction order.
    std::cout << "Destroying AType..." << std::endl;
    {
        test::AType a;
    }
    std::cout << "AType destroyed." << std::endl;

    std::cout << "Destroying CppChild..." << std::endl;
    {
        // Destroy through the generated base to exercise virtual destruction.
        std::unique_ptr<test::AType> child(new CppChild());
    }
    std::cout << "CppChild destroyed." << std::endl;
    return 0;
}
