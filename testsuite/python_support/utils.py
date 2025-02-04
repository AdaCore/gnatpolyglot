import sys
import os
import subprocess
from pathlib import Path

POLYGLOT_HOME = os.path.realpath(
    os.path.join(os.path.dirname(__file__), "..", "..", "polyglot")
)


def run_java(main_class: str, class_path: str, argv: list[str]) -> None:
    """
    Run a java command.
    """

    java_exec = os.path.realpath(os.path.join(os.environ["JAVA_HOME"], "bin", "java"))

    extra_args = [
        "--add-exports",
        "org.graalvm.truffle/com.oracle.truffle.api.strings=ALL-UNNAMED",
    ]

    p = subprocess.run(
        [java_exec, "-cp", class_path, *extra_args, main_class, *argv],
        stdin=subprocess.DEVNULL,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        encoding="utf-8",
    )
    sys.stdout.write(p.stdout)
    sys.stdout.flush()
    p.check_returncode()


def run_proxy_validator(proxy_location: str) -> None:
    """
    Run the proxy validator on the given proxy json file.
    """
    run_java(
        "com.adacore.polyglot.proxy.ProxyValidator",
        os.path.join(POLYGLOT_HOME, "proxy", "target", "proxy.jar"),
        [proxy_location],
    )


def run_scanner(input_lang: str, project_file: str, output_path: str) -> None:
    """
    Run a scanner on ``project_file`` and generate the proxy at
    ``output_path``.
    """
    if input_lang == "ada":
        run_java(
            "com.adacore.polyglot.ada2proxy.Ada2Proxy",
            os.path.join(POLYGLOT_HOME, "ada2proxy", "target", "ada2proxy.jar"),
            [project_file, output_path],
        )


def compile_lib(input_lang: str, proxy_location: str) -> None:
    """
    Compile the generated library at the given path.
    """
    if input_lang == "ada":
        subprocess.run(
            ["gprbuild", list(Path(proxy_location).glob("*.gpr"))[0], "-q"]
        ).check_returncode()
