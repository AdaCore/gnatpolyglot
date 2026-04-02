#! /usr/bin/env python
"""
Python wrapper to instantiate ${ctx.config.library.language_name.camel}Ls.
"""

import os
import subprocess
import sys


if __name__ == '__main__':

    graal_home = os.environ['GRAAL_HOME']

    java = os.path.join(
        graal_home, 'bin', 'java.exe' if os.name == 'nt' else 'java'
    )

    target_dir = os.path.join(
        os.path.dirname(os.path.realpath(__file__)), "target"
    )
    class_path = os.path.join(target_dir, f'cli.jar')

    java_library_path = os.environ.get(
        "PATH" if os.name == 'nt' else 'LD_LIBRARY_PATH', ''
    )

    res = subprocess.run([
        java,
        '-cp', class_path,
        "--enable-native-access=ALL-UNNAMED",
        "--sun-misc-unsafe-memory-access=allow",
        f'-Djava.library.path={java_library_path}',
        f'com.adacore.gnatpolyglot.cli.GNATpolyglotMain',
        *sys.argv[1:]
    ])
    sys.exit(res.returncode)

