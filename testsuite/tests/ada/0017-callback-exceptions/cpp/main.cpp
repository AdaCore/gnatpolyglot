
#include <cstddef>
#include <cstdint>
#include <iostream>

#include "test.h"

void foo() {
  throw gnatpolyglot::ada::exceptions::ProgramError("bar");
}

int main() {
  try {
    test::get_callback()();
  } catch (const gnatpolyglot::ada::exceptions::ProgramError &e) {
    std::cout << "C++ caught : " << e.what() << "\n";
  }
  try {
    test::call(foo);
  } catch (const gnatpolyglot::ada::exceptions::ProgramError &e) {
    std::cout << "C++ caught : " << e.what() << "\n";
  }
}
