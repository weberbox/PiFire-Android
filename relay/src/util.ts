export type ErrorCode =
  | "invalid_request"
  | "invalid_token"
  | "unknown_key"
  | "unregistered"
  | "payload_too_large"
  | "rate_limited"
  | "fcm_error"
  | "not_found"
  | "method_not_allowed"
  | "internal_error";

const ERROR_STATUS: Record<ErrorCode, number> = {
  invalid_request: 400,
  invalid_token: 400,
  unknown_key: 404,
  unregistered: 410,
  payload_too_large: 413,
  rate_limited: 429,
  fcm_error: 502,
  not_found: 404,
  method_not_allowed: 405,
  internal_error: 500,
};

export type Parsed<T> = { ok: true; value: T } | { ok: false; error: ErrorCode };

export function ok(fields: Record<string, unknown> = {}, status = 200): Response {
  return Response.json({ ok: true, ...fields }, { status });
}

export function fail(error: ErrorCode): Response {
  return Response.json({ ok: false, error }, { status: ERROR_STATUS[error] });
}

/** Structured log line. Never pass a push_key, FCM token or alert data; use [keyTag]. */
export function log(event: string, fields: Record<string, unknown> = {}): void {
  console.log(JSON.stringify({ event, ...fields }));
}

/** Short, non-reversible tag for a key hash, safe to log. */
export function keyTag(keyHash: string): string {
  return keyHash.slice(0, 8);
}

export function nowSeconds(): number {
  return Math.floor(Date.now() / 1000);
}

export async function sha256Hex(value: string): Promise<string> {
  const digest = await crypto.subtle.digest("SHA-256", new TextEncoder().encode(value));
  return [...new Uint8Array(digest)].map((byte) => byte.toString(16).padStart(2, "0")).join("");
}

export function base64UrlEncode(bytes: Uint8Array): string {
  let binary = "";
  for (const byte of bytes) binary += String.fromCharCode(byte);
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

/** 32 random bytes, base64url: 43 characters after the `pfk_` prefix. */
export function generatePushKey(): string {
  return `pfk_${base64UrlEncode(crypto.getRandomValues(new Uint8Array(32)))}`;
}

export async function readJsonBody(request: Request, maxBytes: number): Promise<Parsed<unknown>> {
  const declared = Number(request.headers.get("content-length") ?? "0");
  if (declared > maxBytes) return { ok: false, error: "payload_too_large" };
  const bytes = await request.arrayBuffer();
  if (bytes.byteLength > maxBytes) return { ok: false, error: "payload_too_large" };
  try {
    return { ok: true, value: JSON.parse(new TextDecoder().decode(bytes)) };
  } catch {
    return { ok: false, error: "invalid_request" };
  }
}

export function clientIp(request: Request): string {
  return request.headers.get("cf-connecting-ip") ?? "unknown";
}
