//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef GNATPOLYGLOT_EXCEPTIONS_H
#define GNATPOLYGLOT_EXCEPTIONS_H

#ifdef __cplusplus

#include <exception>

#if __has_include(<string>)
   #include <string>
#endif

namespace gnatpolyglot::exceptions {

class polyglot_exception : public std::exception {
public:
    polyglot_exception(void *data) : _data(data) {}

    void *data_() const { return this->_data; }
    void *release_() {
       void *data = this->_data;
       this->_data = nullptr;
       return data;
    }

protected:
    void *_data;
};

} // namespace gnatpolyglot::exceptions

#endif /* __cplusplus */

#endif /* ! GNATPOLYGLOT_EXCEPTIONS_H */
