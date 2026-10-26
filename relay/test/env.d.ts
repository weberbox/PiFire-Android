import type { Env as RelayEnv } from "../src/env";

declare global {
  namespace Cloudflare {
    interface Env extends RelayEnv {}
  }
}
