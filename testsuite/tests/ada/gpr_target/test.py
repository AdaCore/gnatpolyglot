import os
import json
from utils import run_scanner


def run_for_target(target):
    run_scanner("ada", "test.gpr", "proxy", [f"--target={target}"])
    with open(os.path.join("proxy", "proxy.json")) as f:
        test_p = json.load(f).get("modules")[0].get("declarations")[0].get("type")
        print("=" * 40)
        print(f"Target {target}:")
        print(json.dumps(test_p, indent=1))


run_for_target("x86_64-linux")
run_for_target("x86-linux")
run_for_target("x86_64-windows")
run_for_target("x86-windows")
