# PiFire Push Notifications: Migration Plan

Replace the OneSignal v1 (unauthenticated) push flow with a self-hosted relay on **Cloudflare Workers + Firebase Cloud Messaging (FCM)**, plus a **local LAN fallback** so alerts still fire when the internet is down.

---

## 1. Problem

- OneSignal v2 requires an API key on every send. v1 didn't for targeted pushes.
- PiFire runs on users' Raspberry Pis. Neither the Pi nor the Android app is a trusted environment, so **no secrets can ship with either**.
- PiFire has no login, so there are no user accounts to authenticate against.
- Cloud push only works when the home internet is up.

## 2. Solution Overview

```
┌──────────────┐  1. register(fcm_token)   ┌─────────────────────┐
│ Android App  │ ────────────────────────▶ │ Cloudflare Worker    │
│              │ ◀──────────────────────── │ (relay, holds FCM    │
│              │     push_key              │  service account)    │
│              │                           └─────────┬───────────┘
│              │  2. push_key via websocket          │ 4. FCM HTTP v1
│              │ ─────────────┐                      ▼
│              │              ▼             ┌─────────────────────┐
│              │      ┌──────────────┐      │ Firebase Cloud      │
│              │      │ PiFire (Pi)  │      │ Messaging           │
│              │      └──────┬───────┘      └─────────┬───────────┘
│              │             │ 3. send(push_key, msg)  │
│              │             └──────▶ Worker            │
│              │ ◀──────────────────────────────────────┘ 5. push
│              │
│  Foreground  │ ◀── LAN websocket (local alerts, works w/o internet)
│  service     │
└──────────────┘
```

**Core idea: capability tokens.** The relay issues an unguessable random `push_key` per device. Holding the key is the authorization, and it can only notify that one phone. No accounts, no API keys in the app or on the Pi. The FCM service account lives only in the Worker as a secret.

## 3. Cloudflare Worker (Relay)

### 3.1 Free plan limits (verified Oct 2026)
- No credit card required; default Workers Free plan; free `*.workers.dev` subdomain.
- 100,000 requests/day; 1,000 requests/min burst; 10 ms CPU per invocation.
- KV: 100,000 reads/day, **1,000 writes/day**, 1 GB storage.
- Limits are **hard caps** (requests fail, no bill). Resets at 00:00 UTC.
- Expected load for <100 users: well under 5% of the request limit.

> ⚠️ Never write to KV per send (e.g. rate-limit counters). The 1,000 writes/day cap is the tightest limit. KV writes happen only on register/update/delete.

### 3.2 Endpoints

All bodies are JSON. All responses include `{ "ok": bool, "error"?: string }`.

| Method | Path | Called by | Body | Returns |
|---|---|---|---|---|
| `POST` | `/v1/register` | App | `{ fcm_token }` | `201 { push_key }` |
| `PUT` | `/v1/register` | App | `{ push_key, fcm_token }` | `200` (token refresh, same key) |
| `DELETE` | `/v1/register` | App | `{ push_key }` | `200` |
| `POST` | `/v1/send` | PiFire | `{ push_key, title, body, alert_id?, data? }` | `200` / `410` / `429` |
| `GET` | `/v1/health` | Anyone | – | `200` |

**Status codes for `/v1/send`:**
- `200`: accepted by FCM.
- `400`: malformed / payload too large.
- `404`: unknown push_key.
- `410`: FCM said token is `UNREGISTERED`; entry deleted. **PiFire should drop this key.**
- `429`: rate limited. PiFire should back off.
- `502`: FCM error; PiFire may retry with backoff.

### 3.3 Data model (KV namespace `PUSH_KEYS`)
- **Key:** `sha256(push_key)` as hex. The raw push_key is never stored, so a KV leak can't be used to send.
- **Value:** `{ "fcm_token": "...", "created": <epoch>, "updated": <epoch> }`

### 3.4 push_key format
- 32 random bytes from `crypto.getRandomValues`, base64url-encoded (~43 chars).
- Optionally prefixed for readability/grep: `pfk_…`

### 3.5 FCM auth from the Worker
- Store the Firebase service account JSON as a secret: `wrangler secret put FCM_SERVICE_ACCOUNT`.
- Sign an RS256 JWT with WebCrypto (`crypto.subtle.importKey` with PKCS8 + `sign`).
  - `iss` = client_email, `scope` = `https://www.googleapis.com/auth/firebase.messaging`, `aud` = `https://oauth2.googleapis.com/token`, `exp` = now + 3600.
- Exchange it at `https://oauth2.googleapis.com/token` for an access token.
- **Cache the access token** (~55 min) in a module-level variable and/or the Cache API. Don't use KV (write cap).
- Send via `POST https://fcm.googleapis.com/v1/projects/{project_id}/messages:send`.

### 3.6 FCM message shape
Prefer a **data message** with high priority so the app controls display and dedupe:
```json
{
  "message": {
    "token": "<fcm_token>",
    "android": { "priority": "high", "ttl": "300s" },
    "data": {
      "title": "Probe 1 hit 203°F",
      "body": "Brisket is at target temp",
      "alert_id": "<uuid from PiFire>",
      "type": "probe_target"
    }
  }
}
```
- The app **must** post a visible notification for every high-priority data message, or Android may deprioritize future ones.
- A short TTL means stale alerts (e.g. "temp too high" from 2 hours ago) get dropped instead of delivered late.

### 3.7 Abuse protection
Use the **Workers Rate Limiting binding**. It works on the free plan with no extra charge (it only consumes normal Workers requests/CPU). Never use KV for counters.

- **Per push_key send limit:** 10 per 60 s, keyed on `sha256(push_key)`.
- **Per IP register limit:** 3 per 60 s, keyed on `CF-Connecting-IP`. This protects the KV write budget.
- **Payload caps:** see below.

Binding constraints to design around:
- `period` must be **10 or 60 seconds**, so no hourly/daily caps with this binding.
- Counts are **per Cloudflare location**. Fine here, since a given Pi/phone consistently hits the same nearby location.
- Counts are **approximate**. Good as a loose filter against buggy retry loops, not strict abuse prevention.
- Not visible in the dashboard. Log `rate_limited` events (Analytics Engine or `console.log`) to monitor.

If an exact daily cap is ever needed, use a SQLite-backed Durable Object (free plan requires `new_sqlite_classes` in the migration). Not needed for <100 users.

```toml
# wrangler.toml
[[ratelimits]]
name = "SEND_LIMITER"
namespace_id = "1001"
  [ratelimits.simple]
  limit = 10
  period = 60

[[ratelimits]]
name = "REGISTER_LIMITER"
namespace_id = "1002"
  [ratelimits.simple]
  limit = 3
  period = 60
```

```js
const { success } = await env.SEND_LIMITER.limit({ key: keyHash });
if (!success) return json({ ok: false, error: "rate_limited" }, 429);
```

Other protections:
- **Payload caps:** title ≤ 100 chars, body ≤ 500 chars, data ≤ 2 KB.
- Reject unknown fields; validate types.
- Optional later: Play Integrity API check on `/v1/register`.

### 3.8 Project layout
```
push-relay/
├── wrangler.toml        # KV binding, vars (FCM_PROJECT_ID)
├── src/
│   ├── index.js         # router
│   ├── register.js
│   ├── send.js
│   ├── fcm.js           # JWT signing, token cache, send
│   ├── ratelimit.js
│   └── util.js          # sha256, key gen, validation
└── test/
```

## 4. Android App

### 4.1 Registration
1. On first launch / FCM `onNewToken`: if no stored push_key, call `POST /v1/register`; otherwise `PUT /v1/register` with the existing key.
2. Store push_key in app storage (EncryptedSharedPreferences).
3. Send push_key to each connected PiFire over the websocket, e.g. `{ "cmd": "push_register", "push_key": "..." }`.
4. A settings toggle to disable push calls `DELETE /v1/register` and tells PiFire to remove the key.

### 4.2 Receiving
- `FirebaseMessagingService.onMessageReceived` → build notification from `data`.
- **Dedupe** on `alert_id` (keep a small LRU of recently shown IDs) so the local path and FCM don't double-notify.
- Notification channels per alert type (temp alarms vs. info).

### 4.3 Local LAN fallback (works without internet)
- While a cook is active (or the user opts in), run a **foreground service** holding the PiFire websocket.
- PiFire emits alert events over the websocket with the same `alert_id`. The app raises a local notification.
- Persistent notification required by Android; show current temps in it so it's useful.
- Stop the service when the cook ends or the connection is lost for X minutes.
- Requires `FOREGROUND_SERVICE` + appropriate `foregroundServiceType` (likely `dataSync` or `connectedDevice`; check the current Play policy).

## 5. PiFire (Raspberry Pi, Python)

1. New notification provider next to the existing ones, e.g. `"pifire_push"`.
2. Settings: list of push_keys (multiple phones), relay URL (default to the official Worker, overridable for self-hosters).
3. Websocket handler for `push_register` / `push_unregister`: add/remove the key in settings.
4. On alert:
   - Generate `alert_id` (uuid4).
   - Emit the alert event over the websocket (local path).
   - `POST /v1/send` for each key in a background thread with a short timeout (≤ 5 s).
5. Response handling: `410` → remove key; `429`/`5xx` → exponential backoff, max 3 tries; network error → log and move on (never block or crash the control loop).
6. Keep the OneSignal provider during transition, then deprecate.

## 6. Implementation Checklist

### Relay
- [ ] Create Cloudflare account, KV namespace, `wrangler.toml`
- [ ] Create Firebase project, add Android app, generate service account key
- [ ] `wrangler secret put FCM_SERVICE_ACCOUNT`
- [ ] Implement JWT signing + token cache, test against FCM
- [ ] Implement register / update / delete
- [ ] Implement send with 410 cleanup
- [ ] Rate limiting bindings (`SEND_LIMITER`, `REGISTER_LIMITER`) + validation
- [ ] Deploy to `*.workers.dev`

### Android
- [ ] Add Firebase Messaging dependency + `google-services.json`
- [ ] Registration flow + `onNewToken` handling
- [ ] Websocket `push_register` message
- [ ] `FirebaseMessagingService` + notification channels
- [ ] `alert_id` dedupe
- [ ] Foreground service for LAN alerts
- [ ] Settings toggle (enable/disable push)

### PiFire
- [ ] Push provider + settings schema
- [ ] Websocket register/unregister handlers
- [ ] Alert emission over websocket with `alert_id`
- [ ] Async send with backoff + 410 handling
- [ ] Docs for self-hosters (custom relay URL)

### Rollout
- [ ] Ship app + PiFire update with both OneSignal and new provider
- [ ] Monitor Worker analytics / error rates
- [ ] Remove OneSignal

## 7. Testing
- Unit: push_key gen, hashing, validation, JWT signing (compare with a known-good token).
- Integration: register → send → receive on a real device; token refresh keeps the same key; uninstall → 410 → PiFire drops key.
- Offline: unplug the router WAN, confirm the LAN foreground-service alert still fires and no duplicate appears when WAN returns.
- Abuse: hammer `/v1/send` with one key, confirm 429; confirm KV writes don't grow with sends.

## 8. Open Questions
- ~~Is the Workers Rate Limiting binding available on the free plan?~~ **Resolved:** yes, no extra charge. Using it at 10/min per push_key and 3/min per IP for register; daily cap dropped (see 3.7).
- Should one push_key be shared across multiple PiFire instances, or one per instance?
- iOS someday? FCM supports APNs via the same API, so the relay wouldn't change.
- Offer a UnifiedPush/ntfy option for users who want zero third-party cloud?
