// Deploys with the KV namespace id from deploy.local.json (gitignored), so the id never lands in
// the committed wrangler.toml. Extra arguments go to `wrangler deploy` (e.g. --dry-run).
import { spawnSync } from "node:child_process";
import { existsSync, readFileSync, rmSync, writeFileSync } from "node:fs";

const PLACEHOLDER = "__PUSH_KEYS_NAMESPACE_ID__";
const LOCAL_CONFIG = "deploy.local.json";
const DEPLOY_CONFIG = "wrangler.deploy.toml";

if (!existsSync(LOCAL_CONFIG)) {
  console.error(`Missing ${LOCAL_CONFIG}. Create it with {"kvNamespaceId": "<id>"} (see README.md).`);
  process.exit(1);
}
const { kvNamespaceId } = JSON.parse(readFileSync(LOCAL_CONFIG, "utf8"));
if (!/^[0-9a-f]{32}$/.test(kvNamespaceId ?? "")) {
  console.error(`${LOCAL_CONFIG}: kvNamespaceId must be the 32-character hex id from \`wrangler kv namespace create\`.`);
  process.exit(1);
}
const config = readFileSync("wrangler.toml", "utf8");
if (!config.includes(PLACEHOLDER)) {
  console.error(`wrangler.toml no longer contains ${PLACEHOLDER}.`);
  process.exit(1);
}

writeFileSync(DEPLOY_CONFIG, config.replace(PLACEHOLDER, kvNamespaceId));
try {
  const wrangler = "node_modules/wrangler/bin/wrangler.js";
  const result = spawnSync(process.execPath, [wrangler, "deploy", "--config", DEPLOY_CONFIG, ...process.argv.slice(2)], {
    stdio: "inherit",
  });
  process.exitCode = result.status ?? 1;
} finally {
  rmSync(DEPLOY_CONFIG, { force: true });
}
