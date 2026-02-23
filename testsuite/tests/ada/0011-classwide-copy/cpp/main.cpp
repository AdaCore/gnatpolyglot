#include "test.h"
#include <vector>
#include <iostream>

class Inherited : public test::Root {
protected:
    Inherited *internal_clone_(void *data) override {
        return new Inherited(*this, data);
    }

    Inherited(const Inherited& other, void *data)
        // Call the parent ctor: nothing to copy there, only use the new data.
        : test::Root(data) 
    {
        std::cout << "C++ copyctor\n";
        vec = other.vec;
    }

public:
    Inherited() : test::Root(1, 2, this) {}
    Inherited(const Inherited& other) : test::Root(other) {}

    // Points to heap memory which requires CLONING when copied: cannot do shallow copies.
    std::vector<int> vec = { 1, 2, 3};

    void p() const override {
        std::cout << "{ ";
        for (auto e : vec) {
            std::cout << e << ", ";
        }
        std::cout << "}\n";
        test::Root::p();
    }

    void p2() override {
        this->Root::p2();
        for (auto &e : vec) {
            e *= 2;
        }
    }
};

int main() {
    Inherited in{};
    in.p();
    test::p_make_copy(in);
}
