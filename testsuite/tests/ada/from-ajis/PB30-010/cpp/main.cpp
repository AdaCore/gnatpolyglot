#include "xref_abstract_entities_cursor.h"
#include "xref_parameter_array.h"
#include "xref_root_reference_iterator.h"
#include <iostream>

namespace pa = xref_parameter_array;
namespace ri = xref_root_reference_iterator;
namespace ac = xref_abstract_entities_cursor;

int main() {
    auto parameter_entity = pa::get_no_root_entity();
    auto reference_entity = ri::get_no_root_entity();
    auto cursor_entity = ac::get_no_root_entity();
    gnatpolyglot::polyglot_ptr<pa::RootEntity> parameter_ptr(*parameter_entity);
    gnatpolyglot::polyglot_ptr<ri::RootEntity> reference_ptr(*reference_entity);
    gnatpolyglot::polyglot_ptr<ac::RootEntity> cursor_ptr(*cursor_entity);
    pa::should_be_bound(true, parameter_ptr);
    pa::should_be_bound(true, parameter_ptr);
    ri::should_be_bound(true, reference_ptr);
    ac::should_be_bound(true, cursor_ptr);

    auto iter = reference_entity->find_all_references(*reference_entity);
    if (iter->at_end()) {
        auto cursor = cursor_entity->get_all_called_entities();
        if (cursor->at_end()) {
            auto params = parameter_entity->parameters();
            if (params.get(params.get_begin())->get_parameter()) {
                std::cout << "OK\n";
            }
        }
    }
}
