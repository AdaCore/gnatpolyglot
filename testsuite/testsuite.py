#! /usr/bin/env python

import os
import sys

from e3.os import process
from e3.os.fs import which
from e3.testsuite import Testsuite

from drivers import (
    junit_driver, proxy2print_driver, python_driver, scan2proxy_driver
)


class PolyglotTestsuite(Testsuite):
    tests_subdir = "tests"
    test_driver_map = {
        "junit": junit_driver.JunitDriver,
        "python": python_driver.PythonDriver,
        "scan2proxy": scan2proxy_driver.Scan2Proxy,
        "proxy2print": proxy2print_driver.Proxy2Print,
    }

    def add_options(self, parser) -> None:
        parser.add_argument(
            "--rewrite",
            "-r",
            action="store_true",
            help="Rewrite test baselines according to current output.",
        )

        parser.add_argument(
            "--valgrind",
            action="store_true",
            help="Run test executables with Valgrind to check memory issues.",
        )

        parser.add_argument(
            "--native",
            action="store_true",
            help="Run polyglot using the native-image build.",
        )

        parser.add_argument(
            "--maven-executable",
            help="Specify the Maven executable to use. The default one is"
            ' "mvn".'
        )

        parser.add_argument(
            "--maven-local-repo",
            help="Specify the Maven repository to use. The default one is the"
            " user's repository (~/.m2).",
        )

        parser.add_argument(
            "--lal_version",
            help="Specify the version of Libadalang to use. The default one is"
            ' "0.1".'
        )

    def set_up(self) -> None:
        super().set_up()

        args = self.main.args

        self.env.rewrite_baselines = args.rewrite

        # Make sure the runtime is built before running the tests
        try:
            runtime_dir = os.environ["POLYGLOT_RUNTIME"]
        except KeyError:
            runtime_dir = os.path.join(
                os.path.dirname(which("polyglot")), "..", "polyglot", "runtimes"
            )

        for gpr_file in [
            os.path.join(runtime_dir, "polyglot", "polyglot.gpr"),
            os.path.join(runtime_dir, "ada", "polyglot-ada.gpr"),
        ]:
            process.Run(["gprbuild", "-P", gpr_file, "-p", "-f"])


sys.exit(PolyglotTestsuite().testsuite_main())
