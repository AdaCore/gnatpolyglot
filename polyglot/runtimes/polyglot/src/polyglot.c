//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "polyglot.h"

struct kernel *polyglot_get_kernel() {
    static struct kernel k = { 0 };
    return &k;
}
