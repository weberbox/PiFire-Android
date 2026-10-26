import type { Env, PushKeyRecord } from "./env";
import { sendFcm } from "./fcm";
import { MAX_REGISTER_BODY_BYTES, parseDelete, parseRegister, parseUpdate } from "./validate";
import {
  clientIp,
  type ErrorCode,
  fail,
  generatePushKey,
  keyTag,
  log,
  nowSeconds,
  ok,
  readJsonBody,
  sha256Hex,
} from "./util";

// Every route here writes to KV (1,000 writes/day on the free plan), so all are limited per IP.
async function ipLimited(request: Request, env: Env): Promise<boolean> {
  const { success } = await env.REGISTER_LIMITER.limit({ key: clientIp(request) });
  if (!success) log("rate_limited", { route: "register" });
  return !success;
}

/** Checks the token with FCM before it costs a KV write. */
async function checkToken(env: Env, fcmToken: string): Promise<ErrorCode | undefined> {
  const result = await sendFcm(env, { token: fcmToken }, true);
  if (result === "ok") return undefined;
  return result === "error" ? "fcm_error" : "invalid_token";
}

/** POST: new push_key for an FCM token. */
export async function register(request: Request, env: Env): Promise<Response> {
  if (await ipLimited(request, env)) return fail("rate_limited");
  const body = await readJsonBody(request, MAX_REGISTER_BODY_BYTES);
  if (!body.ok) return fail(body.error);
  const parsed = parseRegister(body.value);
  if (!parsed.ok) return fail(parsed.error);

  const tokenError = await checkToken(env, parsed.value.fcmToken);
  if (tokenError) return fail(tokenError);

  const pushKey = generatePushKey();
  const keyHash = await sha256Hex(pushKey);
  const now = nowSeconds();
  const record: PushKeyRecord = { fcm_token: parsed.value.fcmToken, created: now, updated: now };
  await env.PUSH_KEYS.put(keyHash, JSON.stringify(record));
  log("registered", { key: keyTag(keyHash) });
  return ok({ push_key: pushKey }, 201);
}

/** PUT: new FCM token for an existing push_key, so PiFire servers keep working after a token refresh. */
export async function updateRegistration(request: Request, env: Env): Promise<Response> {
  if (await ipLimited(request, env)) return fail("rate_limited");
  const body = await readJsonBody(request, MAX_REGISTER_BODY_BYTES);
  if (!body.ok) return fail(body.error);
  const parsed = parseUpdate(body.value);
  if (!parsed.ok) return fail(parsed.error);

  const keyHash = await sha256Hex(parsed.value.pushKey);
  const existing = await env.PUSH_KEYS.get<PushKeyRecord>(keyHash, "json");
  if (!existing) return fail("unknown_key");
  // The app may call this on every start; an unchanged token costs no write.
  if (existing.fcm_token === parsed.value.fcmToken) return ok();

  const tokenError = await checkToken(env, parsed.value.fcmToken);
  if (tokenError) return fail(tokenError);

  const record: PushKeyRecord = { ...existing, fcm_token: parsed.value.fcmToken, updated: nowSeconds() };
  await env.PUSH_KEYS.put(keyHash, JSON.stringify(record));
  log("token_updated", { key: keyTag(keyHash) });
  return ok();
}

/** DELETE: forget a push_key. Succeeds when it's already gone. */
export async function unregister(request: Request, env: Env): Promise<Response> {
  if (await ipLimited(request, env)) return fail("rate_limited");
  const body = await readJsonBody(request, MAX_REGISTER_BODY_BYTES);
  if (!body.ok) return fail(body.error);
  const parsed = parseDelete(body.value);
  if (!parsed.ok) return fail(parsed.error);

  const keyHash = await sha256Hex(parsed.value.pushKey);
  await env.PUSH_KEYS.delete(keyHash);
  log("unregistered", { key: keyTag(keyHash) });
  return ok();
}
