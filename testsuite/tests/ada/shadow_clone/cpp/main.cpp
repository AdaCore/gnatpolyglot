#include <iostream>

#include "test.h"

class Type1Cpp : public test::Type1 {
public:
    Type1Cpp() : test::Type1(this) {}
    Type1Cpp(const Type1Cpp &, gnatpolyglot::data *data)
        : test::Type1(data) {}

    Type1 *internal_clone_(gnatpolyglot::data *data) override {
        return new Type1Cpp(*this, data);
    }

    int i = 1;
};

class Type2Cpp : public test::Type2 {
public:
    Type2Cpp() : test::Type2(this) {}

    gnatpolyglot::polyglot_ptr<::test::Type1> get() const override {
        return {new Type1Cpp(), gnatpolyglot::memory_owner::USER};
    }
};

class Type2CppNull : public test::Type2 {
public:
    Type2CppNull() : test::Type2(this) {}

    gnatpolyglot::polyglot_ptr<::test::Type1> get() const override {
        return nullptr;
    }
};

int main() {
    test::call_get(Type2Cpp{});
    std::cout << "end of main\n";
}
