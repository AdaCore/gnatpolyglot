import os
import json
from utils import run_scanner

run_scanner("ada", "test.gpr", "proxy")
with open(os.path.join("proxy", "proxy.json")) as f:
    j = json.load(f)
    module_names = [str(m.get("name")) for m in j.get("modules")]
    module_names.sort()
    print(json.dumps(module_names, indent=1))
