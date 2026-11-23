#include "test.h"
int main() {
    // F and F_2 are not testable: proxy compilation fails with
    // "incompatible arguments for operator" — a proxy generation bug
    // involving the "=" operator for String access subtypes.
}
