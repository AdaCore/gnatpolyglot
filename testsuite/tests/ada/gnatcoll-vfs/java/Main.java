import java.nio.file.Path;
import java.util.HashMap;

import com.adacore.gnatpolyglot.runtime.BooleanRef;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libgnatcoll_core.gnatcoll.vfs.ReadDirFilter;
import com.adacore.libgnatcoll_core.gnatcoll.vfs.VfsPackage;
import com.adacore.libgnatcoll_core.gnatcoll.vfs.VirtualFile;
import com.adacore.libgnatcoll_core.gnatcoll.vfs.WritableFile;

public class Main {

    static void testF(VirtualFile dir) {
        VirtualFile f = dir.createFromDir(new PolyglotString("foo.txt"), false);

        // A.Assert (+Base_Name (F), "foo.txt", "base name");
        assert f.baseName(new PolyglotString(""), false).toString().equals("foo.txt");
        // A.Assert (+Full_Name (F), +Full_Name (Dir) & "foo.txt", "full name");
        assert f.fullName(false).toString().equals(dir.fullName(false) + "foo.txt");
        // A.Assert (+Dir_Name (F), +Full_Name (Dir), "dir name");
        assert f.dirName().toString().equals(dir.fullName(false).toString());
        // A.Assert (+F.File_Extension, ".txt", "file extension");
        assert f.fileExtension(false).toString().equals(".txt");
        // A.Assert (Is_Absolute_Path (F), "is absolute");
        assert f.isAbsolutePath();
        // A.Assert (not Is_Regular_File (F), "is regular file");
        assert !f.isRegularFile();
        // A.Assert (+Relative_Path (F, Dir), "foo.txt", "relative path");
        assert f.relativePath(dir).toString().equals("foo.txt");
        // A.Assert (Has_Suffix (F, "t"), "has suffix");
        assert f.hasSuffix(new PolyglotString("t"));
        // A.Assert (not Is_Symbolic_Link (F), "is symlink");
        assert !f.isSymbolicLink();

        WritableFile w = f.writeFile(false);
        w.write(new PolyglotString("first word "));
        w.close_();

        w = f.writeFile(true);
        {
            w.write(new PolyglotString("second word"));
        }
        w.close_();

        // Check whether the file exists

        // A.Assert (Is_Regular_File (F), "is regular file after creation");
        assert f.isRegularFile();
        // A.Assert (Is_Writable (F), "is writable after creation");
        assert f.isWritable();


        // Make the file unreadable

        f.setReadable(false);
        // A.Assert (Is_Regular_File (F), "is regular file when unreadable");
        assert f.isRegularFile() ;

        if (!System.getProperty("os.name").startsWith("Windows")) {
            // A.Assert (not Is_Readable (F) or else OS = Windows or else Root,
            //           "is readable");
            assert(!f.isReadable());
        }

        // Try and read the file

        PolyglotString str = f.readFile().orElse(null);
        /// A.Assert (Str = null or Root, "can read unreadable file?");
        assert str == null;

        // Make it readable again, and read again

        f.setReadable(true);
        str = f.readFile().get();
        assert str.toString().equals("first word second word");
        assert f.size() == str.length();
        str._setOwner(Owner.USER);

        // Make the file read-only
        f.setWritable(false);
        // A.Assert (not Is_Writable (F) or else OS = Windows or else Root,
        //           "is writable");
        assert !f.isWritable();

        // Check directory operations
        /// A.Assert (Is_Directory (Dir), "is directory");
        assert dir.isDirectory();
        VirtualFile d = dir.createFromDir(new PolyglotString("sub/sub1"), false);
        d.makeDir(true);
        d.createFromDir(new PolyglotString("foo"), false).writeFile(false).close_();
        VfsPackage.createFromUtf8(
            new PolyglotString(d.fullName(false) + "/bar"),
            VfsPackage.getLocalHost(),
            false
        ).writeFile(false).close_();
        BooleanRef success = new BooleanRef(false);
        d.operatorDiv(new PolyglotString("bar")).rename(d.createFromDir(new PolyglotString("bar.txt"), false), success);
        assert success.getValue();
        d.copy(new PolyglotString("sub/sub2"), success);
        assert success.getValue();
        d.removeDir(false, success);
        assert !success.getValue();

        VirtualFile.Array dirs =
            dir.createFromDir(new PolyglotString("sub"), false)
               .readDirRecursive(new PolyglotString(""), ReadDirFilter.DIRS_ONLY).get();

        assert dirs.size() == 2;
        VirtualFile.Array files = VfsPackage.readFilesFromDirs(dirs).get();
        assert files.size() == 4;
        VfsPackage.uncheckedFree(new VirtualFile.Array.Ref(dirs));
        VfsPackage.uncheckedFree(new VirtualFile.Array.Ref(files));

        d.removeDir(true, success);
        assert success.getValue();

        files = dir.readDirRecursive(new PolyglotString(".txt"), ReadDirFilter.ALL_FILES).get();
        assert files.size() == 2;

        if (!System.getProperty("os.name").startsWith("Windows")) {
            // A.Assert (not Is_Readable (F) or else OS = Windows or else Root,
            //           "is readable");
            f.delete(success);
            assert success.getValue();
        }

        f.delete(success);
        assert !success.getValue();
        files._setOwner(Owner.USER);
    }

    public static void main(String[] args) throws Throwable {
        VirtualFile curDir = VfsPackage.getCurrentDir(VfsPackage.getLocalHost());
        String curDirAd = Path.of(".").toAbsolutePath().normalize() + "/";
        assert VfsPackage.operatorPlus(curDir.dirName()).toString().equals(curDirAd);
        assert VfsPackage.operatorPlus(
                curDir.baseName(new PolyglotString(""), false)).toString().equals("");
        testF(curDir);

        // A.Assert (+Base_Name (No_File), "", "base name");
        assert VfsPackage.getNoFile().baseName(new PolyglotString(""), false).toString().equals("");
        // A.Assert (+Full_Name (No_File), "", "full name");
        assert VfsPackage.getNoFile().fullName(false).toString().equals("");
        // A.Assert (+Dir_Name (No_File), "", "dir name");
        assert VfsPackage.getNoFile().dirName().toString().equals("");
        // A.Assert (+File_Extension (No_File), "", "file extension");
        assert VfsPackage.getNoFile().fileExtension(false).toString().equals("");
        // A.Assert (not Is_Absolute_Path (No_File), "is absolute");
        assert !VfsPackage.getNoFile().isAbsolutePath();
        // A.Assert (not Is_Regular_File (No_File), "is regular file");
        assert !VfsPackage.getNoFile().isRegularFile();

        // Comparisons
        {
            VirtualFile defaultPrefPy =
                VfsPackage.getCurrentDir(VfsPackage.getLocalHost())
                        .createFromDir(new PolyglotString("default_pref.py"), false);
            VirtualFile defaultPrefPyc =
                VfsPackage.getCurrentDir(VfsPackage.getLocalHost())
                        .createFromDir(new PolyglotString("default_pref.pyc"), false);
            assert defaultPrefPy.operatorLt(defaultPrefPyc);
            assert !defaultPrefPyc.operatorLt(defaultPrefPy);

            // Wrapper class to have hashing and equality as Object methods
            class VirtualFileWrapper {
                VirtualFile vf;

                public VirtualFileWrapper(VirtualFile vf) {
                    this.vf = vf;
                }

                @Override
                public int hashCode() {
                    return vf.fullNameHash();
                }

                @Override
                public boolean equals(Object obj) {
                    if (this == obj) return true;
                    if (obj instanceof VirtualFileWrapper vfw)
                        return vf.operatorEq(vfw.vf);
                    return false;
                }

            }

            HashMap<VirtualFileWrapper, Integer> m = new HashMap<>();
            VirtualFile f1 = VfsPackage.create(new PolyglotString("/a/b"), VfsPackage.getLocalHost(), false);
            m.put(new VirtualFileWrapper(f1), 2);
            VirtualFile f2 = VfsPackage.create(new PolyglotString("/a/b/c"), VfsPackage.getLocalHost(), false);
            f2 = f2.getParent();
            assert m.containsKey(new VirtualFileWrapper(f2));
        }

        // Normalizing
        {
            PolyglotString f1 = new PolyglotString(curDirAd + "obj//../obj/.///./main.o");
            PolyglotString f2 = 
                VfsPackage.createFromBase(f1, new PolyglotString(""), VfsPackage.getLocalHost())
                    .fullName(false);
            PolyglotString f3 = VfsPackage.create(f2, VfsPackage.getLocalHost(), true).fullName(false);

            assert f3.toString().equals(Path.of("obj", "main.o").toAbsolutePath().toString());
        }
    }
}
