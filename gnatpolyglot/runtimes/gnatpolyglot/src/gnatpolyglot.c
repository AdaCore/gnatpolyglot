//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot.h"

#if __STDC_VERSION__ < 202311L
    #if defined(__GNUC__) || defined(__clang__)
        #define thread_local __thread
    #elif defined(_MSC_VER)
        #define thread_local __declspec( thread )
    #elif __STDC_VERSION__ >= 201112L
        #include <threads.h>
    #else
        #error "thread_local not supported by this compiler"
    #endif
#endif

struct kernel *gnatpolyglot_get_kernel() {
    static thread_local struct kernel k = { 0 };
    return &k;
}
