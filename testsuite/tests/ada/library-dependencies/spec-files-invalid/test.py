"""
In this test, we're asking polyglot to generate bindings for lib_3.ads.
However, the project file is the one for lib_2.gpr, thus the source file
lib_3.ads will not be found. Polyglot should emit a proper error for that.
"""
from utils import run_polyglot, list_generated_sources


run_polyglot(
    "ada2proxy",
    ["-P", "lib2/lib_2.gpr", "-o", "proxy", "--spec-files", "lib_2.ads,lib_3.ads"],
    expect_returncode=1
)
