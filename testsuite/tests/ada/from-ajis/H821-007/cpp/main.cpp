#include "test.h"
#include <iostream>

// Subclass of A_Tagged_Type to exercise virtual dispatch
class CppTaggedType : public test::ATaggedType {
public:
    CppTaggedType() : test::ATaggedType(this) {}
};

// Callback for tagged type (A_Tagged_Callback not bound — access to subprograms not yet supported)
static void tagged_callback(const test::ATaggedType &v) {
    (void)v;
    std::cout << "tagged callback called" << std::endl;
}

// Callback for non-tagged type (A_Non_Tagged_Callback not bound)
static void non_tagged_callback(const test::ANonTaggedType &v) {
    (void)v;
    std::cout << "non-tagged callback called" << std::endl;
}

int main() {
    // Get a non-tagged object (bound)
    test::ANonTaggedType obj = test::get_non_tagged_object();
    (void)obj;
    std::cout << "got non-tagged object" << std::endl;

    // Set and get tagged object (Set_Tagged_Object not bound: uses A_Tagged_Access)
    // Get_Tagged_Object not bound: returns class-wide
    CppTaggedType t;
    test::set_tagged_object(&t);
    test::ATaggedType got = test::get_tagged_object();
    (void)got;

    // Call callbacks (not bound: access-to-subprogram params)
    test::ATaggedCallback cb1 = &tagged_callback;
    CppTaggedType y;
    test::call_tagged_callback(cb1, y);

    test::ANonTaggedCallback cb2 = &non_tagged_callback;
    test::ANonTaggedType y2;
    test::call_non_tagged_type_callback(cb2, y2);

    return 0;
}
