//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot_ada_exceptions.h"
#include "gnatpolyglot.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_exceptions.h"

extern "C" void *
gnatpolyglot__ada__exceptions__create_exception_occurence(int kind);
extern "C" void *
gnatpolyglot__ada__exceptions__create_exception_occurence_message(
    int kind, gnatpolyglot::ada::strings::string_data data);

extern "C" char *gnatpolyglot__ada__exceptions__get_cstr_message(void *data);
extern "C" char *gnatpolyglot__ada__strings_to_c_chars_ptr(
    gnatpolyglot::ada::strings::string_data data);
extern "C" void gnatpolyglot__ada__strings_free_c_chars_ptr(char *);

namespace gnatpolyglot::ada::exceptions {

AdaException::AdaException(const strings::polyglot_string &what)
    : gnatpolyglot::exceptions::polyglot_exception(
          gnatpolyglot__ada__exceptions__create_exception_occurence_message(
              0, what.data_())),
      _what(gnatpolyglot__ada__strings_to_c_chars_ptr(what.data_())) {}
AdaException::AdaException(void *data)
    : gnatpolyglot::exceptions::polyglot_exception(data),
      _what(gnatpolyglot__ada__exceptions__get_cstr_message(data)) {}
AdaException::AdaException(void *data, const strings::polyglot_string &what)
    : gnatpolyglot::exceptions::polyglot_exception(data),
      _what(gnatpolyglot__ada__strings_to_c_chars_ptr(what.data_())) {}
extern "C" void gnatpolyglot__ada__exceptions__free_exception_occurence(void *);
AdaException::~AdaException() {
  gnatpolyglot__ada__exceptions__free_exception_occurence(this->_data);
  gnatpolyglot__ada__strings_free_c_chars_ptr(_what);
}

const char *AdaException::what() const noexcept { return _what; }

ConstraintError::ConstraintError()
    : AdaException(gnatpolyglot__ada__exceptions__create_exception_occurence(
          static_cast<int>(standard_exception_kind::CONSTRAINT_ERROR_KIND))) {}
ConstraintError::ConstraintError(const strings::polyglot_string &what)
    : AdaException(
          gnatpolyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::CONSTRAINT_ERROR_KIND),
              what.data_()),
          what) {}
ConstraintError::ConstraintError(void *data) : AdaException(data) {}
ConstraintError::ConstraintError(void *data,
                                 const strings::polyglot_string &what)
    : AdaException(data, what) {}

ProgramError::ProgramError()
    : AdaException(gnatpolyglot__ada__exceptions__create_exception_occurence(
          static_cast<int>(standard_exception_kind::PROGRAM_ERROR_KIND))) {}
ProgramError::ProgramError(const strings::polyglot_string &what)
    : AdaException(
          gnatpolyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::PROGRAM_ERROR_KIND),
              what.data_()),
          what) {}
ProgramError::ProgramError(void *data) : AdaException(data) {}
ProgramError::ProgramError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

StorageError::StorageError()
    : AdaException(gnatpolyglot__ada__exceptions__create_exception_occurence(
          static_cast<int>(standard_exception_kind::STORAGE_ERROR_KIND))) {}
StorageError::StorageError(const strings::polyglot_string &what)
    : AdaException(
          gnatpolyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::STORAGE_ERROR_KIND),
              what.data_()),
          what) {}
StorageError::StorageError(void *data) : AdaException(data) {}
StorageError::StorageError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

TaskingError::TaskingError()
    : AdaException(gnatpolyglot__ada__exceptions__create_exception_occurence(
          static_cast<int>(standard_exception_kind::TASKING_ERROR_KIND))) {}
TaskingError::TaskingError(const strings::polyglot_string &what)
    : AdaException(
          gnatpolyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::TASKING_ERROR_KIND),
              what.data_()),
          what) {}
TaskingError::TaskingError(void *data) : AdaException(data) {}
TaskingError::TaskingError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

void rethrow_standard_exception() {
  kernel *k = gnatpolyglot_get_kernel();
  void *data = k->exc_info.exception_data;
  char *message = k->exc_info.message;
  int kind = k->exc_info.exception_kind;
  k->exc_info.exception_data = nullptr;
  try {
    switch (kind) {
    case static_cast<int>(standard_exception_kind::CONSTRAINT_ERROR_KIND):
      throw ConstraintError(data, message);
    case static_cast<int>(standard_exception_kind::PROGRAM_ERROR_KIND):
      throw ProgramError(data, message);
    case static_cast<int>(standard_exception_kind::STORAGE_ERROR_KIND):
      throw StorageError(data, message);
    case static_cast<int>(standard_exception_kind::TASKING_ERROR_KIND):
      throw TaskingError(data, message);
    }
  } catch (const std::exception &) {
    k->exc_info.clear_exception(k);
    throw;
  }
}

} // namespace gnatpolyglot::ada::exceptions
