#include "vfs.h"
#include "ref.h"
#include "gnatpolyglot_ada_arrays.h"
#include <iostream>

using namespace gnatpolyglot::ada::arrays;

int main() {
    // File_Array = array (Positive range <>) of Virtual_File
    // Global X : File_Array (1 .. 2) in Ref
    auto arr = ref::get_x();
    vfs::VirtualFile vf;
    arr->set(1, vf);
    arr->get(1)->prim(*arr);
    std::cout << "NO EXCEPTION\n";
    return 0;
}
