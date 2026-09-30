#include <iostream>
#include "test.h"

int main() {
     auto v = test::get_object_1();
     bool is_child = dynamic_cast<test::Child *>(v.get()) != nullptr;
     bool is_gchild = dynamic_cast<test::GChild *>(v.get()) != nullptr;
     std::cout << (is_child ? "true" : "false") << "\n";
     std::cout << (is_gchild ? "true" : "false") << "\n";

     auto v2 = test::get_object_2();
     is_child = dynamic_cast<test::GChild *>(v2.get()) != nullptr;
     std::cout << (is_child ? "true" : "false") << "\n";
}
