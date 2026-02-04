"""
Test basic commands of the Polyglot's CLI.
"""

import os

from utils import run_polyglot


def print_cmd(subcommand: str, args: list[str]):
    print(f"Running `polyglot {subcommand} {" ".join(args)}`")
    print("=" * 80)
    print()
    run_polyglot(subcommand, args)
    print()
    print()


print_cmd("", ["--version"])

print_cmd("", ["--help"])
print_cmd("setup", ["--help"])
print_cmd("validator", ["--help"])
print_cmd("ada2proxy", ["--help"])
print_cmd("proxy2cpp", ["--help"])
