import type { Env } from "./env";
import { base64UrlEncode, log } from "./util";

const TOKEN_URL = "https://oauth2.googleapis.com/token";
const SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
const TOKEN_LIFETIME_SECONDS = 3600;
// Refresh a minute early so a token never expires between the check and the send.
const TOKEN_REFRESH_MARGIN_MS = 60_000;

export interface FcmMessage {
  token: string;
  android?: { priority: "high" | "normal"; ttl: string };
  data?: Record<string, string>;
}

/**
 * - `unregistered`: the token is dead (app uninstalled, data cleared) or belongs to another
 *   Firebase project; drop it.
 * - `invalid_argument`: FCM rejected the token or the message.
 * - `error`: auth, quota or server trouble; worth a retry later.
 */
export type FcmResult = "ok" | "unregistered" | "invalid_argument" | "error";

export interface ServiceAccount {
  project_id: string;
  client_email: string;
  private_key: string;
}

// Per isolate. Not KV: the token would cost a KV write every hour.
let cachedToken: { value: string; expiresAt: number } | undefined;
let pendingToken: Promise<string> | undefined;

/** Clears the access token cache; for tests. */
export function resetFcmCache(): void {
  cachedToken = undefined;
  pendingToken = undefined;
}

export async function sendFcm(env: Env, message: FcmMessage, validateOnly = false): Promise<FcmResult> {
  let response: Response;
  try {
    const account = parseServiceAccount(env.FCM_SERVICE_ACCOUNT);
    response = await postMessage(account, message, validateOnly);
    if (response.status === 401) {
      // Google can revoke a token before its expiry; fetch a new one and retry once.
      cachedToken = undefined;
      response = await postMessage(account, message, validateOnly);
    }
  } catch (error) {
    // Our own error messages and fetch failures carry no secrets.
    log("fcm_failed", { message: error instanceof Error ? error.message : String(error) });
    return "error";
  }
  if (response.ok) return "ok";

  const code = await fcmErrorCode(response);
  log("fcm_error", { status: response.status, code, validateOnly });
  switch (code) {
    case "UNREGISTERED":
    case "SENDER_ID_MISMATCH":
      return "unregistered";
    case "INVALID_ARGUMENT":
      return "invalid_argument";
    default:
      return "error";
  }
}

async function postMessage(account: ServiceAccount, message: FcmMessage, validateOnly: boolean): Promise<Response> {
  const url = `https://fcm.googleapis.com/v1/projects/${account.project_id}/messages:send`;
  return fetch(url, {
    method: "POST",
    headers: {
      authorization: `Bearer ${await accessToken(account)}`,
      "content-type": "application/json",
    },
    body: JSON.stringify({ message, validate_only: validateOnly }),
  });
}

async function fcmErrorCode(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as {
      error?: { status?: string; details?: { "@type"?: string; errorCode?: string }[] };
    };
    const detail = body.error?.details?.find((item) => item["@type"]?.endsWith("google.firebase.fcm.v1.FcmError"));
    return detail?.errorCode ?? body.error?.status ?? "UNKNOWN";
  } catch {
    return "UNKNOWN";
  }
}

function parseServiceAccount(json: string): ServiceAccount {
  // JSON.parse errors can quote the input, which holds the private key: never rethrow them.
  let account: Partial<ServiceAccount>;
  try {
    account = JSON.parse(json) as Partial<ServiceAccount>;
  } catch {
    throw new Error("FCM_SERVICE_ACCOUNT is not valid JSON");
  }
  if (!account.project_id || !account.client_email || !account.private_key) {
    throw new Error("FCM_SERVICE_ACCOUNT is missing project_id, client_email or private_key");
  }
  return account as ServiceAccount;
}

async function accessToken(account: ServiceAccount): Promise<string> {
  if (cachedToken && cachedToken.expiresAt - TOKEN_REFRESH_MARGIN_MS > Date.now()) return cachedToken.value;
  // Concurrent requests in one isolate share a single token fetch.
  pendingToken ??= fetchAccessToken(account).finally(() => {
    pendingToken = undefined;
  });
  return pendingToken;
}

async function fetchAccessToken(account: ServiceAccount): Promise<string> {
  const response = await fetch(TOKEN_URL, {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: await signJwt(account),
    }),
  });
  if (!response.ok) throw new Error(`OAuth token request failed with ${response.status}`);
  const body = (await response.json()) as { access_token?: string; expires_in?: number };
  if (!body.access_token) throw new Error("OAuth token response has no access_token");
  cachedToken = {
    value: body.access_token,
    expiresAt: Date.now() + (body.expires_in ?? TOKEN_LIFETIME_SECONDS) * 1000,
  };
  return body.access_token;
}

export async function signJwt(account: ServiceAccount, nowSeconds = Math.floor(Date.now() / 1000)): Promise<string> {
  const encode = (value: object) => base64UrlEncode(new TextEncoder().encode(JSON.stringify(value)));
  const unsigned = `${encode({ alg: "RS256", typ: "JWT" })}.${encode({
    iss: account.client_email,
    scope: SCOPE,
    aud: TOKEN_URL,
    iat: nowSeconds,
    exp: nowSeconds + TOKEN_LIFETIME_SECONDS,
  })}`;
  const key = await crypto.subtle.importKey(
    "pkcs8",
    pemToDer(account.private_key),
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const signature = await crypto.subtle.sign("RSASSA-PKCS1-v1_5", key, new TextEncoder().encode(unsigned));
  return `${unsigned}.${base64UrlEncode(new Uint8Array(signature))}`;
}

function pemToDer(pem: string): ArrayBuffer {
  const base64 = pem.replace(/-----(BEGIN|END) PRIVATE KEY-----/g, "").replace(/\s+/g, "");
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return bytes.buffer;
}
