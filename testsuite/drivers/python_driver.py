import os
import sys

from e3.testsuite.driver.diff import DiffTestDriver


class PythonDriver(DiffTestDriver):
    """
    Base Python driver that runs a python script.

    By default, runs the ``test.py`` file located in the test folder, but it
    can be changed by overriding the ``script_and_args`` property.
    """

    def add_path(self, env: dict[str, str], env_var: str, path: str):
        """
        Adds the path to the ``env_var`` path variable in ``env``
        """
        env[env_var] = "{}{}{}".format(path, os.path.pathsep, env.get("env_var", ""))

    @property
    def script_and_args(self) -> list[str]:
        """
        Return the path to the script with the argument to use when running the
        test script.
        """
        return ["test.py"]

    @property
    def testsuite_dir(self) -> str:
        """
        Return the path to the root testsuite directory.
        """
        return os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))

    @property
    def support_dir(self) -> str:
        """
        Return the path to the ``python_support`` directory.
        """
        return os.path.join(self.testsuite_dir, "python_support")

    def run(self) -> None:
        env = dict(os.environ)
        self.add_path(env, "PYTHONPATH", self.support_dir)
        self.shell([sys.executable] + self.script_and_args)
