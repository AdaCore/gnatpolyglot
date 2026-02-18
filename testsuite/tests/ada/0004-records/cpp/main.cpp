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
  std::cout << "v.v = " << v.get_v() << "\n";
  std::cout << "p.v_1.v = " << p.get_v_1()->get_v() << "\n";

  p.get_v_1()->set_v(5);
  p.get_v_2()->set_v(6);
  p.print();

  test::Value::view val_view_1 = p.get_v_1();
  val_view_1->set_v(7);
  test::Value::view val_view_2 = p.get_v_2();
  val_view_2->set_v(8);
  p.print();

  // Test the constructor
  test::Pair ctor(test::Value(4), test::Value(2));
  ctor.print();

  test::PairBis bis(test::Value(0), test::Value(0));
  bis.get_v_1()->set_v(2);
  bis.get_v_2()->set_v(4);
  bis.print_bis();
  bis.print();

  test::HalfDefault h1(test::Value(2), 3);
  test::HalfDefault h2(3);
}
