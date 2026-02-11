"""
In this test, we're only asking polyglot to generate bindings for lib_2.ads.
Since it does not require any entity from lib_1.ads, the generated library
should indeed contain only lib_2-proxy units, and none from lib_1.
"""
from utils import run_polyglot, list_generated_sources


run_polyglot(
    "ada2proxy",
    ["-P", "lib2/lib_2.gpr", "-o", "proxy", "--spec-files", "lib_2.ads"]
)
print(list_generated_sources("proxy"))
