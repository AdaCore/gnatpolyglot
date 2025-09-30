import os
import subprocess
import sys
import yaml

from e3.fs import mkdir

from utils import (
    add_path, compile_lib, run_proxy_validator, run_scanner, get_proxy_lib_file
)

env = os.environ

try:
    with open("test.yaml") as f:
        config = yaml.safe_load(f)
    project_file = config["project_file"]
    input_lang = config["input_lang"]
    units = ",".join(config.get("units", []))
    scanner_args = []
    if units != "":
        scanner_args.append(f"--units={units}")
    for p in config.get("local_project_path", []):
        add_path(env, "GPR_PROJECT_PATH", p)

    input_lib_flags = config.get("input_lib_flags", [])
except KeyError as e:
    print(f"Error: missing value of `{e.args[0]}` in test.yaml")
    sys.exit(1)

proxy_location = "proxy"

mkdir(proxy_location)

print("Running scanner test.")

# Run the scanenr to generate the proxy
print("Running the scanner...")
run_scanner(input_lang, project_file, proxy_location, scanner_args)

# Run the proxy validator on the generated json
print("Validating the json...")
run_proxy_validator(os.path.join(proxy_location, "proxy.json"))

# Try to compile the proxy library
print("Trying to compile the library...")
compile_lib(input_lang, project_file, input_lib_flags)
compile_lib(input_lang, get_proxy_lib_file(input_lang, proxy_location))

print("Done.")
