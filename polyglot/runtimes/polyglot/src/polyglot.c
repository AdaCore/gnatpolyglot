#include "polyglot.h"

struct kernel *polyglot_get_kernel() {
    static struct kernel k = { 0 };
    return &k;
}
