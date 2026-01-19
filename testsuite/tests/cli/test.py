"""
Test basic commands of the Polyglot's CLI.
"""

import os

from utils import run_polyglot


def print_help(subcommand: str):
    print(f"Running `polyglot {subcommand} --help`")
    print("=" * 80)
    print()
    run_polyglot(subcommand, ["--help"])
    print()
    print()


print_help("")
print_help("setup")
print_help("validator")
print_help("ada2proxy")
print_help("proxy2cpp")
