import os

from drivers.python_driver import PythonDriver


class Scan2Proxy(PythonDriver):
    """
    Driver to test scanners.
    """

    @property
    def script_and_args(self) -> list[str]:
        return [os.path.join(self.support_dir, "polyglot_test_scanner.py")]
