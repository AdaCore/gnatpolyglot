#include <iomanip>
#include <ios>
#include <iostream>

#include "floats.h"

class Child : public floats::T {
public:
    Child() : floats::T(this) {}

    float inc(float f) const override {
        return f + 2;
    }
};

int main() {
    floats::T obj;
    Child c;

    std::cout << floats::call_inc(obj, 2.5) << "\n";
    std::cout << floats::call_inc(c, 2.5) << "\n";
}
