import type { Parsed } from "./util";

// Caps from docs/PUSH.md 3.6. Changing them is a contract change with PiFire.
export const MAX_REGISTER_BODY_BYTES = 2048;
export const MAX_SEND_BODY_BYTES = 4096;
const MAX_DATA_BYTES = 2048;
const MAX_DATA_KEYS = 16;
const MAX_FALLBACK_TITLE = 100;
const MAX_FALLBACK_BODY = 500;

const PUSH_KEY = /^pfk_[A-Za-z0-9_-]{43}$/;
const FCM_TOKEN = /^[A-Za-z0-9_:.-]{1,1024}$/;
const ID = /^[A-Za-z0-9-]{1,64}$/;
const TYPE = /^[a-z0-9_]{1,32}$/;
const DATA_KEY = /^[a-z0-9_]{1,32}$/;

export type DataValue = string | number | boolean | null;

export interface SendRequest {
  pushKey: string;
  alertId: string;
  type: string;
  serverUuid: string;
  time: number;
  data?: Record<string, DataValue>;
  fallback?: { title?: string; body?: string };
}

type Fields = Record<string, unknown>;

const invalid = { ok: false, error: "invalid_request" } as const;
const tooLarge = { ok: false, error: "payload_too_large" } as const;

function isObject(value: unknown): value is Fields {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function matches(value: unknown, pattern: RegExp): value is string {
  return typeof value === "string" && pattern.test(value);
}

export function parseRegister(body: unknown): Parsed<{ fcmToken: string }> {
  if (!isObject(body) || !matches(body.fcm_token, FCM_TOKEN)) return invalid;
  return { ok: true, value: { fcmToken: body.fcm_token } };
}

export function parseUpdate(body: unknown): Parsed<{ pushKey: string; fcmToken: string }> {
  if (!isObject(body) || !matches(body.push_key, PUSH_KEY) || !matches(body.fcm_token, FCM_TOKEN)) {
    return invalid;
  }
  return { ok: true, value: { pushKey: body.push_key, fcmToken: body.fcm_token } };
}

export function parseDelete(body: unknown): Parsed<{ pushKey: string }> {
  if (!isObject(body) || !matches(body.push_key, PUSH_KEY)) return invalid;
  return { ok: true, value: { pushKey: body.push_key } };
}

export function parseSend(body: unknown): Parsed<SendRequest> {
  if (!isObject(body)) return invalid;
  const { push_key, alert_id, type, server_uuid, time } = body;
  if (
    !matches(push_key, PUSH_KEY) ||
    !matches(alert_id, ID) ||
    !matches(type, TYPE) ||
    !matches(server_uuid, ID) ||
    typeof time !== "number" ||
    !Number.isSafeInteger(time) ||
    time <= 0
  ) {
    return invalid;
  }
  const request: SendRequest = { pushKey: push_key, alertId: alert_id, type, serverUuid: server_uuid, time };

  if (body.data !== undefined && body.data !== null) {
    const data = parseData(body.data);
    if (!data.ok) return data;
    request.data = data.value;
  }
  if (body.fallback !== undefined && body.fallback !== null) {
    const fallback = parseFallback(body.fallback);
    if (!fallback.ok) return fallback;
    request.fallback = fallback.value;
  }
  return { ok: true, value: request };
}

function parseData(value: unknown): Parsed<Record<string, DataValue>> {
  if (!isObject(value)) return invalid;
  const entries = Object.entries(value);
  if (entries.length > MAX_DATA_KEYS) return tooLarge;
  for (const [key, item] of entries) {
    if (!DATA_KEY.test(key)) return invalid;
    const isScalar =
      item === null ||
      typeof item === "string" ||
      typeof item === "boolean" ||
      (typeof item === "number" && Number.isFinite(item));
    if (!isScalar) return invalid;
  }
  if (new TextEncoder().encode(JSON.stringify(value)).byteLength > MAX_DATA_BYTES) return tooLarge;
  return { ok: true, value: value as Record<string, DataValue> };
}

function parseFallback(value: unknown): Parsed<{ title?: string; body?: string }> {
  if (!isObject(value)) return invalid;
  const { title, body } = value;
  if ((title !== undefined && typeof title !== "string") || (body !== undefined && typeof body !== "string")) {
    return invalid;
  }
  if ((title?.length ?? 0) > MAX_FALLBACK_TITLE || (body?.length ?? 0) > MAX_FALLBACK_BODY) return tooLarge;
  return { ok: true, value: { title, body } };
}
