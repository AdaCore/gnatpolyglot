from utils import run_polyglot, list_generated_sources


# Check that running polyglot with a project that does not exist raises a
# proper error.
run_polyglot(
    "ada2proxy",
    ["-P", "/dummy/missing.gpr", "-o", "proxy"],
    expect_returncode=1
)

# Check that running polyglot with a project containing a dependency that does
# not exist raises a proper error.
res = run_polyglot(
    "ada2proxy",
    ["-P", "test.gpr", "-o", "proxy"],
    expect_returncode=1,
    pipe=True
)
# Only show the three lines, as what follows "The following directories have
# been searched: ..." is environment-dependant.
print("\n".join(res.splitlines()[:3]))
