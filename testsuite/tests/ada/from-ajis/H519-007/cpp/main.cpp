#include "test.h"
int main() {
    test::MyType obj;
    obj.set_fields(10, 199.9f);
    obj.display_my_type();

    gnatpolyglot::polyglot_ptr<test::MyType> ptr(obj);
    test::set_fields_3(ptr, 30, 399.9f);
    test::display_my_type_3(ptr);
}
