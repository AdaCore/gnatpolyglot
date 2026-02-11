import os

from drivers.python_driver import PythonDriver


class ProxyValidator(PythonDriver):
    """
    Driver to test the proxy validator.
    """

    @property
    def script_and_args(self) -> list[str]:
        return [
            os.path.join(self.support_dir, "polyglot_proxy_validator.py"),
            str(self.test_env.get("expect_returncode", 0))
        ]

