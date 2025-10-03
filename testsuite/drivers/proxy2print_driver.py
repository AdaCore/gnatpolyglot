import os

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
        if self.env.options.valgrind:
            res.append("--enable-valgrind")
        return res

    def set_up(self) -> None:
        super().set_up()

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
