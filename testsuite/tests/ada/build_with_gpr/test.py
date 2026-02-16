"""
The goal of this test is to only building bindings and a main using only GPR,
thus never leaving an Ada environment, instead of calling the compiler
ourselves.
"""

from utils import run, run_scanner, run_printer
from os.path import join as P

run_scanner("ada", "test.gpr", "2proxy")
run_printer("c++", P("2proxy", "proxy.json"), "2cpp")
run(["gprbuild2", "main.gpr", "-q"])
run([P("bin", "main")])

