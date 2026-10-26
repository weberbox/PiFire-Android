import { env } from "cloudflare:test";
import { afterEach, beforeAll, beforeEach, describe, expect, it, vi } from "vitest";
import type { Env, PushKeyRecord } from "../src/env";
import { resetFcmCache } from "../src/fcm";
import * as entry from "../src/index";
import worker from "../src/index";
import { sha256Hex } from "../src/util";
import { RELAY_VERSION } from "../src/version";
import { testServiceAccount } from "./helpers";

const TOKEN = "dGVzdA:APA91bExample_token-123";
const NEW_TOKEN = "dGVzdA:APA91bExample_token-456";
const FCM_URL = "https://fcm.googleapis.com/v1/projects/test-project/messages:send";

let testEnv: Env;
let fcmResponses: Response[];
let fcmBodies: { message: Record<string, unknown>; validate_only: boolean }[];
let tokenRequests: number;
let ipCounter = 0;

type IncomingRequest = Parameters<typeof worker.fetch>[0];

function fcmError(status: number, errorCode: string): Response {
  return Response.json(
    {
      error: {
        code: status,
        status: errorCode,
        details: [{ "@type": "type.googleapis.com/google.firebase.fcm.v1.FcmError", errorCode }],
      },
    },
    { status },
  );
}

// Each call gets its own IP so the register limiter (3/min) never spans tests.
function call(method: string, path: string, body?: unknown, ip = `198.51.100.${++ipCounter}`): Promise<Response> {
  const request: IncomingRequest = new Request(`https://relay.example.com${path}`, {
    method,
    headers: { "content-type": "application/json", "cf-connecting-ip": ip },
    body: body === undefined ? undefined : typeof body === "string" ? body : JSON.stringify(body),
  });
  return worker.fetch(request, testEnv);
}

async function registerKey(token = TOKEN): Promise<string> {
  fcmResponses.push(Response.json({ name: "validated" }));
  const response = await call("POST", "/v1/register", { fcm_token: token });
  expect(response.status).toBe(201);
  return ((await response.json()) as { push_key: string }).push_key;
}

async function storedRecord(pushKey: string): Promise<PushKeyRecord | null> {
  return env.PUSH_KEYS.get<PushKeyRecord>(await sha256Hex(pushKey), "json");
}

function alert(pushKey: string, extra: Record<string, unknown> = {}) {
  return {
    push_key: pushKey,
    alert_id: "0b8e5c1e-6d0a-4c39-9a8f-1f4f0f8b2a11",
    type: "probe_target",
    server_uuid: "6f1d2b8a-3c4e-4f5a-8b9c-0d1e2f3a4b5c",
    time: 1761400000,
    ...extra,
  };
}

beforeAll(async () => {
  const { account } = await testServiceAccount();
  testEnv = { ...env, FCM_SERVICE_ACCOUNT: JSON.stringify(account) };
});

beforeEach(() => {
  resetFcmCache();
  fcmResponses = [];
  fcmBodies = [];
  tokenRequests = 0;
  vi.spyOn(globalThis, "fetch").mockImplementation(async (input, init) => {
    const url = input instanceof Request ? input.url : String(input);
    if (url === "https://oauth2.googleapis.com/token") {
      tokenRequests++;
      return Response.json({ access_token: `access-${tokenRequests}`, expires_in: 3600 });
    }
    if (url === FCM_URL) {
      fcmBodies.push(JSON.parse(String(init?.body)));
      return fcmResponses.shift() ?? Response.json({ name: "sent" });
    }
    throw new Error(`Unexpected fetch to ${url}`);
  });
});

afterEach(() => {
  vi.restoreAllMocks();
});

describe("routing", () => {
  it("exports only the default handler, as Workers requires", () => {
    expect(Object.keys(entry)).toEqual(["default"]);
  });

  it("returns the version from health", async () => {
    const response = await call("GET", "/v1/health");
    expect(response.status).toBe(200);
    expect(await response.json()).toEqual({ ok: true, version: RELAY_VERSION });
  });

  it("rejects unknown paths and methods, including inherited keys", async () => {
    expect((await call("GET", "/v1/nope")).status).toBe(404);
    expect((await call("GET", "/__proto__")).status).toBe(404);
    expect((await call("GET", "/v1/send")).status).toBe(405);
    expect(await (await call("PATCH", "/v1/register")).json()).toEqual({ ok: false, error: "method_not_allowed" });
  });
});

describe("register", () => {
  it("validates the token with FCM, then stores it under the key hash", async () => {
    const pushKey = await registerKey();

    expect(pushKey).toMatch(/^pfk_[A-Za-z0-9_-]{43}$/);
    expect(fcmBodies).toEqual([{ message: { token: TOKEN }, validate_only: true }]);
    expect((await storedRecord(pushKey))?.fcm_token).toBe(TOKEN);
    expect(await env.PUSH_KEYS.get(pushKey)).toBeNull();
  });

  it("rejects a token FCM doesn't accept without writing", async () => {
    const keysBefore = (await env.PUSH_KEYS.list()).keys.length;
    fcmResponses.push(fcmError(400, "INVALID_ARGUMENT"));
    const response = await call("POST", "/v1/register", { fcm_token: NEW_TOKEN });

    expect(response.status).toBe(400);
    expect(await response.json()).toEqual({ ok: false, error: "invalid_token" });
    expect((await env.PUSH_KEYS.list()).keys).toHaveLength(keysBefore);
  });

  it("reports FCM outages as fcm_error", async () => {
    fcmResponses.push(fcmError(503, "UNAVAILABLE"));
    const response = await call("POST", "/v1/register", { fcm_token: TOKEN });
    expect(response.status).toBe(502);
  });

  it("rate limits registrations per IP", async () => {
    const statuses: number[] = [];
    for (let i = 0; i < 5; i++) {
      statuses.push((await call("POST", "/v1/register", { fcm_token: TOKEN }, "203.0.113.99")).status);
    }
    expect(statuses.slice(0, 3)).toEqual([201, 201, 201]);
    expect(statuses.at(-1)).toBe(429);
  });

  it("updates the token for an existing key", async () => {
    const pushKey = await registerKey();
    const response = await call("PUT", "/v1/register", { push_key: pushKey, fcm_token: NEW_TOKEN });

    expect(response.status).toBe(200);
    const record = await storedRecord(pushKey);
    expect(record?.fcm_token).toBe(NEW_TOKEN);
  });

  it("skips FCM and the write when the token is unchanged", async () => {
    const pushKey = await registerKey();
    fcmBodies = [];
    const response = await call("PUT", "/v1/register", { push_key: pushKey, fcm_token: TOKEN });

    expect(response.status).toBe(200);
    expect(fcmBodies).toHaveLength(0);
  });

  it("returns unknown_key when updating a key that doesn't exist", async () => {
    const response = await call("PUT", "/v1/register", { push_key: `pfk_${"A".repeat(43)}`, fcm_token: TOKEN });
    expect(response.status).toBe(404);
  });

  it("deletes a key, and succeeds again when it's already gone", async () => {
    const pushKey = await registerKey();

    expect((await call("DELETE", "/v1/register", { push_key: pushKey })).status).toBe(200);
    expect(await storedRecord(pushKey)).toBeNull();
    expect((await call("DELETE", "/v1/register", { push_key: pushKey })).status).toBe(200);
  });
});

describe("send", () => {
  it("forwards the alert as a high-priority data message", async () => {
    const pushKey = await registerKey();
    const response = await call(
      "POST",
      "/v1/send",
      alert(pushKey, {
        data: { probe_label: "Probe1", probe_name: "Brisket", target: 203, units: "F" },
        fallback: { title: "Brisket Target Achieved", body: "Brisket target of 203F achieved" },
      }),
    );

    expect(response.status).toBe(200);
    expect(fcmBodies.at(-1)).toEqual({
      validate_only: false,
      message: {
        token: TOKEN,
        android: { priority: "high", ttl: "900s" },
        data: {
          alert_id: "0b8e5c1e-6d0a-4c39-9a8f-1f4f0f8b2a11",
          type: "probe_target",
          server_uuid: "6f1d2b8a-3c4e-4f5a-8b9c-0d1e2f3a4b5c",
          time: "1761400000",
          data: '{"probe_label":"Probe1","probe_name":"Brisket","target":203,"units":"F"}',
          fallback_title: "Brisket Target Achieved",
          fallback_body: "Brisket target of 203F achieved",
        },
      },
    });
  });

  it("omits data and fallback when PiFire sends none", async () => {
    const pushKey = await registerKey();
    await call("POST", "/v1/send", alert(pushKey, { type: "timer_expired" }));

    expect(Object.keys(fcmBodies.at(-1)?.message.data as object)).toEqual([
      "alert_id",
      "type",
      "server_uuid",
      "time",
    ]);
  });

  it("deletes the key and returns 410 when FCM says the token is gone", async () => {
    const pushKey = await registerKey();
    fcmResponses.push(fcmError(404, "UNREGISTERED"));
    const response = await call("POST", "/v1/send", alert(pushKey));

    expect(response.status).toBe(410);
    expect(await response.json()).toEqual({ ok: false, error: "unregistered" });
    expect(await storedRecord(pushKey)).toBeNull();
  });

  it("refreshes the access token once on 401", async () => {
    const pushKey = await registerKey();
    fcmResponses.push(new Response(null, { status: 401 }));
    const response = await call("POST", "/v1/send", alert(pushKey));

    expect(response.status).toBe(200);
    expect(tokenRequests).toBe(2);
  });

  it("reuses the cached access token", async () => {
    const pushKey = await registerKey();
    await call("POST", "/v1/send", alert(pushKey));
    await call("POST", "/v1/send", alert(pushKey));
    expect(tokenRequests).toBe(1);
  });

  it("returns fcm_error when the OAuth exchange fails", async () => {
    const pushKey = await registerKey();
    resetFcmCache();
    vi.mocked(fetch).mockImplementationOnce(async () => new Response("denied", { status: 400 }));
    const response = await call("POST", "/v1/send", alert(pushKey));
    expect(response.status).toBe(502);
  });

  it("returns unknown_key for a key that was never registered", async () => {
    const response = await call("POST", "/v1/send", alert(`pfk_${"B".repeat(43)}`));
    expect(response.status).toBe(404);
  });

  it("rejects malformed and oversized bodies", async () => {
    expect((await call("POST", "/v1/send", "{not json")).status).toBe(400);
    expect((await call("POST", "/v1/send", { type: "probe_target" })).status).toBe(400);
    const large = await call("POST", "/v1/send", alert(`pfk_${"C".repeat(43)}`, { padding: "x".repeat(5000) }));
    expect(large.status).toBe(413);
  });

  it("rate limits sends per key", async () => {
    const pushKey = await registerKey();
    const statuses: number[] = [];
    for (let i = 0; i < 22; i++) statuses.push((await call("POST", "/v1/send", alert(pushKey))).status);

    expect(statuses.slice(0, 20).every((status) => status === 200)).toBe(true);
    expect(statuses.at(-1)).toBe(429);
  });
});
