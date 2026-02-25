//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef POLYGLOT_H
#define POLYGLOT_H

#ifdef __cplusplus

#include <exception>

extern "C" {
#endif /* __cplusplus */

struct kernel;

struct exception_information {
    int exception_kind;
    char *message;
    void *exception_data;
    void (*clear_exception)(struct kernel *k);
} ;

struct kernel {
    struct exception_information exc_info;
};

struct kernel *polyglot_get_kernel();

#ifdef __cplusplus
}
#endif

#endif /* ! POLYGLOT_H */
