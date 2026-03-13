//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot.h"

#if __STDC_VERSION__ < 202311L
    #if __STDC_VERSION__ >= 201112L
        #include <threads.h>
    #elif defined(__GNUC__) || defined(__clang__)
        #define thread_local __thread
    #elif defined(_MSC_VER)
        #define thread_local __declspec( thread )
    #endif
#endif

struct kernel *gnatpolyglot_get_kernel() {
    thread_local static struct kernel k = { 0 };
    return &k;
}
