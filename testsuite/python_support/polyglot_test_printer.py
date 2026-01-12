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

print("Running printer test.")

input_proxy_location = os.path.join(input_lib, "proxy")
mkdir(input_proxy_location)

print("Running the scanner...")
run_scanner(scfg.input_lang, scfg.project_file, input_proxy_location, scfg.extra_args)
print("Compiling the library...")
compile_lib(scfg.input_lang, scfg.project_file, scfg.input_lib_flags)
compile_lib(
    scfg.input_lang,
    get_proxy_lib_file(scfg.input_lang, input_proxy_location),
    scfg.output_lib_flags
)

pcfg = PrinterConfig(".")

output_proxy_location = os.path.join("output_proxy")
mkdir(output_proxy_location)

print("Running the printer...")
run_printer(
    pcfg.output_lang,
    os.path.join(input_proxy_location, "proxy.json"),
    output_proxy_location
)

print("Compiling test main program...")
res = compile_main(
    pcfg.output_lang,
    pcfg.test_file,
    output_proxy_location,
    input_proxy_location,
    scfg.input_lib_name,
    pcfg.cflags,
    pcfg.ldflags,
)

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
