# PiFire Push Notifications: Migration Plan

Replace the OneSignal v1 (unauthenticated) push flow with a self-hosted relay on **Cloudflare Workers + Firebase Cloud Messaging (FCM)**, plus **local alerts over the v2 live stream** while the app is open. Transport and rollout are in `BACKLOG.md` (v2 API and push plan).

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
│              │  2. push_key via v2 API             │ 4. FCM HTTP v1
│              │ ─────────────┐                      ▼
│              │              ▼             ┌─────────────────────┐
│              │      ┌──────────────┐      │ Firebase Cloud      │
│              │      │ PiFire (Pi)  │      │ Messaging           │
│              │      └──────┬───────┘      └─────────┬───────────┘
│              │             │ 3. send(push_key, msg)  │
│              │             └──────▶ Worker            │
│              │ ◀──────────────────────────────────────┘ 5. push
│              │
│  (app open)  │ ◀── v2 live stream (local alerts, LAN)
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

All bodies are JSON. All responses are `{ "ok": bool, "error"?: string, ...fields }`, where
`error` is one of the codes below. Unknown request fields are ignored, not forwarded, so a newer
PiFire or app never breaks against the relay.

| Method | Path | Called by | Body | Returns |
|---|---|---|---|---|
| `POST` | `/v1/register` | App | `{ fcm_token }` | `201 { push_key }` |
| `PUT` | `/v1/register` | App | `{ push_key, fcm_token }` | `200` (token refresh, same key) |
| `DELETE` | `/v1/register` | App | `{ push_key }` | `200` (also when the key is already gone) |
| `POST` | `/v1/send` | PiFire | see 3.6 | `200` |
| `GET` | `/v1/health` | Anyone | – | `200 { version }` |

`POST`/`PUT /v1/register` check the token with an FCM `validate_only` send before writing to KV,
so junk tokens never use the KV write budget (`400 invalid_token`).

**Status codes and `error` values:**

| Status | `error` | Meaning / caller action |
|---|---|---|
| `200` | – | Accepted by FCM. |
| `400` | `invalid_request` | Malformed JSON, missing or mistyped field. Don't retry. |
| `400` | `invalid_token` | (register) FCM rejected the token. |
| `404` | `unknown_key` | push_key not found. PiFire drops it only after repeated 404s over a day (KV is eventually consistent for ~60 s after register). |
| `410` | `unregistered` | (send) FCM said the token is `UNREGISTERED`; entry deleted. **PiFire drops this key.** |
| `413` | `payload_too_large` | Over the caps in 3.6. Don't retry. |
| `429` | `rate_limited` | Back off. |
| `502` | `fcm_error` | FCM failed; retry with backoff, max 3 tries. |

Any route can also return `404 not_found`, `405 method_not_allowed` or `500 internal_error`.

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
- Send via `POST https://fcm.googleapis.com/v1/projects/{project_id}/messages:send`, with
  `project_id` from the service account (no separate config var).
- On `401`, drop the cached token and retry once.

### 3.6 Send request and FCM message
The relay is **type-agnostic**: it checks the envelope and caps and forwards `type` and `data`
untouched, so a new alert type needs a PiFire and app change but no relay deploy.

`POST /v1/send` body:
```json
{
  "push_key": "pfk_...",
  "alert_id": "<uuid4 from PiFire, same id as on the v2 stream>",
  "type": "probe_target",
  "server_uuid": "<settings.server_info.uuid>",
  "time": 1761400000,
  "data": { "probe_label": "Probe1", "probe_name": "Brisket", "target": 203, "units": "F" },
  "fallback": { "title": "Brisket Target Achieved", "body": "Brisket target of 203F achieved" }
}
```
- Required: `push_key`, `alert_id`, `type`, `server_uuid`, `time` (epoch seconds on the Pi; the
  app formats it in the phone's locale).
- `data`: optional flat object, values string / number / bool / null, max 16 keys, keys
  `[a-z0-9_]{1,32}`. Fields per type are in 3.9.
- `fallback`: optional English `title` / `body` PiFire already builds today. The app shows it
  only for a `type` it doesn't know (an older app and a newer PiFire), so every alert still shows
  something useful.
- Caps: whole body ≤ 4 KB, `data` ≤ 2 KB serialized, `fallback.title` ≤ 100 chars,
  `fallback.body` ≤ 500, `type` `[a-z0-9_]{1,32}`.

The relay sends a high-priority **data message** (FCM data values must be strings):
```json
{
  "message": {
    "token": "<fcm_token>",
    "android": { "priority": "high", "ttl": "900s" },
    "data": {
      "alert_id": "<uuid>",
      "type": "probe_target",
      "server_uuid": "<uuid>",
      "time": "1761400000",
      "data": "{\"probe_label\":\"Probe1\",\"probe_name\":\"Brisket\",\"target\":203,\"units\":\"F\"}",
      "fallback_title": "Brisket Target Achieved",
      "fallback_body": "Brisket target of 203F achieved"
    }
  }
}
```
- `data` is nested as a JSON string so its keys can never collide with the envelope or FCM's
  reserved keys (`from`, `message_type`, `google.*`, `gcm.*`). The app decodes it with the shared
  lenient `Json`.
- Payload contract with installed apps: only add fields, never rename or remove.
- The app **must** post a visible notification for every high-priority data message, or Android
  may deprioritize future ones. That includes unknown types (fallback text, or a generic
  string) and a `server_uuid` the app no longer has (generic server name).
- Send structured values, not text: the app builds the wording from its string resources
  (translated, per-type channels), and less content passes through Cloudflare and Google.
- No `collapse_key`: FCM keeps only 4 distinct collapse keys per offline device, so one per alert
  would drop alerts; the app dedupes on `alert_id` instead.
- TTL 15 minutes: a phone briefly offline still gets the alert, but a stale one (e.g. "temp too
  high" from 2 hours ago) is dropped instead of delivered late.
- The relay never logs a push_key, FCM token or `data`; logs use the first 8 hex chars of the key
  hash.

### 3.7 Abuse protection
Use the **Workers Rate Limiting binding**. It works on the free plan with no extra charge (it only consumes normal Workers requests/CPU). Never use KV for counters.

- **Per push_key send limit:** 20 per 60 s, keyed on `sha256(push_key)` (one key serves every
  PiFire the phone uses).
- **Per IP register limit:** 3 per 60 s, keyed on `CF-Connecting-IP`. This protects the KV write budget.
- **Payload caps:** see 3.6.

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
  limit = 20
  period = 60

[[ratelimits]]
name = "REGISTER_LIMITER"
namespace_id = "1002"
  [ratelimits.simple]
  limit = 3
  period = 60
```

```ts
const { success } = await env.SEND_LIMITER.limit({ key: keyHash });
if (!success) return json({ ok: false, error: "rate_limited" }, 429);
```

Optional later: Play Integrity API check on `/v1/register`.

### 3.8 Project layout
TypeScript, separate from the Android build (see `BACKLOG.md`).
```
relay/
├── package.json, tsconfig.json, vitest.config.ts, .gitignore
├── wrangler.toml        # KV and rate limit bindings; KV id is a placeholder
├── README.md            # develop, deploy and self-build steps
├── scripts/deploy.mjs   # fills the KV id from gitignored deploy.local.json, runs wrangler deploy
├── src/
│   ├── index.ts         # router (default export only)
│   ├── version.ts       # RELAY_VERSION
│   ├── env.ts           # bindings, KV record
│   ├── register.ts      # POST / PUT / DELETE /v1/register
│   ├── send.ts          # POST /v1/send, FCM message
│   ├── fcm.ts           # JWT signing, token cache, send, validate_only
│   ├── validate.ts      # request bodies and caps
│   └── util.ts          # sha256, key gen, json responses, logging
└── test/                # Vitest + @cloudflare/vitest-plugin (Workers runtime, FCM mocked)
```

### 3.9 Alert types (v1)
PiFire maps each `send_notifications` event to a `type`. Temps are numbers and may be decimals
(Celsius); `units` is `"F"` or `"C"`. The app shows `probe_name`, falling back to `probe_label`.

| PiFire event | `type` | `data` | App channel |
|---|---|---|---|
| `Probe_Temp_Achieved` | `probe_target` | `probe_label`, `probe_name`, `target`, `units` | temp |
| `Probe_Temp_Limit_Alarm` | `probe_limit` | `probe_label`, `probe_name`, `limit`, `units`, `direction` (`"high"`/`"low"`) | temp |
| `Timer_Expired` | `timer_expired` | – | timer |
| `Pellet_Level_Low` | `pellet_level_low` | `level` (percent) | pellets |
| `Grill_Error_00` | `grill_error` | – | error |
| `Grill_Error_01` | `grill_error_max_temp` | `temp`, `max_temp`, `units` | error |
| `Grill_Error_02` | `grill_error_startup_temp` | `temp`, `startup_temp`, `units` | error |
| `Grill_Error_03` | `grill_reignite` | `temp`, `startup_temp`, `units` | error |
| `Grill_Warning` | `grill_warning` | – | error |
| `Control_Process_Stopped` | `control_stopped` | – | error |
| `Recipe_Step_Message` | `recipe_message` | `message` (recipe text, ≤ 500 chars) | recipe |
| `Test_Notify` | `test` | – | base |

- `probe_name` and `direction` aren't passed to `send_notifications` today; the PiFire PR adds
  them (`notify_data` items already carry `name` and `type`). A missing `direction` gets neutral
  "limit reached" wording.
- `recipe_message` is the only free text from the user's recipe; shown as-is (`UiText(value)`).
- PiFire's OneSignal sends already target `pifire_recipe_message` and `pifire_test_message`
  channels, which the app never creates; the new receiver adds a recipe channel and uses base for
  test.

## 4. Android App

### 4.1 Registration
1. On first launch / FCM `onNewToken`: if no stored push_key, call `POST /v1/register`; otherwise `PUT /v1/register` with the existing key.
2. Store push_key with the app's existing `Crypto` + DataStore (security-crypto is deprecated).
3. Send push_key to each PiFire through the v2 push register endpoint (Socket.IO servers keep OneSignal). One key per phone, shared by all its PiFires; alerts carry `server_uuid`.
4. A settings toggle to disable push calls `DELETE /v1/register` and tells PiFire to remove the key.

### 4.2 Receiving
- `FirebaseMessagingService.onMessageReceived` → build notification from `data`.
- **Dedupe** on `alert_id` (keep a small LRU of recently shown IDs) so the local path and FCM don't double-notify.
- Notification channels per alert type (temp alarms vs. info).

### 4.3 Local alerts (no foreground service)
- **Decided against a foreground service.** Cooks run 12-16 hours: Android 15 caps `dataSync` services at 6 hours a day, the system may kill it anyway, and holding a connection that long drains the battery.
- While the app is open, PiFire sends alert events over the v2 live stream with the same `alert_id`, and the app shows them (deduped against FCM).
- With the internet down and the app closed there is no push. On open, the app shows missed alerts from the events list. Users who need alerts with no internet keep PiFire's other notifiers (Apprise).

## 5. PiFire (Raspberry Pi, Python)

1. New notification provider next to the existing ones, e.g. `"pifire_push"`.
2. Push keys (multiple phones) live in the v2 blueprint's own store, never in the settings file: `settings_data` returns settings to any client. The relay URL is fixed: FCM tokens belong to the app's Firebase project, so a self-hosted relay can't reach the official app (ntfy/UnifiedPush is the zero-cloud option).
3. v2 endpoints for push register / unregister add or remove the key.
4. On alert:
   - Generate `alert_id` (uuid4).
   - Send the alert event on the v2 live stream (local path).
   - `POST /v1/send` for each key in a background thread with a short timeout (≤ 5 s).
5. Response handling: `410` → remove key; `404` → remove only after repeated failures over time (KV is eventually consistent right after register); `429`/`5xx` → exponential backoff, max 3 tries; network error → log and move on (never block or crash the control loop).
6. Keep the OneSignal provider during transition, then deprecate.

## 6. Implementation Checklist

### Relay
- [x] Create Cloudflare account, KV namespace, `wrangler.toml`
- [x] Create Firebase project, add Android app, generate service account key
- [x] `wrangler secret put FCM_SERVICE_ACCOUNT`
- [x] Implement JWT signing + token cache (first push delivered to a device 2026-10-25)
- [x] Implement register / update / delete
- [x] Implement send with 410 cleanup
- [x] Rate limiting bindings (`SEND_LIMITER`, `REGISTER_LIMITER`) + validation
- [x] Deploy to `*.workers.dev`

### Android
- [x] Registration flow + `onNewToken` handling (`PushRepo`; registers on first use, not at install)
- [ ] v2 push register / unregister calls
- [x] `FirebaseMessagingService` + notification channels (`PushMessagingService`, `AlertChannel`)
- [x] `alert_id` dedupe (`AlertDeduper`, in memory)
- [x] Debug-only "Send Test Push" in App settings: registers and sends a `test` alert to this phone
- [ ] Local alerts from the v2 stream while open; missed alerts from the events list
- [ ] Settings toggle (enable/disable push)

### PiFire
- [ ] Push provider + blueprint key store
- [ ] v2 register/unregister endpoints
- [ ] Alert events on the v2 stream with `alert_id`
- [ ] Async send with backoff + 410 handling

### Rollout
- [ ] Follow the order in `BACKLOG.md` (relay, app, forced update, PiFire PR); keep OneSignal on both sides
- [ ] Monitor Worker analytics / error rates
- [ ] Remove OneSignal

## 7. Testing
- Unit: push_key gen, hashing, validation, JWT signing (compare with a known-good token).
- Integration: register → send → receive on a real device; token refresh keeps the same key; uninstall → 410 → PiFire drops key.
- Offline: unplug the router WAN with the app open, confirm the stream alert fires and no duplicate appears when WAN returns; with the app closed, confirm missed alerts show on open.
- Abuse: hammer `/v1/send` with one key, confirm 429; confirm KV writes don't grow with sends.

## 8. Open Questions
- ~~Is the Workers Rate Limiting binding available on the free plan?~~ **Resolved:** yes, no extra charge. Using it at 10/min per push_key and 3/min per IP for register; daily cap dropped (see 3.7).
- ~~Should one push_key be shared across multiple PiFire instances?~~ **Resolved:** one per phone, alerts carry `server_uuid`; raise the per-key send limit a little for several grills.
- iOS someday? FCM supports APNs via the same API, so the relay wouldn't change.
- Offer a UnifiedPush/ntfy option for users who want zero third-party cloud?
