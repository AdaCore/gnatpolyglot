#include <iostream>

#include "hello.h"

class Buffer : public hello::EditorBuffer {
public:
    Buffer() : hello::EditorBuffer(this) {}

    int32_t f1() const override {
        return 1;
    }
};

class My_T2 : public hello::T2 {
public:
    My_T2() : hello::T2(this) {}
};

int main() {
    hello::T1 t1;
    Buffer buffer;
    My_T2 t2;
    std::cout << buffer.f1() << " " << t1.f4() << " " << t2.f7() << std::endl;
    return 0;
}
