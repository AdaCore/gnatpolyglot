import os
from subprocess import CalledProcessError
import sys

from utils import run_proxy_validator

print("Running validator test.")

expect_returncode = int(sys.argv[1])

run_proxy_validator(
    os.path.join("proxy.json"),
    expect_returncode=expect_returncode
)

print("Done.")
