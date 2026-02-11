"""
In this run, although we're only asking to generate bindings for lib_3.ads,
polyglot sees that lib_1.ads is a required dependency, so it includes it in
the final library. However, lib_2 should not be involved at all, although it
is a library dependency of lib_3.
"""
from utils import run_polyglot, list_generated_sources


run_polyglot(
    "ada2proxy",
    ["-P", "lib3/lib_3.gpr", "-o", "proxy", "--spec-files", "lib_3.ads"]
)
print(list_generated_sources("proxy"))
