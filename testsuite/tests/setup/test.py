import os

from utils import run_setup
from e3.fs import rm


run_setup()
run_setup(check_only=True)
rm(os.path.join("runtimes", "ada", "polyglot-ada.gpr"))
run_setup(check_only=True)

print()

run_setup()
run_setup(check_only=True)
with open(os.path.join("runtimes", "polyglot", "polyglot.gpr"), "a") as f:
    f.write("foo\n")
run_setup(check_only=True)

print()

run_setup(check_only=True, to_lang="cpp", expect_returncode=1)
run_setup(check_only=True, from_lang="ada", to_lang="ada", expect_returncode=1)
