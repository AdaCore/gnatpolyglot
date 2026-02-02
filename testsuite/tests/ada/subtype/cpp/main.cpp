#include <iostream>

#include "test.h"
#include "tagged_type.h"

int main() {
  test::A v(1);
  test::A x = v.identity();
  v.set_v(2);
  std::cout << v.get_v() << " " << x.get_v() << "\n";

  test::foo(tagged_type::T{});
}
