from utils import run_polyglot, list_generated_sources


run_polyglot("ada2proxy", ["-P", "lib2/lib_2.gpr", "--no-subprojects", "-o", "proxy"])
print(list_generated_sources("proxy"))
