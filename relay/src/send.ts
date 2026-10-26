import type { Env, PushKeyRecord } from "./env";
import { type FcmMessage, sendFcm } from "./fcm";
import { MAX_SEND_BODY_BYTES, parseSend, type SendRequest } from "./validate";
import { fail, keyTag, log, ok, readJsonBody, sha256Hex } from "./util";

// A briefly offline phone still gets the alert; a stale one is dropped instead of arriving late.
const ALERT_TTL = "900s";

/** POST from PiFire: forward one alert to the phone behind a push_key. */
export async function send(request: Request, env: Env): Promise<Response> {
  const body = await readJsonBody(request, MAX_SEND_BODY_BYTES);
  if (!body.ok) return fail(body.error);
  const parsed = parseSend(body.value);
  if (!parsed.ok) return fail(parsed.error);
  const alert = parsed.value;

  // Limit before the KV read, so a retry loop can't use up the read budget either.
  const keyHash = await sha256Hex(alert.pushKey);
  const { success } = await env.SEND_LIMITER.limit({ key: keyHash });
  if (!success) {
    log("rate_limited", { route: "send", key: keyTag(keyHash) });
    return fail("rate_limited");
  }

  const record = await env.PUSH_KEYS.get<PushKeyRecord>(keyHash, "json");
  if (!record) return fail("unknown_key");

  const result = await sendFcm(env, buildMessage(record.fcm_token, alert));
  switch (result) {
    case "ok":
      log("sent", { key: keyTag(keyHash), type: alert.type });
      return ok();
    case "unregistered":
      await env.PUSH_KEYS.delete(keyHash);
      log("token_unregistered", { key: keyTag(keyHash) });
      return fail("unregistered");
    default:
      return fail("fcm_error");
  }
}

/** FCM data values must be strings; `data` is nested as JSON so its keys never clash with ours or FCM's. */
export function buildMessage(fcmToken: string, alert: SendRequest): FcmMessage {
  const data: Record<string, string> = {
    alert_id: alert.alertId,
    type: alert.type,
    server_uuid: alert.serverUuid,
    time: String(alert.time),
  };
  if (alert.data) data.data = JSON.stringify(alert.data);
  if (alert.fallback?.title) data.fallback_title = alert.fallback.title;
  if (alert.fallback?.body) data.fallback_body = alert.fallback.body;
  return { token: fcmToken, android: { priority: "high", ttl: ALERT_TTL }, data };
}
