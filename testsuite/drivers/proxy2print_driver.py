import os

from e3.testsuite.driver.classic import TestSkip
import yaml

from drivers.python_driver import PythonDriver

from e3.fs import sync_tree

class Proxy2Print(PythonDriver):
    """
    Driver to test scanners.
    """

    @property
    def script_and_args(self) -> list[str]:
        res = [os.path.join(self.support_dir, "polyglot_test_printer.py")]
        test_valgrind = self.test_env.get("valgrind")
        if test_valgrind == "always" or (
            self.env.options.valgrind and test_valgrind != "never"
        ):
            res.append("--enable-valgrind")
        return res

    def set_up(self) -> None:
        super().set_up()

        if self.test_env.get("output_lang") == "java" and not self.env.java_supported:
            raise TestSkip("Java is not supported on this platform")

        input_proxy_path = self.test_dir(self.test_env["input_proxy"])
        sync_tree(
            input_proxy_path,
            self.working_dir("input_proxy")
        )

        # Sync the input proxy's if necessary
        with open(os.path.join(input_proxy_path, "test.yaml")) as input_proxy_yaml:
            input_proxy_config = yaml.safe_load(input_proxy_yaml)
            for path in input_proxy_config.get("sync_tree", []):
                sync_tree(
                    self.test_dir(path),
                    self.working_dir("input_proxy"),
                    delete=False,
                )

    def run_env(self) -> dict[str, str]:
        env = super().run_env()
        if self.test_env.get("output_lang") == "java":
            maven_args = ""
            if self.env.options.maven_local_repo is not None:
                maven_args = f"-Dmaven.repo.local={self.env.options.maven_local_repo}"
            env["MAVEN_ARGS"] = maven_args
            env["MAVEN_EXEC"] = self.env.options.maven_executable or "mvn"
        return env
