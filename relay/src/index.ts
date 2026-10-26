import type { Env } from "./env";
import { register, unregister, updateRegistration } from "./register";
import { send } from "./send";
import { fail, log, ok } from "./util";
import { RELAY_VERSION } from "./version";

// Workers treats every named export of this module as an entrypoint: export only the default handler.

type Handler = (request: Request, env: Env) => Promise<Response>;

// Maps, not object literals, so a path or method like `__proto__` never matches an inherited key.
const routes = new Map<string, Map<string, Handler>>([
  [
    "/v1/register",
    new Map([
      ["POST", register],
      ["PUT", updateRegistration],
      ["DELETE", unregister],
    ]),
  ],
  ["/v1/send", new Map([["POST", send]])],
  ["/v1/health", new Map([["GET", async () => ok({ version: RELAY_VERSION })]])],
]);

export default {
  async fetch(request, env): Promise<Response> {
    const methods = routes.get(new URL(request.url).pathname);
    if (!methods) return fail("not_found");
    const handler = methods.get(request.method);
    if (!handler) return fail("method_not_allowed");
    try {
      return await handler(request, env);
    } catch (error) {
      log("internal_error", { message: error instanceof Error ? error.message : String(error) });
      return fail("internal_error");
    }
  },
} satisfies ExportedHandler<Env>;
