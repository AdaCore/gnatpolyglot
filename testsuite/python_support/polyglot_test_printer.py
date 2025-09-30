import os
import subprocess
import sys
import yaml

from e3.fs import mkdir

from utils import (
    add_path, compile_lib, get_proxy_lib_file, run_scanner, run_printer,
    compile_main, run, valgrind_cmd
)

env = os.environ
input_lib = os.path.realpath("input_proxy")

try:
    with open(os.path.join(input_lib, "test.yaml")) as f:
        input_proxy_config = yaml.safe_load(f)
    input_project_file = os.path.join(
        input_lib, input_proxy_config["project_file"]
    )
    input_lang = input_proxy_config["input_lang"]
    if input_lang == "ada":
        # Remove the `.gpr` file extension to get the name of the lib
        input_lib_name = os.path.basename(input_proxy_config["project_file"])[:-4]
    else:
        input_lib_name = ""
    units = ",".join(input_proxy_config.get("units", []))
    scanner_args = []
    if units != "":
        scanner_args.append(f"--units={units}")
    for p in input_proxy_config.get("local_project_path", []):
        add_path(env, "GPR_PROJECT_PATH", os.path.join(input_lib, p))

    input_lib_flags = input_proxy_config.get("input_lib_flags", [])
except KeyError as e:
    print(f"Error: missing value of `{e.args[0]}` in proxy test.yaml")
    sys.exit(1)

print("Running printer test.")

input_proxy_location = os.path.join(input_lib, "proxy")
mkdir(input_proxy_location)

print("Running the scanner...")
run_scanner(input_lang, input_project_file, input_proxy_location, scanner_args)
print("Compiling the library...")
compile_lib(input_lang, input_project_file, input_lib_flags)
compile_lib(input_lang, get_proxy_lib_file(input_lang, input_proxy_location))

try:
    with open("test.yaml") as f:
        output_proxy_config = yaml.safe_load(f)
    output_lang = output_proxy_config["output_lang"]
    test_file = output_proxy_config["test_file"]
except KeyError as e:
    print(f"Error: missing value of `{e.args[0]}` in test.yaml")
    sys.exit(1)


output_proxy_location = os.path.join("output_proxy")
mkdir(output_proxy_location)

print("Running the printer...")
run_printer(output_lang, os.path.join(input_proxy_location, "proxy.json"), output_proxy_location)

print("Compiling test main program...")
res = compile_main(output_lang, test_file, output_proxy_location, input_proxy_location, input_lib_name)

print("Running test main program...")
print()

add_path(env, "LD_LIBRARY_PATH", f"{os.path.join(input_proxy_location, 'lib', 'relocatable')}")
add_path(env, "LD_LIBRARY_PATH", f"{os.path.join(output_proxy_location)}")
main_argv = [res]
if "--enable-valgrind" in sys.argv:
    main_argv = valgrind_cmd(main_argv)
run(main_argv, env)

print()
print("Done.")
