#include "test.h"
#include <type_traits>

static_assert(!std::is_trivially_constructible<test::Value>::value,
              "Value is trivially constructible");

int main() {
  test::Value v = test::init_value(3, 4);
  v.p();
  v.p();
}
