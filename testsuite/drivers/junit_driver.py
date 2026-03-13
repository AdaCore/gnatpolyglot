import os
from pathlib import Path

from e3.testsuite import mkdir
from e3.testsuite.driver.classic import ClassicTestDriver
from e3.testsuite.report.index import ReportIndex
from e3.testsuite.report.xunit import XUnitImporter, etree


class JunitDriver(ClassicTestDriver):

    results_dir: str
    """Path where JUnit reports will be produced."""

    gnatpolyglot_java_root_dir: str = os.path.join(
        os.path.dirname(__file__), "..", "..", "gnatpolyglot"
    )
    """Path where the GNATpolyglot Java root ``pom.xml`` file is located."""

    final_report: str
    """Path to the final xml report"""

    def aggregate_junit_results(self):
        # Create a root ``<testsuites>`` tag
        testsuites = etree.Element("testsuites")
        # Add all the testsuites resulting from JUnit execution
        for file in list(Path(self.results_dir).glob("*.xml")):
            testsuites.append(etree.parse(file).getroot())
        # Write the report
        etree.ElementTree(testsuites).write(self.final_report)

    def set_up(self) -> None:
        self.results_dir = self.working_dir("surefire-reports")
        self.final_report = self.working_dir("final_report.xml")
        pass

    def run(self) -> None:
        argv = [
            self.env.options.maven_executable or "mvn",
            "test",
            "-q",
            "-f",
            self.gnatpolyglot_java_root_dir,
            # Do not print the summary.
            f"-DprintSummary=false",
            # Custom argument to change Surefire's report directory.
            f"-Dmaven.surefire.reportsDirectory={self.results_dir}",
            # If a test fails, maven will exit with != 0, ending the
            # current test and preventing the analysis of the test results.
            f"-Dmaven.test.failure.ignore=true",
        ]

        if self.env.options.maven_local_repo is not None:
            argv.append(
                "-Dmaven.repo.local=" + self.env.options.maven_local_repo
            )
        if self.env.options.lal_version is not None:
            argv.append(
                "-Dconfig.libadalang.version=" + self.env.options.lal_version
            )

        # Run the Junit testsuites
        self.shell(argv)

        # Aggregate all the results.
        #
        # Maven Surefire XML reports are missing the <testsuites> tags as the
        # root of the file. We aggregate all the xml files into a single one
        # for simplicity and to add the <testsuites> tag.
        self.aggregate_junit_results()

    def analyze(self) -> None:
        # Import the JUnit test results into an e3.testsuite ReportIndex
        mkdir(self.working_dir("results_index"))
        index = ReportIndex(self.working_dir("results_index"))
        importer = XUnitImporterCustomTestNaming(index)
        importer.run(str(self.final_report))

        # Push individual e3.testsuite results for each JUnit test result
        for test_entry in index.entries.values():
            test_result = test_entry.load()
            self.push_result(test_result)


class XUnitImporterCustomTestNaming(XUnitImporter):
    def get_test_name(
        self,
        testsuite_name: str,
        testcase_name: str,
        classname: str | None = None,
    ) -> str:
        """
        Override the naming scheme to prevent duplication of class paths
        resulting from ``testsuite_name`` always being the prefix of
        ``classname``.
        """
        return super().get_test_name("", testcase_name, classname)
