import sys
import os
import glob
import subprocess
import yaml
from pathlib import Path

POLYGLOT_HOME = os.path.realpath(
    os.path.join(os.path.dirname(__file__), "..", "..", "polyglot")
)
RUNTIME_DIR = os.path.join(POLYGLOT_HOME, "runtimes")

NATIVE_RUN = "--native" in sys.argv
POLYGLOT_EXEC = "polyglot.py"
if NATIVE_RUN:
    POLYGLOT_EXEC = "polyglot"

def run(
    argv: list[str],
    env: dict[str, str] | None = None,
    pipe: bool = False,
    expect_returncode: int = 0
) -> str | None:
    p = subprocess.run(
        argv,
        stdin=subprocess.DEVNULL,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        encoding="utf-8",
        env=env,
    )
    out = p.stdout
    if not pipe:
        sys.stdout.write(out)
        sys.stdout.flush()
    if p.returncode != expect_returncode:
        raise RuntimeError(
            f"Expected return code {expect_returncode} but got {p.returncode}"
        )
    return out


def run_java(main_class: str, class_path: str, argv: list[str]) -> None:
    """
    Run a java command.
    """

    java_exec = os.path.realpath(os.path.join(os.environ["JAVA_HOME"], "bin", "java"))

    extra_args = ["--enable-native-access=ALL-UNNAMED"]
    return run([java_exec, "-cp", class_path, *extra_args, main_class, *argv])


def run_polyglot(
    subcommand: str,
    argv: list[str],
    pipe: bool = False,
    expect_returncode: int = 0
):
    return run(
        [POLYGLOT_EXEC, subcommand, *argv],
        pipe=pipe,
        expect_returncode=expect_returncode
    )


def run_proxy_validator(proxy_location: str, expect_returncode: int = 0) -> None:
    """
    Run the proxy validator on the given proxy json file.
    """
    run_polyglot(
        "validator",
        [proxy_location],
        expect_returncode=expect_returncode
    )


class ScannerConfig:

    def __init__(self, path) -> None:
        with open(os.path.join(path, "test.yaml")) as f:
            self._cfg = yaml.safe_load(f)
        self._root = path

    @property
    def project_file(self) -> str:
        return os.path.join(self._root, self._cfg["project_file"])

    @property
    def input_lib_name(self) -> str:
        if self.input_lang == "ada":
            # Remove the `.gpr` file extension to get the name of the lib
            return os.path.basename(self.project_file)[:-4]
        raise Exception(f"Unknown language {self.input_lang}")

    @property
    def input_lang(self) -> str:
        return self._cfg["input_lang"]

    @property
    def extra_args(self) -> list[str]:
        args = []
        specFiles = ",".join(self._cfg.get("spec_files", []))
        if specFiles != "":
            args.append(f"--spec-files={specFiles}")
        args.extend(self._cfg.get("scanner_extra_args", []))
        return args

    @property
    def input_lib_flags(self) -> list[str]:
        return self._cfg.get("input_lib_flags", [])

    @property
    def output_lib_flags(self) -> list[str]:
        return self._cfg.get("output_lib_flags", [])

    def set_env(self) -> None:
        for p in self._cfg.get("local_project_path", []):
            add_path(os.environ, "GPR_PROJECT_PATH", os.path.join(self._root, p))


def run_scanner(
    input_lang: str,
    project_file: str,
    output_path: str,
    extra_args: list[str] | None = None
) -> None:
    """
    Run a scanner on ``project_file`` and generate the proxy at
    ``output_path``.
    """
    if extra_args is None:
        extra_args = []
    if input_lang == "ada":
        run_polyglot(
            "ada2proxy",
            ["-P", project_file, "-o", output_path, *extra_args]
        )
    else:
        raise Exception(f"Unknown language: {input_lang}")


def compile_main(
    output_lang: str,
    test_file: str,
    output_proxy: str,
    input_proxy: str,
    input_lib: str,
    cflags: list[str] | None = None,
    ldflags: list[str] | None = None,
) -> str:
    """
    Compile the main test file and return a path to its corresponding
    executable.
    """
    if output_lang == "c++":
        proxy_c_files = glob.glob(os.path.join(output_proxy, "*.cpp"))
        proxy_c_files += glob.glob(
            os.path.join(output_proxy, "runtimes", "ada", "src2cpp", "*.cpp")
        )
        # We may want to use cflags that are incompatible with the ones we use
        # by default (eg. `--std=c++17 when using C++17 constructs...): specifying
        # cflags will overwrite.
        C_FLAGS = cflags or ["-Wall", "-Wextra", "-Werror", "-std=c++11"]

        LD_FLAGS = [
            f"-L{os.path.join(input_proxy, 'lib_agg', 'static', 'dev')}",
            f"-l{input_lib}_proxy_agg",
            *(ldflags if ldflags else [])
        ]
        if os.name != "nt":
            LD_FLAGS.extend(["-ldl", "-lpthread"])

        argv = [
            "g++",
            f"-I{os.path.join(output_proxy, 'include')}",
            f"-I{os.path.join(output_proxy, 'runtimes', 'ada', 'src2cpp')}",
            "-o",
            "main",
            test_file,
            *proxy_c_files,
            *LD_FLAGS,
            *C_FLAGS,
        ]
        run(argv)
        return os.path.realpath("main")
    else:
        raise Exception(f"Unknown language: {output_lang}")


def compile_lib(
    input_lang: str,
    lib_location: str,
    extra_args: list[str] | None = None
) -> None:
    """
    Compile the generated library at the given path.
    """
    if extra_args is None:
        extra_args = []
    if input_lang == "ada":
        run([
            "gprbuild",
            lib_location,
            "-q",
            "-XLIBRARY_TYPE=static",
            "-gnatwI",
            "--gpr=2",
            *extra_args,
        ])
    elif input_lang == "c++":
        run(["make", "--silent", "-B", "-C", lib_location])
    else:
        raise Exception(f"Unknown language: {input_lang}")


class PrinterConfig():

    def __init__(self, path) -> None:
        with open(os.path.join(path, "test.yaml")) as f:
            self._cfg = yaml.safe_load(f)
        self._root = path

    @property
    def output_lang(self) -> str:
        return self._cfg["output_lang"]

    @property
    def test_file(self) -> str:
        return self._cfg["test_file"]

    @property
    def cflags(self) -> list[str] | None:
        return self._cfg.get("main_cflags")

    @property
    def ldflags(self) -> list[str] | None:
        ld_flags = self._cfg.get("main_ldflags", {})
        if ld_flags:
            if os.name == "nt":
                return ld_flags.get("windows")
            else:
                return ld_flags.get("linux")


def run_printer(output_lang: str, proxy_file: str, output_path: str) -> None:
    """
    Run a scanner on ``project_file`` and generate the proxy at
    ``output_path``.
    """
    if output_lang == "c++":
        run_polyglot("proxy2cpp", [proxy_file, "-o", output_path])
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


def get_proxy_lib_file(input_lang: str, proxy_location: str) -> str:
    if input_lang == "ada":
        return str(list(Path(proxy_location).glob("*agg.gpr"))[0])
    return ""


def list_generated_sources(proxy_dir: Path | str) -> list[str]:
    sources = os.listdir(Path(proxy_dir, "src").as_posix())
    sources.sort()
    return sources


def run_setup(prefix: str = "runtimes", check_only=False):
    argv = [f"--prefix={prefix}"]
    if check_only:
        argv.append("--check-only")
    out = run_polyglot("setup", argv, pipe=True)
    if check_only and len(out) > 0:
        print(out.replace("\\", "/"), end="")
