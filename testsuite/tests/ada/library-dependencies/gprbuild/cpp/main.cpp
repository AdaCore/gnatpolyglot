#include "lib_1.h"
#include "lib_2.h"
#include "lib_3.h"

int main() {
    lib_1::T t(3);
    lib_1::foo();
    lib_2::bar();
    lib_3::bar(t);
}
