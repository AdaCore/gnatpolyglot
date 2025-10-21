#include "polyglot_ada_strings.h"
#include "test.h"

int main() {
    ada::calendar::Time time = test::get_time();
    ada::strings::unbounded::UnboundedString s;
    test::append(s, time);
    std::cout << polyglot::ada::strings::to_string(test::to_string(s)) << "\n";
}
