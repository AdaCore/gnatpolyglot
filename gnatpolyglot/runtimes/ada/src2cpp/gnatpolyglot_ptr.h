//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef GNATPOLYGLOT_PTR_H
#define GNATPOLYGLOT_PTR_H

#include <cstddef>
#ifdef __cplusplus


namespace gnatpolyglot {
#endif 

#if defined (__cplusplus) && __cplusplus >= 201103L
enum class memory_owner {
#else
enum memory_owner {
#endif 
    UNKNOWN,
    USER,
    LIBRARY,
    STATIC,
};

#ifdef __cplusplus
struct shared_counter {
    int count;
    memory_owner owner;
};

template <typename T>
class polyglot_ptr {
public:
    polyglot_ptr(T* data, memory_owner owner = memory_owner::LIBRARY)
        : _object_data(data)
        , _shared_counter(
            data == nullptr ? nullptr : new shared_counter{1, owner}
        )
    { }
    explicit polyglot_ptr(T& data)
        : _object_data(&data)
        , _shared_counter(new shared_counter{1, memory_owner::STATIC})
    { }

#if __cplusplus >= 201103L
    polyglot_ptr(std::nullptr_t)
        : _object_data(nullptr)
        , _shared_counter(nullptr)
    { }
#endif /** __cplusplus >= 201103L */

    polyglot_ptr(const polyglot_ptr &other)
        : _object_data(other._object_data)
        , _shared_counter(other._shared_counter)
    {
        if (_shared_counter != nullptr)
            _shared_counter->count += 1;
    }

    /** Copy constructor to create a shared_ptr from a child type. */
    template <typename U = T>
    polyglot_ptr(const polyglot_ptr<U> &other)
        : _object_data(other._object_data)
        , _shared_counter(other._shared_counter)
    {
        if (_shared_counter != nullptr)
            _shared_counter->count += 1;
    }

    polyglot_ptr &operator=(const polyglot_ptr<T> &other)
    {
        reset();
        _object_data = other._object_data;
        _shared_counter = other._shared_counter;
        if (_shared_counter != nullptr)
            _shared_counter->count += 1;
        return *this;
    }

    template <typename U>
    polyglot_ptr &operator=(const polyglot_ptr<U> &other)
    {
        reset();
        _object_data = other._object_data;
        _shared_counter = other._shared_counter;
        if (_shared_counter != nullptr)
            _shared_counter->count += 1;
        return *this;
    }

    ~polyglot_ptr() {
        reset();
    }

    T &operator *() {
        return *_object_data;
    }

    const T &operator *() const {
        return *_object_data;
    }

    T *operator->() {
        return _object_data;
    }

    const T *operator->() const {
        return _object_data;
    }

    operator bool() const {
        return _object_data != nullptr;
    }

    /** Return the current owner of the stored pointer. */
    memory_owner get_owner() const {
       return _shared_counter == nullptr
           ? memory_owner::STATIC
           : _shared_counter->owner;
    }

    /**
     * Set the owner of the stored pointer. Changing the ownership from STATIC
     * has not effect.
     */
    void set_owner(memory_owner owner) {
        if (_shared_counter != nullptr
                && _shared_counter->owner != memory_owner::STATIC) {
            _shared_counter->owner = owner;
        }
    }

    /** Return the stored pointer. */
    T* get() const {
        return _object_data;
    }

    /**
     * Swap the stored pointer and the counters with the other `polyglot_ptr`.
     */
    void swap(polyglot_ptr &other) {
        T *object = other._object_data;
        shared_counter *counter = other._shared_counter;
        other._object_data = this->_object_data;
        other._shared_counter = this->_shared_counter;
        this->_object_data = object;
        this->_shared_counter = counter;
    }

    /** Reset the polyglot_ptr */
    void reset() {
        if (_shared_counter == nullptr)
            return;
        _shared_counter->count -= 1;
        if (_shared_counter->count == 0) {
            if (_shared_counter->owner == memory_owner::USER) {
                delete _object_data;
                _object_data = nullptr;
            } else if (_shared_counter->owner == memory_owner::LIBRARY
                    && !_object_data->is_shadow()){
                // In cases where the ref count reaches 0 and the object is
                // library owned, the Ada value must not be freed. However,
                // there exists a C++ value which needs to be freed.
                // Shadow objects are able to free this C++ value from the
                // binded library through a callback provided in the vtable, so
                // do not free the C++ value in that case.
                _object_data->release_();
                delete _object_data;
            }
            delete _shared_counter;
            _shared_counter = nullptr;
        }
    }

    /**
     * Reset the polyglot_ptr and use `new_object` instead and keeping the
     * same previous ownership.
     *
     * If ``new_object == this->get()``, then nothing happens.
     */
    void reset(T *new_object) {
        if (new_object != _object_data)
           reset(new_object, get_owner());
    }

    /**
     * Reset the polyglot_ptr and use `new_object` instead, owned as
     * `new_owner`.
     *
     * If ``new_object == this->get()``, only the owner is changed.
     */
    void reset(T *new_object, memory_owner new_owner) {
        if (new_object != _object_data) {
           reset();
           new (this) polyglot_ptr(new_object, new_owner);
        } else {
           set_owner(new_owner);
        }
    }

    /**
     * Return the number of polyglot_ptr referring to the same object.
     */
    int use_count() const {
        return _shared_counter == nullptr ? 0 : _shared_counter->count;
    }

private:
    T *_object_data;
    shared_counter *_shared_counter;

    template <typename U>
    friend class polyglot_ptr;
};

#endif /** __cplusplus */

#ifdef __cplusplus
} // namespace gnatpolyglot
#endif

#endif /* ! GNATPOLYGLOT_PTR_H */
