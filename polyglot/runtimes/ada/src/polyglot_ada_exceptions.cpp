#include "polyglot_ada_exceptions.h"
#include "polyglot_ada_strings.h"
#include "polyglot_exceptions.h"
#include "polyglot.h"

extern "C" void *
polyglot__ada__exceptions__create_exception_occurence(int kind);
extern "C" void *polyglot__ada__exceptions__create_exception_occurence_message(
    int kind, polyglot::ada::strings::string_data data);

namespace polyglot::ada::exceptions {

AdaException::AdaException(const strings::polyglot_string &what)
    : polyglot::exceptions::polyglot_exception(
          polyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::CONSTRAINT_ERROR_KIND),
              what.data_()),
          strings::to_string(what)) {}
AdaException::AdaException(void *data)
    : polyglot::exceptions::polyglot_exception(data) {}
AdaException::AdaException(void *data, const strings::polyglot_string &what)
    : polyglot::exceptions::polyglot_exception(
          data, strings::to_string(what)) {}
extern "C" void polyglot__ada__exceptions__free_exception_occurence(void *);
AdaException::~AdaException() {
    polyglot__ada__exceptions__free_exception_occurence(this->_data);
}

ConstraintError::ConstraintError()
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence(
              static_cast<int>(
                  standard_exception_kind::CONSTRAINT_ERROR_KIND))) {}
ConstraintError::ConstraintError(const strings::polyglot_string &what)
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::CONSTRAINT_ERROR_KIND),
              what.data_()),
          what) {}
ConstraintError::ConstraintError(void *data)
    : AdaException(data) {}
ConstraintError::ConstraintError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

ProgramError::ProgramError()
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence(
              static_cast<int>(standard_exception_kind::PROGRAM_ERROR_KIND))) {}
ProgramError::ProgramError(const strings::polyglot_string &what)
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::PROGRAM_ERROR_KIND),
              what.data_()),
          what) {}
ProgramError::ProgramError(void *data)
    : AdaException(data) {}
ProgramError::ProgramError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

StorageError::StorageError()
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence(
              static_cast<int>(standard_exception_kind::STORAGE_ERROR_KIND))) {}
StorageError::StorageError(const strings::polyglot_string &what)
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::STORAGE_ERROR_KIND),
              what.data_()),
          what) {}
StorageError::StorageError(void *data)
    : AdaException(data) {}
StorageError::StorageError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

TaskingError::TaskingError()
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence(
              static_cast<int>(standard_exception_kind::TASKING_ERROR_KIND))) {}
TaskingError::TaskingError(const strings::polyglot_string &what)
    : AdaException(
          polyglot__ada__exceptions__create_exception_occurence_message(
              static_cast<int>(standard_exception_kind::TASKING_ERROR_KIND),
              what.data_()),
          what) {}
TaskingError::TaskingError(void *data)
    : AdaException(data) {}
TaskingError::TaskingError(void *data, const strings::polyglot_string &what)
    : AdaException(data, what) {}

void rethrow_standard_exception() {
    kernel *k = polyglot_get_kernel();
    void *data = k->exc_info.exception_data;
    char *message =k->exc_info.message;
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
    } catch (const std::exception&) {
        k->exc_info.clear_exception(k);
        throw;
    }
}

} // namespace polyglot::ada::exceptions
