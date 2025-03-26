import os

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

        sync_tree(
            self.test_dir(self.test_env["input_proxy"]),
            self.working_dir("input_proxy")
        )
