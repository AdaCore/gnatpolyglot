from utils import run_polyglot, list_generated_sources


run_polyglot("ada2proxy", ["-P", "test.gpr", "-o", "proxy"])
print(list_generated_sources("proxy"))
