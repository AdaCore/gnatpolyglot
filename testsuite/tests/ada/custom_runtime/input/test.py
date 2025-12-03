import subprocess
import sys
import os
import json
from utils import run, compile_lib

# Compile the custom Ada runtime
compile_lib("ada", os.path.join("rts-custom", "adainclude", "libada.gpr"), ["-gnatws"])
# Compile the local polyglot runtime with the custom runtime
compile_lib(
    "ada",
    os.path.join("ada", "polyglot-ada.gpr"),
    [f"--RTS={os.path.join('..', 'rts-custom')}"]
)
subprocess.check_call([sys.executable, "-m", "polyglot_test_scanner"] + sys.argv[1:])
with open(os.path.join("proxy", "proxy.json")) as f:
    test_p = json.load(f).get("modules")[0].get("declarations")[0].get("type")
    print(json.dumps(test_p, indent=1))
