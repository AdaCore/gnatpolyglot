#include "hello.h"
#include "hello_file_info_sets.h"
#include <cassert>
#include <iostream>

int main() {
    {
        hello::file_info_sets::Set set;
        hello::FileInfo item, equivalent;
        hello::file_info_sets::Cursor position;
        assert(set.is_empty() && set.length() == 0);
        assert(!set.first().has_element());

        bool inserted = false;
        set.insert(item, position, inserted);
        assert(inserted);
        assert(!set.is_empty() && set.length() == 1);
        assert(position.has_element());

        // Null-record values are equivalent: this must not add a second node.
        set.insert(equivalent, position, inserted);
        assert(!inserted && set.length() == 1);
        assert(set.contains(equivalent));
        auto found = set.find(equivalent);
        assert(found.has_element());
        assert(found.element());
        auto first = set.first();
        auto end = first.next();
        assert(first.has_element());
        assert(!end.has_element());

        {
            hello::file_info_sets::Set copy(set);
            assert(copy.length() == 1 && copy.contains(item));
            copy.delete_first();
            assert(copy.is_empty() && copy.length() == 0);
            assert(set.length() == 1);
        }
        set.delete_(equivalent);
        assert(set.is_empty() && set.length() == 0);
        set.insert(item);
        set.clear();
        assert(set.is_empty() && set.length() == 0);
    }
    std::cout << "OK\n";
}
