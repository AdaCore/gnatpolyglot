from utils import run_polyglot, list_generated_sources


def contains_prefix(values: list[str], prefix: str) -> bool:
    for v in values:
        if v.startswith(prefix):
            return True
    return False

run_polyglot(
    "ada2proxy",
    ["-v", "-P", "lib2/lib_2.gpr", "--process-runtime", "-o", "proxy"],
    pipe=True # Binding the runtime generates a lot of warnings, discard them
)
sources = list_generated_sources("proxy")
print("generated package of lib1? %s" % contains_prefix(sources, "lib_1"))
print("generated package of lib2? %s" % contains_prefix(sources, "lib_2"))
print("generated package of ada runtime? %s" % contains_prefix(sources, "ada"))
