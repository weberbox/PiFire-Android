"""Prints GitHub release notes for a version from the bundled app assets.

Usage: release_notes.py <version>

Fails if the changelog's current entry isn't <version>, so a release can't go out with the previous
release's notes.
"""

import json
import sys
from pathlib import Path

ASSETS = Path(__file__).resolve().parents[2] / "app" / "src" / "main" / "assets"
PIFIRE_DEV_BRANCH = "https://github.com/nebhead/PiFire/tree/development"


def load(name):
    return json.loads((ASSETS / name).read_text(encoding="utf-8"))


def version_key(version, build):
    parts = [int(p) if p.isdigit() else 0 for p in (version or "0").split(".")]
    return parts + [0] * (3 - len(parts)), int(build) if str(build).isdigit() else 0


def main():
    version = sys.argv[1]

    current = next((e for e in load("changelog.json")["changelog"] if e.get("current")), None)
    if current is None or current.get("version") != version:
        found = current.get("version") if current else "none"
        sys.exit(f"changelog.json current entry is {found}, expected {version}")

    # The newest PiFire build any feature is gated on: what this release targets.
    features = load("config_feature_support.json")[0].values()
    target = max(features, key=lambda f: version_key(f["minVersion"], f["minBuild"]))

    rule = min(load("config_server_support_v2.json")["rules"], key=lambda r: r["minAppVersionCode"])
    minimum = rule["minServerInfo"]

    lines = [
        f"This release is targeted at v{target['minVersion']} Build {target['minBuild']}+ of PiFire, "
        f"minimum PiFire version required is v{minimum['version']} Build {minimum['build']}  ",
        "",
    ]
    lines += [f"- {log['text']}" for log in current["logs"]]
    lines += ["", "", f"PiFire Development branch: [here]({PIFIRE_DEV_BRANCH})"]
    print("\n".join(lines))


if __name__ == "__main__":
    main()
