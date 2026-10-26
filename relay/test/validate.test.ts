import { describe, expect, it } from "vitest";
import { parseDelete, parseRegister, parseSend, parseUpdate } from "../src/validate";
import { validPushKey } from "./helpers";

const alert = {
  push_key: validPushKey,
  alert_id: "0b8e5c1e-6d0a-4c39-9a8f-1f4f0f8b2a11",
  type: "probe_target",
  server_uuid: "6f1d2b8a-3c4e-4f5a-8b9c-0d1e2f3a4b5c",
  time: 1761400000,
};

describe("parseSend", () => {
  it("accepts a full alert and ignores unknown fields", () => {
    const result = parseSend({
      ...alert,
      data: { probe_label: "Probe1", target: 203.5, enabled: true, note: null },
      fallback: { title: "Target", body: "Reached" },
      added_later: "ignored",
    });
    expect(result).toEqual({
      ok: true,
      value: {
        pushKey: validPushKey,
        alertId: alert.alert_id,
        type: "probe_target",
        serverUuid: alert.server_uuid,
        time: 1761400000,
        data: { probe_label: "Probe1", target: 203.5, enabled: true, note: null },
        fallback: { title: "Target", body: "Reached" },
      },
    });
  });

  it("accepts null data and fallback as absent", () => {
    const result = parseSend({ ...alert, data: null, fallback: null });
    expect(result.ok && result.value.data).toBeUndefined();
  });

  it.each([
    ["missing push_key", { ...alert, push_key: undefined }],
    ["malformed push_key", { ...alert, push_key: "pfk_short" }],
    ["uppercase type", { ...alert, type: "Probe_Target" }],
    ["string time", { ...alert, time: "1761400000" }],
    ["fractional time", { ...alert, time: 1.5 }],
    ["nested data", { ...alert, data: { probe: { label: "Probe1" } } }],
    ["array data", { ...alert, data: ["Probe1"] }],
    ["bad data key", { ...alert, data: { "Probe Label": "x" } }],
    ["non-finite number", { ...alert, data: { target: Number.NaN } }],
    ["numeric fallback title", { ...alert, fallback: { title: 1 } }],
    ["not an object", "probe_target"],
  ])("rejects %s", (_, body) => {
    expect(parseSend(body)).toEqual({ ok: false, error: "invalid_request" });
  });

  it.each([
    ["too many data keys", { ...alert, data: Object.fromEntries(Array.from({ length: 17 }, (_, i) => [`k${i}`, i])) }],
    ["data over 2 KB", { ...alert, data: { message: "x".repeat(2100) } }],
    ["long fallback title", { ...alert, fallback: { title: "x".repeat(101) } }],
    ["long fallback body", { ...alert, fallback: { body: "x".repeat(501) } }],
  ])("rejects %s as too large", (_, body) => {
    expect(parseSend(body)).toEqual({ ok: false, error: "payload_too_large" });
  });
});

describe("register bodies", () => {
  const token = "dGVzdA:APA91bExample_token-123";

  it("parses register, update and delete", () => {
    expect(parseRegister({ fcm_token: token })).toEqual({ ok: true, value: { fcmToken: token } });
    expect(parseUpdate({ push_key: validPushKey, fcm_token: token })).toEqual({
      ok: true,
      value: { pushKey: validPushKey, fcmToken: token },
    });
    expect(parseDelete({ push_key: validPushKey })).toEqual({ ok: true, value: { pushKey: validPushKey } });
  });

  it("rejects bad tokens and keys", () => {
    expect(parseRegister({ fcm_token: "" }).ok).toBe(false);
    expect(parseRegister({ fcm_token: "has space" }).ok).toBe(false);
    expect(parseRegister({ fcm_token: "x".repeat(1025) }).ok).toBe(false);
    expect(parseUpdate({ push_key: "nope", fcm_token: token }).ok).toBe(false);
    expect(parseDelete({}).ok).toBe(false);
  });
});
