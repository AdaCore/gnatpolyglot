#include "test.h"

int main() {
    test::get_procedure()(10);
    test::call_a_procedure(11, test::get_procedure());
}
