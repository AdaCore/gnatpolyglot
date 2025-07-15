#ifndef POLYGLOT_ADA_EXCEPTIONS_H
#define POLYGLOT_ADA_EXCEPTIONS_H

#include "polyglot_ada_strings.h"
#include "polyglot_exceptions.h"

namespace polyglot::ada::exceptions {

class AdaException : public polyglot::exceptions::polyglot_exception {
public:
    AdaException(const strings::polyglot_string &what);
    AdaException(void *data);
    AdaException(void *data, const strings::polyglot_string &what);
    ~AdaException();
};

} // namespace polyglot::ada::exceptions

#endif /* ! POLYGLOT_ADA_EXCEPTIONS_H */
