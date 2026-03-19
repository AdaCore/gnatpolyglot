//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef POLYGLOT_ADA_EXCEPTIONS_H
#define POLYGLOT_ADA_EXCEPTIONS_H

#include "polyglot_ada_strings.h"
#include "polyglot_exceptions.h"

namespace polyglot::ada::exceptions {

enum class standard_exception_kind {
    CONSTRAINT_ERROR_KIND = -4,
    PROGRAM_ERROR_KIND = -3,
    STORAGE_ERROR_KIND = -2,
    TASKING_ERROR_KIND = -1
};

class AdaException : public polyglot::exceptions::polyglot_exception {
public:
    AdaException(const strings::polyglot_string &what);
    AdaException(void *data);
    AdaException(void *data, const strings::polyglot_string &what);
    ~AdaException();

    const char *what() const noexcept override;

private:
    char *_what;
};

class ConstraintError : public AdaException {
public:
    ConstraintError();
    ConstraintError(const strings::polyglot_string &what);
    ConstraintError(void *data);
    ConstraintError(void *data, const strings::polyglot_string &what);
};

class ProgramError : public AdaException {
public:
    ProgramError();
    ProgramError(const strings::polyglot_string &what);
    ProgramError(void *data);
    ProgramError(void *data, const strings::polyglot_string &what);
};

class StorageError : public AdaException {
public:
    StorageError();
    StorageError(const strings::polyglot_string &what);
    StorageError(void *data);
    StorageError(void *data, const strings::polyglot_string &what);
};

class TaskingError : public AdaException {
public:
    TaskingError();
    TaskingError(const strings::polyglot_string &what);
    TaskingError(void *data);
    TaskingError(void *data, const strings::polyglot_string &what);
};

void rethrow_standard_exception();

} // namespace polyglot::ada::exceptions

#endif /* ! POLYGLOT_ADA_EXCEPTIONS_H */
