import os
import sys

from e3.fs import mkdir

from utils import (
    PrinterConfig, ScannerConfig, add_path, compile_lib, get_proxy_lib_file, run_scanner, run_printer,
    compile_main, run, valgrind_cmd
)

env = os.environ
input_lib = os.path.realpath("input_proxy")

scfg = ScannerConfig(input_lib)
scfg.set_env()

pcfg = PrinterConfig(".")

print("Running printer test.")

input_proxy_location = os.path.join(input_lib, "proxy")
mkdir(input_proxy_location)

print("Running the scanner...")
run_scanner(scfg.input_lang, scfg.project_file, input_proxy_location, scfg.extra_args)
print("Compiling the library...")
compile_lib(scfg.input_lang, scfg.project_file, scfg.input_lib_flags)
proxy_lib = compile_lib(
    scfg.input_lang,
    get_proxy_lib_file(scfg.input_lang, input_proxy_location),
    pcfg.output_lib_flags or scfg.output_lib_flags
)

output_proxy_location = os.path.join("output_proxy")
mkdir(output_proxy_location)

print("Running the printer...")
run_printer(
    pcfg.output_lang,
    os.path.join(input_proxy_location, "proxy.json"),
    output_proxy_location
)

print("Compiling the library")
out_lib = compile_lib(
    pcfg.output_lang,
    output_proxy_location,
    deps = proxy_lib,
)

print("Compiling test main program...")
main = compile_main(
    pcfg.output_lang,
    pcfg.test_file,
    output_proxy_location,
    input_proxy_location,
    scfg.input_lib_name,
    pcfg.cflags,
    pcfg.ldflags,
    [*proxy_lib, *out_lib]
)

print("Running test main program...")
print()

main_argv = main.exec_cmd
if "--enable-valgrind" in sys.argv:
    main_argv = valgrind_cmd(pcfg.output_lang, main_argv)
for k, v in main.exec_env.items():
    add_path(env, k, v)
run(main_argv, env)

print()
print("Done.")
