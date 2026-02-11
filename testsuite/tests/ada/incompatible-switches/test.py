from utils import run_polyglot, list_generated_sources


# Check that --spec-files is incompatible with --no-subprojects
run_polyglot(
    "ada2proxy",
    ["-P", "test.gpr", "--spec-files", "test.ads", "--no-subprojects", "-o", "proxy"],
    expect_returncode=1
)

# Check that --spec-files is incompatible with --process-runtime
run_polyglot(
    "ada2proxy",
    ["-P", "test.gpr", "--spec-files", "test.ads", "--process-runtime", "-o", "proxy"],
    expect_returncode=1
)

# Check that --no-subprojects is incompatible with --process-runtime
run_polyglot(
    "ada2proxy",
    ["-P", "test.gpr", "--no-subprojects", "--process-runtime", "-o", "proxy"],
    expect_returncode=1
)
