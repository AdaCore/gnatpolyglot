"""
In this test, we're asking polyglot to generate bindings for both lib_1.ads as
well as lib_2.ads, so the generated library should contain lib_1-proxy and
lib_2-proxy units. This also checks that giving multiple files to --spec-files
works as expected.
"""
from utils import run_polyglot, list_generated_sources


run_polyglot(
    "ada2proxy",
    ["-P", "lib2/lib_2.gpr", "-o", "proxy", "--spec-files", "lib_1.ads,lib_2.ads"]
)
print(list_generated_sources("proxy"))
