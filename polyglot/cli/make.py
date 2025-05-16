import argparse
import os
import os.path as P
import subprocess

BIN_DIR = P.join(P.dirname(__file__), "..", "bin")
CLI_JAR = P.join(P.dirname(__file__), "target", "cli.jar")

if __name__ == "__main__":

    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--build-mode",
        choices=["dev", "prod", "debug"],
        default="dev",
        help="define the build mode",
    )
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

    # Create the base Native-Image command
    cmd = [
        native_image,
        "--macro:truffle",
        "--no-fallback",
        "-H:-UseContainerSupport",
    ]

    if args.build_mode in ("dev", "debug"):
        cmd.extend(
            [
                "-g",
                "-O0",
                "-H:-DeleteLocalSymbols",
                "-H:+SourceLevelDebug",
                "-H:+PreserveFramePointer",
                "-H:+IncludeNodeSourcePositions",
            ]
        )
        if args.build_mode == "debug":
            cmd.append("-H:+PrintRuntimeCompileMethods")

    final_cmd = cmd + [
        "-cp",
        os.pathsep.join([CLI_JAR]),
        f"com.adacore.polyglot.cli.PolyglotMain",
        P.join(BIN_DIR, "polyglot"),
    ]

    # Debug print and run
    print(f"Execute: {final_cmd}", flush=True)
    subprocess.check_call(final_cmd)
