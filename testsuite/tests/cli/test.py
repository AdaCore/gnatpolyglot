"""
Test basic commands of the GNATpolyglot's CLI.
"""

import os

from utils import run_polyglot


def print_cmd(subcommand: str, args: list[str], discard_first_lines: int = 0):
    print(f"Running `gnatpolyglot {subcommand} {" ".join(args)}`")
    print("=" * 80)
    print()
    res = run_polyglot(subcommand, args, pipe=True)
    lines = res.split("\n", discard_first_lines)
    print(lines[discard_first_lines], end="")
    print()
    print()


print_cmd("", ["--version"], discard_first_lines=1)

print_cmd("", ["--help"])
print_cmd("setup", ["--help"])
print_cmd("validator", ["--help"])
print_cmd("ada2proxy", ["--help"])
print_cmd("proxy2cpp", ["--help"])
