#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ptr.h"
#include <cassert>
#include <filesystem>
#include <functional>
#include <gnatcoll_vfs.h>
#include <unordered_map>

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::strings;
using namespace gnatpolyglot::ada::arrays;
using namespace gnatcoll::vfs;

void test_f(const VirtualFile &dir) {
    VirtualFile f = dir.create_from_dir("foo.txt", false);

    /// A.Assert (+Base_Name (F), "foo.txt", "base name");
    assert(to_string(+f.base_name("", false)) == "foo.txt");
    /// A.Assert (+Full_Name (F), +Full_Name (Dir) & "foo.txt", "full name");
    assert(to_string(+f.full_name(false)) == to_string(dir.full_name(false)) + "foo.txt");
    /// A.Assert (+Dir_Name (F), +Full_Name (Dir), "dir name");
    assert(to_string(+f.dir_name()) == to_string(+dir.full_name(false)));
    /// A.Assert (+F.File_Extension, ".txt", "file extension");
    assert(to_string(+f.file_extension(false)) == ".txt");
    /// A.Assert (Is_Absolute_Path (F), "is absolute");
    assert(f.is_absolute_path());
    /// A.Assert (not Is_Regular_File (F), "is regular file");
    assert(!f.is_regular_file());
    /// A.Assert (+Relative_Path (F, Dir), "foo.txt", "relative path");
    assert(to_string(f.relative_path(dir)) == "foo.txt");
    /// A.Assert (Has_Suffix (F, "t"), "has suffix");
    assert(f.has_suffix("t"));
    /// A.Assert (not Is_Symbolic_Link (F), "is symlink");
    assert(!f.is_symbolic_link());

    WritableFile w = f.write_file(false);
    w.write(from_string("first word "));
    w.close();

    w = f.write_file(true);
    {
        w.write("second word");
    }
    w.close();

    // Check whether the file exists

    /// A.Assert (Is_Regular_File (F), "is regular file after creation");
    assert(f.is_regular_file());
    /// A.Assert (Is_Writable (F), "is writable after creation");
    assert(f.is_writable());

    // Make the file unreadable

    f.set_readable(false);
    /// A.Assert (Is_Regular_File (F), "is regular file when unreadable");
    assert(f.is_regular_file());
#if !defined(_WIN32)
    /// A.Assert (not Is_Readable (F) or else OS = Windows or else Root,
    ///           "is readable");
    assert(!f.is_readable());
#endif

    // Try and read the file

    polyglot_ptr<polyglot_string> str = f.read_file();
    /// A.Assert (Str = null or Root, "can read unreadable file?");
    assert(str.get() == nullptr);
    str.set_owner(gnatpolyglot::memory_owner::USER);
    str.reset();

    // Make it readable again, and read again

    f.set_readable(true);
    str = f.read_file();
    /// A.Assert (Str.all, "first word second word", "contents when readable");
    assert(to_string(*str) == "first word second word");
    /// A.Assert (Integer (Size (F)), Str.all'Length, "file size");
    assert(f.size() == str->size());
    str.set_owner(gnatpolyglot::memory_owner::USER);
    str.reset();

    // Make the file read-only
    f.set_writable(false);
    /// A.Assert (not Is_Writable (F) or else OS = Windows or else Root,
    ///           "is writable");
    assert(!f.is_writable());

    // Check directory operations
    /// A.Assert (Is_Directory (Dir), "is directory");
    assert(dir.is_directory());
    VirtualFile d = dir.create_from_dir("sub/sub1", false);
    d.make_dir(true);
    d.create_from_dir("foo", false).write_file(false).close();
    create_from_utf8(from_string(to_string(+d.full_name(false)) + "/bar"),
                     get_local_host(), false)
            .write_file(false)
            .close();
    bool success;
    (d / "bar").rename(d.create_from_dir("bar.txt", false), success);
    assert(success);
    d.copy("sub/sub2", success);
    assert(success);
    d.remove_dir(false, success);
    assert(!success);

    polyglot_ptr<polyglot_array<VirtualFile>> dirs =
        dir.create_from_dir("sub", false)
            .read_dir_recursive("", ReadDirFilter::DIRS_ONLY);
    assert(dirs->size() == 2);
    polyglot_ptr<polyglot_array<VirtualFile>> files =
        read_files_from_dirs(*dirs);
    assert(files->size() == 4);
    unchecked_free(dirs);
    unchecked_free(files);

    d.remove_dir(true, success);
    assert(success);
    files = dir.read_dir_recursive(".txt", ::ReadDirFilter::ALL_FILES);
    // Delete the file
    // A.Assert (Success or else OS = Windows, "could delete");
#if !defined(_WIN32)
    f.delete_(success);
#endif
    assert(success);
    f.delete_(success);
    assert(!success);
    files.set_owner(gnatpolyglot::memory_owner::USER);
}

template<>
struct std::hash<VirtualFile> {
    std::size_t operator()(const VirtualFile &v) const {
        return v.full_name_hash();
    }
};

int main() {
    VirtualFile cur_dir = get_current_dir(get_local_host());
    std::string cur_dir_ad = (std::filesystem::current_path() / "").string();

    assert(to_string(+cur_dir.dir_name()) == cur_dir_ad);
    assert(to_string(+cur_dir.base_name("", false)) == "");

    test_f(cur_dir);

    // Try manipulating no_file

    /// A.Assert (+Base_Name (No_File), "", "base name");
    assert(to_string(+get_no_file()->base_name("", false)) == "");
    /// A.Assert (+Full_Name (No_File), "", "full name");
    assert(to_string(+get_no_file()->full_name(false)) == "");
    /// A.Assert (+Dir_Name (No_File), "", "dir name");
    assert(to_string(+get_no_file()->dir_name()) == "");
    /// A.Assert (+File_Extension (No_File), "", "file extension");
    assert(to_string(+get_no_file()->file_extension(false)) == "");
    /// A.Assert (not Is_Absolute_Path (No_File), "is absolute");
    assert(!get_no_file()->is_absolute_path());
    /// A.Assert (not Is_Regular_File (No_File), "is regular file");
    assert(!get_no_file()->is_regular_file());

    // Comparisons
    {
        VirtualFile default_pref_py =
            get_current_dir(get_local_host())
                    .create_from_dir("default_pref.py", false);
        VirtualFile default_pref_pyc =
            get_current_dir(get_local_host())
                    .create_from_dir("default_pref.pyc", false);
        assert(default_pref_py < default_pref_pyc);
        assert(!(default_pref_pyc < default_pref_py));

        std::unordered_map<VirtualFile, int> m;
        VirtualFile f1 = create("/a/b", get_local_host(), false);
        m[f1] = 2;
        VirtualFile f2 = create("/a/b/c", get_local_host(), false);
        f2 = f2.get_parent();
        assert(m.count(f2) == 1);
    }

    // Normalizing
    {
        polyglot_string f1 = from_string(cur_dir_ad + "obj//../obj/.///./main.o");
        polyglot_string f2 = create_from_base(f1, "", get_local_host())
                                    .full_name(false);
        polyglot_string f3 = create(f2, get_local_host(), true).full_name(false);

        assert(
            to_string(f3)
                == (std::filesystem::current_path() / "obj" / "main.o").string());
    }
}
