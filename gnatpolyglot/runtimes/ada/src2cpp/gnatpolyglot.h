//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef GNATPOLYGLOT_H
#define GNATPOLYGLOT_H

extern "C" {

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

struct kernel *gnatpolyglot_get_kernel();

}

#endif /* ! GNATPOLYGLOT_H */
