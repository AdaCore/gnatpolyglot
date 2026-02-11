# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

import time
from pathlib import Path

# -- General configuration ---------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#general-configuration

extensions = [
    "sphinx.ext.viewcode",
    "sphinx_rtd_theme",
    "myst_parser",
]

templates_path = ["_templates"]

# The suffix of source filenames.
source_suffix = {".rst" : "restructuredtext",
                 ".md" : "markdown",
                 }

# The master toctree document.
master_doc = "index"

# -- Project information -----------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#project-information

project = "polyglot"

def get_version():
    version_file = Path(__file__).parents[1] / "VERSION"
    if version_file.exists():
        with version_file.open() as f:
            return f.readline().strip()

    # If there is no VERSION file, get the current date
    return time.strftime("%Y%m%d")

version = get_version()
release = version

def get_copyright():
    return f"2023-{time.strftime('%Y')}, AdaCore"

copyright = get_copyright()

author = "AdaCore"


exclude_patterns = ["_build"]

# -- Options for HTML output -------------------------------------------------
# https://www.sphinx-doc.org/en/master/usage/configuration.html#options-for-html-output

html_theme = "sphinx_rtd_theme"

html_theme_options = {
    # Use AdaCore blue in the Table Of Content
    "style_nav_header_background": "#12284c",
}
html_static_path = ["_static"]

html_logo = "adacore-logo-white.png"

# The name of an image file (within the static path) to use as favicon of the
# docs.  This file should be a Windows icon file (.ico) being 16x16 or 32x32
# pixels large.
html_favicon = "favicon.ico"

htmlhelp_basename = "polyglotdoc"

# -- Options for LaTeX output -------------------------------------------------

latex_documents = [
    ("index", "polyglot.tex", "polyglot Documentation", "AdaCore", "manual"),
]

# -- Options for manual page output -------------------------------------------

man_pages = [("index", "polyglot", "polyglot Documentation", ["AdaCore"], 1)]

# -- Options for Epub output --------------------------------------------------

# Bibliographic Dublin Core info.
epub_title = "polyglot"
epub_author = "AdaCore"
epub_publisher = "AdaCore"
epub_copyright = get_copyright()
