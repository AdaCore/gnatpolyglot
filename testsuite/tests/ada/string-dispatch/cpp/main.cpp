#include <iomanip>
#include <ios>
#include <iostream>

#include "gnatpolyglot_ada_strings.h"
#include "test.h"

using namespace gnatpolyglot::ada::strings;

class Child : public test::T {
public:
  Child() : test::T(this) {}

  ::gnatpolyglot::ada::strings::polyglot_string
  concat(const gnatpolyglot::ada::strings::polyglot_string &a,
         const gnatpolyglot::ada::strings::polyglot_string &b) const override {
    return from_string(to_string(a) + to_string(b) + to_string(a));
  }
};

int main() {
  test::T obj;
  Child c;

  std::cout << to_string(test::call_concat(obj, "foo", "bar")) << "\n";
  std::cout << to_string(test::call_concat(c, "bar", "baz")) << "\n";
}
