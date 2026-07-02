"""
Test that the --header-file switch correctly prepends the given file's content
to all generated sources. Right now, this only tests that it works for the
generated Ada and C++ sources.
"""

import os
from pathlib import Path

from utils import run_polyglot, list_generated_sources


SRC_EXTENSIONS = ["ads", "adb", "h", "c", "hpp", "cpp", "java"]

def collect_sources(src_dir: Path, ignore: Path | None = None) -> list[Path]:
    result = []
    for root, _, files in os.walk(src_dir.as_posix()):
        if ignore is not None and root.startswith(str(ignore)):
            continue
        for f in files:
            if any(
                f.endswith(ext)
                for ext in SRC_EXTENSIONS
            ):
                result.append(Path(root, f))
    result.sort(key=Path.as_posix)
    return result


all_sources = []

run_polyglot("ada2proxy", ["--header-file", "header.txt", "-P", "lib.gpr", "-o", "proxy"])
all_sources += collect_sources(Path("proxy", "src"))

run_polyglot("proxy2cpp", ["--header-file", "header.txt", "proxy/proxy.json", "-o", "cpp"])
all_sources += collect_sources(Path("cpp"), Path("cpp", "runtime"))

run_polyglot("proxy2java", ["--header-file", "header.txt", "proxy/proxy.json", "-o", "java"])
all_sources += collect_sources(Path("java"), Path("java", "runtime"))

header_line_count = 0
with open("header.txt", "r") as header_file:
    header_line_count = len(header_file.readlines())

print(
    "Checking presence of the user-given header in the following generated"
    + " source files:"
)
for s in all_sources:
    print(f"  - {s.as_posix()}:")
    with open(s, "r") as f:
        lines = f.read().splitlines()
        content = [l for l in lines if l]
        for l in content[:header_line_count]:
            print(f"    {l}")
