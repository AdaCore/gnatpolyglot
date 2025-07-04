#include <iostream>

#include "test.h"

void test_native_arrays() {

  std::cout << "Starting test_native_arrays\n";

  polyglot::ada::arrays::polyglot_array<int32_t> arr = test::f_u_1();

  std::cout << "bounds:" << arr.get_begin() << " " << arr.get_end() << "\n";
  std::cout << "content : [" << arr.get(1) << " " << arr.get(2) << " "
            << arr.get(3) << "]\n";

  std::cout << "total: " << test::f_u_2(arr) << "\n";

  std::cout << "setting new value\n";
  arr.set(1, 4);
  arr.set(2, 5);
  arr.set(3, 6);

  std::cout << "content : [" << arr.get(1) << " " << arr.get(2) << " "
            << arr.get(3) << "]\n";
  std::cout << "total: " << test::f_u_2(arr) << "\n";

  std::cout << "==================\narr: ";
  for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
    std::cout << arr.get(i) << ", ";
  }
  std::cout << "\n";

  std::cout << "Calling out array param procedure...\n";
  test::out_proc(arr);;;

  std::cout << "arr: ";
  for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
    std::cout << arr.get(i) << ", ";
  }
  std::cout << "\nDone.\n============================================\n";
}

void test_struct_arrays() {
  std::cout << "Starting test_struct_arrays\n";
  polyglot::ada::arrays::polyglot_array<test::MyInt> arr = test::my_int_arr_func();
  std::cout << "bounds:" << arr.get_begin() << " " << arr.get_end() << "\n";
  std::cout << "==================\narr: ";
  for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
    std::cout << arr.get(i)->get_i() << ", ";
  }
  std::cout << "\n";

  std::cout << "Calling out array param procedure...\n";
  test::my_int_arr_proc(arr);

  std::cout << "arr: ";
  for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
    std::cout << arr.get(i)->get_i() << ", ";
  }
  std::cout << "\n";

  test::print_image(arr);

  std::cout << "\nDone.\n============================================\n";
}

int main() {
    test_native_arrays();
    test_struct_arrays();
}
