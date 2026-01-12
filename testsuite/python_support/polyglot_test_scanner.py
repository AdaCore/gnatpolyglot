import os
import subprocess
import sys
import yaml

from e3.fs import mkdir

from utils import (
    ScannerConfig, compile_lib, run_proxy_validator, run_scanner, get_proxy_lib_file
)

env = os.environ

cfg = ScannerConfig(".")

cfg.set_env()

proxy_location = "proxy"

mkdir(proxy_location)

print("Running scanner test.")

# Run the scanenr to generate the proxy
print("Running the scanner...")
run_scanner(cfg.input_lang, cfg.project_file, proxy_location, cfg.extra_args)

# Run the proxy validator on the generated json
print("Validating the json...")
run_proxy_validator(os.path.join(proxy_location, "proxy.json"))

# Try to compile the proxy library
print("Trying to compile the library...")
compile_lib(cfg.input_lang, cfg.project_file, cfg.input_lib_flags)
compile_lib(cfg.input_lang,
    get_proxy_lib_file(cfg.input_lang, proxy_location),
    cfg.output_lib_flags,
)

print("Done.")
