import sys
import os
import glob
import subprocess
from pathlib import Path

POLYGLOT_HOME = os.path.realpath(
    os.path.join(os.path.dirname(__file__), "..", "..", "polyglot")
)
RUNTIME_DIR = os.path.join(POLYGLOT_HOME, "runtimes")

NATIVE_RUN = "--native" in sys.argv

def run(argv: list[str], env: dict[str, str] | None = None) -> None:
    p = subprocess.run(
        argv,
        stdin=subprocess.DEVNULL,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        encoding="utf-8",
        env=env,
    )
    sys.stdout.write(p.stdout)
    sys.stdout.flush()
    p.check_returncode()


def run_java(main_class: str, class_path: str, argv: list[str]) -> None:
    """
    Run a java command.
    """

    java_exec = os.path.realpath(os.path.join(os.environ["JAVA_HOME"], "bin", "java"))

    extra_args = [
        "--add-exports",
        "org.graalvm.truffle/com.oracle.truffle.api.strings=ALL-UNNAMED",
    ]
    run([java_exec, "-cp", class_path, *extra_args, main_class, *argv])

def run_native(subcommand: str, argv: list[str]):
    run(["polyglot", subcommand, *argv])

def run_proxy_validator(proxy_location: str) -> None:
    """
    Run the proxy validator on the given proxy json file.
    """
    if NATIVE_RUN:
        run_native("validator", [proxy_location])
    else:
        run_java(
            "com.adacore.polyglot.cli.PolyglotMain",
            os.path.join(POLYGLOT_HOME, "cli", "target", "cli.jar"),
            ["validator", proxy_location],
        )


def run_scanner(input_lang: str, project_file: str, output_path: str) -> None:
    """
    Run a scanner on ``project_file`` and generate the proxy at
    ``output_path``.
    """
    if input_lang == "ada":
        if NATIVE_RUN:
            run_native("ada2proxy", ["-P", project_file, "-o", output_path])
        else:
            run_java(
                "com.adacore.polyglot.cli.PolyglotMain",
                os.path.join(POLYGLOT_HOME, "cli", "target", "cli.jar"),
                ["ada2proxy", "-P", project_file, "-o", output_path],
            )
    else:
        raise Exception(f"Unknown language: {input_lang}")


def compile_main(output_lang: str, test_file: str, output_proxy: str, input_proxy: str) -> str:
    """
    Compile the main test file and return a path to its corresponding
    executable.
    """
    if output_lang == "c++":
        proxy_c_files = glob.glob(os.path.join(output_proxy, "*.cpp"))
        C_FLAGS = [
            "-Wall",
            "-Wextra",
            "-Werror",
            "-std=c++11",
        ]

        LD_FLAGS = [
            f"-L{os.path.join(input_proxy, 'lib_agg', 'static', 'dev')}",
            "-ltest_proxy_agg",
            "-ldl",
            "-lpthread",
        ]
        argv = [
            "g++",
            f"-I{os.path.join(output_proxy, 'include')}",
            "-o",
            "main",
            test_file,
            *proxy_c_files,
            *C_FLAGS,
            *LD_FLAGS,
        ]
        run(argv)
        return os.path.realpath("main")
    else:
        raise Exception(f"Unknown language: {output_lang}")


def compile_lib(input_lang: str, proxy_location: str) -> None:
    """
    Compile the generated library at the given path.
    """
    if input_lang == "ada":
        run([
            "gprbuild",
            str(list(Path(proxy_location).glob("*agg.gpr"))[0]),
            "-q",
            "-XLIBRARY_TYPE=static",
        ])
    elif input_lang == "c++":
        run(["make", "--silent", "-B", "-C", proxy_location])
    else:
        raise Exception(f"Unknown language: {input_lang}")


def run_printer(output_lang: str, proxy_file: str, output_path: str) -> None:
    """
    Run a scanner on ``project_file`` and generate the proxy at
    ``output_path``.
    """
    if output_lang == "c++":
        if NATIVE_RUN:
            run_native("proxy2cpp", [proxy_file, "-o", output_path])
        else:
            run_java(
                "com.adacore.polyglot.cli.PolyglotMain",
                os.path.join(POLYGLOT_HOME, "cli", "target", "cli.jar"),
                ["proxy2cpp", proxy_file, "-o", output_path],
            )
    else:
        raise Exception(f"Unknown language: {output_lang}")

def add_path(env: dict[str, str], env_var: str, path: str):
    """
    Adds the path to the ``env_var`` path variable in ``env``
    """
    env[env_var] = "{}{}{}".format(path, os.path.pathsep, env.get(env_var, ""))

def valgrind_cmd(argv: list[str]):
    suppression_file = os.path.join(
        os.path.dirname(os.path.realpath(__file__)), "package_elab.supp"
    )
    return [
        "valgrind",
        "-q",
        "--leak-check=full",
        "--show-leak-kinds=all",
        "--track-origins=yes",
        "--error-exitcode=2",
        f"--suppressions={suppression_file}",
        *argv
    ]
