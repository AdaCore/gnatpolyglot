#include "test.h"
#include <type_traits>

static_assert(!std::is_copy_constructible<test::Value>::value,
              "Value is copy constructible");
static_assert(!std::is_copy_assignable<test::Value>::value,
              "Value is copy assignable");

int main() {
  test::Value v = test::init_value(3, 4);
  v.p();
  v.p();
}
