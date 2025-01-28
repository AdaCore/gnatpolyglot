#! /usr/bin/env python

import sys

from e3.testsuite import Testsuite

from drivers import junit_driver


class PolyglotTestsuite(Testsuite):
    tests_subdir = "tests"
    test_driver_map = {
        "junit": junit_driver.JunitDriver,
    }


sys.exit(PolyglotTestsuite().testsuite_main())
