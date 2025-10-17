#include "test.h"
#include "test_child.h"
#include "binded.h"

int main() {
    test::child::T t;
    binded::p(t);
}
