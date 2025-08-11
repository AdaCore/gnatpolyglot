#include <iostream>

#include <polyglot_ada_strings.h>
#include "test.h"

using namespace polyglot::ada::strings;

int main() {
    polyglot_string ada_arr = test::string_func();
    test::string_proc(ada_arr);
    std::string ada_str = to_string(ada_arr);
    std::cout << "Ada.String from C++: " << ada_str << std::endl;

    polyglot_string user_arr = test::user_string_func();
    test::user_string_proc(user_arr);
    std::string user_str = to_string(user_arr);
    std::cout << "Test.User_Str from C++: " << user_str << std::endl;

    // We should be able to use functions defined for similar types
    test::user_string_proc(ada_arr);
    test::string_proc(user_arr);

    // Test with a string from C++
    std::string cpp_str = "from C++";
    polyglot_string cpp_arr = from_string(cpp_str);
    test::string_proc(cpp_arr);
    test::user_string_proc(cpp_arr);
}
