import os
from utils import run_scanner, run_printer
from pathlib import Path


run_scanner("ada", "test.gpr", "proxy")
run_printer("java", os.path.join("proxy", "proxy.json"), "2java")

def get_docstrings(filename: Path):
    print(f"{'=' * len(filename.as_posix())}=")
    print(f"{filename.as_posix()}:")
    with open(filename) as f:
        lines = [l.strip() for l in f.readlines()]
        i = 0
        while i < len(lines):
            if lines[i].startswith("/**"):
                print(lines[i])
                i += 1
                while not lines[i - 1].endswith("*/"):
                    print(f" {lines[i]}")
                    i += 1
                # print the decl
                while lines[i - 1].count("(") == 0 and lines[i - 1].count("{") == 0:
                    print(lines[i])
                    i += 1
                print()
            i += 1
    print()
    print()


sources = []

for root, _, files in os.walk(
    Path("2java", "src", "main", "java", "com", "adacore", "libtest", "test")
):
    for f in files:
        if f.endswith(".java") and not f == "Library.java":
            sources.append(Path(root, f))

sources.sort()
for s in sources:
    get_docstrings(s)
