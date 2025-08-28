import argparse
import os
import os.path as P
import subprocess

BIN_DIR = P.join(P.dirname(__file__), "..", "bin")

def look_for_files_in_env(files: list[str], env_var_name: str) -> dict[str, str] | None:
    """
    Look for required `files` in directories listed in the value of the
    environment variable `env_var_name`. Return the dictionary associating each
    input file to the directory containing it. If one of the requested file
    cannot be retrieved this function returns `None` and displays error message
    about file not being found.
    """
    res = {f: None for f in files}
    for dir in os.environ.get(env_var_name, "").split(os.pathsep):
        for file in files:
            if os.path.isfile(os.path.join(dir, file)):
                res[file] = dir
                break
    one_not_found = False
    for file, dir in res.items():
        if dir is None:
            one_not_found = True
            print(f"Cannot find \"{file}\" in {env_var_name}")
    return None if one_not_found else res

if __name__ == "__main__":

    # Parse arguments provided to this script
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--build-mode",
        choices=["dev", "prod", "debug"],
        default="dev",
        help="define the build mode",
    )
    parser.add_argument("--classpath", help="classpath to forward")
    args = parser.parse_args()

    # Get the GraalVM installation
    try:
        graal_home = os.environ["GRAAL_HOME"]
    except:
        raise RuntimeError(
            "Define the 'GRAAL_HOME' environment variable to your local "
            "GraalVM installation directory"
        )

    native_image = (
        P.join(graal_home, "bin", "native-image.cmd")
        if os.name == "nt"
        else P.join(graal_home, "bin", "native-image")
    )

    # Create the dir hierarchy
    os.makedirs(BIN_DIR, exist_ok=True)

    # Prepare system dependant native-image options
    os_specific_options = []
    if not os.name == "nt":
        # On Linux, we need to provide the compiler additional arguments for
        # it to find headers and shared objects.
        headers_paths = look_for_files_in_env(
            ["libadalang.h"],
            "C_INCLUDE_PATH",
        )
        libs_paths = look_for_files_in_env(
            ["libadalang.so", "libz.so"],
            "LIBRARY_PATH",
        )
        if headers_paths is None or libs_paths is None:
            print("Missing lib dependencies, cannot continue")
            exit(1)

        # We also need to provide rpath-links to the compiler to allow it to
        # find libraries during linking phase.
        ld_library_path = os.environ.get('LD_LIBRARY_PATH')
        rpaths = (
            [f"-Wl,-rpath-link={p}" for p in ld_library_path.split(os.pathsep)]
            if ld_library_path else
            []
        )

        os_specific_options.extend([
            # Then we add additional options for the C compiler
            *[f"--native-compiler-options=-I{dir}" for dir in headers_paths.values()],
            *[f"--native-compiler-options=-L{dir}" for dir in libs_paths.values()],
            *[f"--native-compiler-options={rp}" for rp in rpaths],
        ])

    # Create the base Native-Image command
    cmd = [
        native_image,
        "-cp", args.classpath,
        "--no-fallback",
        "-H:+UnlockExperimentalVMOptions",
        *os_specific_options
    ]

    if args.build_mode in ("dev", "debug"):
        cmd.extend(
            [
                "-g",
                "-Ob",
                "-H:-DeleteLocalSymbols",
                "-H:+SourceLevelDebug",
                "-H:+PreserveFramePointer",
                "-H:+IncludeNodeSourcePositions",
            ]
        )
        if args.build_mode == "debug":
            cmd.extend(
                [
                    "-H:+PrintRuntimeCompileMethods",
                    "-H:+TraceNativeToolUsage",
                ]
            )

    final_cmd = cmd + [
        f"com.adacore.polyglot.cli.PolyglotMain",
        P.join(BIN_DIR, "polyglot"),
    ]

    # Debug print and run
    print(f"Execute: {final_cmd}", flush=True)
    subprocess.check_call(final_cmd)
