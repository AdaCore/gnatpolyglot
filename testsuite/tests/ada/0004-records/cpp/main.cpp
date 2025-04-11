#include <iostream>

#include "test.h"

int main() {
  // Get a Pair from a free function
  test::Pair p = test::init_pair(3, 4);
  p.print();
  test::Value v = p.get_v_1();

  std::cout << "p.v_1.v = " << v.get_v() << "\n";

  v.set_v(-1);

  // the value of "p.v_1.v" should stay the same: the record has been copied.
  std::cout << "p.v_1.v = " << p.get_v_1().get_v() << "\n";

  p.set_v_1(v);
  p.print();
  v.set_v(-2);
  p.set_v_2(v);
  p.print();
}
