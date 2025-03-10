#! /usr/bin/env python

import sys

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

    def set_up(self) -> None:
        super().set_up()

        args = self.main.args

        self.env.rewrite_baselines = args.rewrite


sys.exit(PolyglotTestsuite().testsuite_main())
