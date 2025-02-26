import os
from subprocess import CalledProcessError

from utils import run_proxy_validator

print("Running validator test.")

try:
    run_proxy_validator(os.path.join("proxy.json"))
except CalledProcessError:
    pass

print("Done.")
