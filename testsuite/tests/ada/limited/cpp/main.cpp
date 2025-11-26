#include "test.h"
#include <iostream>
#include <type_traits>

static_assert(!std::is_copy_constructible<test::Value>::value,
              "Value is copy constructible");
static_assert(!std::is_copy_assignable<test::Value>::value,
              "Value is copy assignable");

static_assert(!std::is_copy_constructible<test::TaggedValue>::value,
              "TaggedValue is copy constructible");
static_assert(!std::is_copy_assignable<test::TaggedValue>::value,
              "TaggedValue is copy assignable");

class Child : public test::TaggedValue {
public:
  Child() : test::TaggedValue(this) {}

  void p() override {
    test::TaggedValue::p();
    std::cout << (i += 2) << "\n";
  }

  int i = 4;
};

int main() {
  test::Value v = test::init_value(3, 4);
  v.p();
  v.p();

  test::TaggedValue x;
  x.p();
  x.p();

  Child y;
  y.p();
}
