#ifndef POLYGLOT_EXCEPTIONS_H
#define POLYGLOT_EXCEPTIONS_H

#ifdef __cplusplus

#include <exception>
#include <string>

namespace polyglot::exceptions {

class polyglot_exception : public std::exception {
public:
    polyglot_exception(void *data) : _data(data) {}
    polyglot_exception(void *data, const std::string &what)
        : _data(data), _what(what) {}

    const char *what() const noexcept override { return _what.data(); }

    void *data() const { return this->_data; }
    void *release() {
       void *data = this->_data;
       this->_data = nullptr;
       return data;
    }

protected:
    void *_data;
    std::string _what;
};

} // namespace polyglot::exceptions

#endif /* __cplusplus */

#endif /* ! POLYGLOT_EXCEPTIONS_H */
