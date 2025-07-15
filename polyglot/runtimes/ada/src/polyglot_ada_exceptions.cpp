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
              what.data()),
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

} // namespace polyglot::ada::exceptions
