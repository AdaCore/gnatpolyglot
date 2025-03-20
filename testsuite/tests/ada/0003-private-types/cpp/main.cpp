#include <iostream>

#include "test.h"

test::Pair t(test::Pair p) {
  p.print();
  // Initialize the pair using the default allocation function
  test::Pair p2;
  p2.print();
  // Verify that a class passed by argument works
  p.add_pairs(test::init_pair(4, 3)).print();

  return p;
}

int main() {
  // Get a Pair from a free function
  test::Pair p = test::init_pair(3, 4);
  std::cout << p.left() << "\n";
  std::cout << p.sum() << "\n";
  std::cout.flush();
  // The value should be copied, and the copy freed by the callee.
  t(p);
  // `p` should still be allocated.
  p.print();
}
