# PiFire Mobile API v2

HTTP + Server-Sent Events replacement for the Socket.IO transport (`blueprints/mobile/socket_io.py`),
used only by this app. Rollout and order are in `BACKLOG.md` (v2 API and push plan); push delivery
is in `docs/PUSH.md`.

**Design rule: same actions, new transport.** v2 exposes the exact reads and writes the app makes
over Socket.IO today, with the same payloads, so PiFire reuses its existing dispatch
(`_get_app_data` / `_post_app_data`) and the app swaps transport under its existing `*ApiImpl`s.
Repos, DTOs and mappers don't change.

---

## 1. Basics

- Blueprint `blueprints/mobile/v2/` (`routes.py`, `stream.py`, `push.py`), mounted at
  **`/mobile/v2`**. Outside it: registered in `app.py`, `notify/mobile_push.py` (alert feed,
  device store, relay sends; in `notify/` so the control process can use it without importing the
  web app), and one call from `send_notifications` (section 6).
- The dispatch functions moved from `socket_io.py` into `blueprints/mobile/app_data.py`, which
  both transports import, so `socket_io.py` can be deleted later without touching v2.
- Runs under the existing `gunicorn -k eventlet -w 1` (SSE needs eventlet; installs may not get a
  new `webapp.conf`).
- Auth is whatever already protects the server (reverse proxy basic auth, extra headers). The app
  sends the same headers and TLS settings it uses for Socket.IO.

### 1.1 Envelope
Every JSON response:
```json
{ "ok": true, "data": { } }
{ "ok": false, "code": "action_failed", "message": "Error: Profile not included in request" }
```
`data` is exactly what `_response(data=...)` holds today (may be `null`). `message` is PiFire's own
text, shown to the user as today (`DataError.Server`).

| Status | `code` | Meaning |
|---|---|---|
| `200` | – | OK |
| `400` | `invalid_request` | Body isn't a JSON object, or wrong content type |
| `403` | `forbidden_origin` | Browser request from another origin (1.2) |
| `404` | `unknown_action` | Unknown data name, action or type |
| `404` | `unknown_device` | (push) No device with that id |
| `409` | `too_many_devices` | (push) The device limit is reached |
| `422` | `action_failed` | PiFire returned `result: Error`; see `message` |
| `500` | `internal_error` | Unhandled exception (logged on the Pi, no details returned) |

Unknown request fields are ignored. New response fields may appear at any time.

### 1.2 Browser protection
Fixes the Socket.IO hole (`cors_allowed_origins="*"`, so any web page could call `post_app_data`):
- A request with an `Origin` header whose host doesn't match the request's `Host` gets `403`.
  The app (OkHttp) sends no `Origin`.
- `POST` requires `Content-Type: application/json`, so a cross-site form or simple fetch can't
  reach it without a CORS preflight, which v2 never answers (`DELETE` always needs one).
- No CORS headers are ever sent.

---

## 2. Discovery

`GET /mobile/v2/hello`
```json
{ "ok": true, "data": {
    "api_version": 1,
    "uuid": "<settings.server_info.uuid>",
    "server_version": "<settings.versions.server>",
    "server_build": "<settings.versions.build>",
    "capabilities": ["stream", "push"]
} }
```
- The app calls it on every connect (when v2 is switched on). Only a successful answer selects
  v2; `404` or any failure uses Socket.IO, which then reports its own connection errors as
  before. A v2 bug or an odd proxy in front of an old PiFire can't lock anyone out.
- `server_version` feeds the supported-version check on every connect (over HTTP, not the socket).
- `api_version` only goes up for a breaking change; `capabilities` grows for additions.

---

## 3. Reads

`GET /mobile/v2/data/<name>`

| `<name>` | Socket.IO today | `data` |
|---|---|---|
| `settings` | `get_app_data('settings_data')` | full settings |
| `dash` | `get_app_data('dash_data')` | dash payload |
| `pellets` | `get_app_data('pellets_data')` | `{uuid, pellets}` |
| `events` | `get_app_data('events_data')` | `{uuid, events}` |
| `info` | `get_app_data('info_data')` | system info |
| `manual` | `get_app_data('manual_data')` | `{manual, active, dcFan}` |
| `recipes` | `get_app_data('recipe_data', 'details')` | `{uuid, recipe_details}` |

## 4. Writes

`POST /mobile/v2/action/<action>/<type>` with the same JSON body the app sends as `json_data`
today (`{}` when it sends none). Maps to `post_app_data('<action>_action', '<type>', body)`.

| `<action>` | `<type>` values (unchanged from Socket.IO) |
|---|---|
| `update` | `settings`, `control` |
| `admin` | `clear_history`, `clear_events`, `clear_pelletdb`, `clear_pelletdb_log`, `factory_defaults`, `reboot`, `shutdown`, `restart_control`, `restart_webapp`, `restart_supervisor` |
| `units` | `f_units`, `c_units` |
| `pellets` | `load_profile`, `hopper_check`, `edit_brands`, `edit_woods`, `add_profile`, `edit_profile`, `delete_profile`, `delete_log` |
| `timer` | `start_timer`, `pause_timer`, `stop_timer` |
| `recipes` | `recipe_delete`, `recipe_start` |
| `probes` | `probe_update` |
| `notify` | `notify_update` |

`data` is whatever PiFire returns today (settings, control, or `null`).

---

## 5. Live stream

`GET /mobile/v2/stream` (`Accept: text/event-stream`)

- Response headers: `Content-Type: text/event-stream`, `Cache-Control: no-cache`,
  `X-Accel-Buffering: no` (stops nginx buffering the stream).
- **On connect**, a snapshot: one `dash`, `pellets`, `events` and `settings` event each. Every
  client gets its own snapshot (Socket.IO only sent one to the first client).
- **Then changes**, checked once a second like `_emit_app_data`: an event only when its payload
  changed. `settings` is new (the app already listens for it; Socket.IO never sent it).
- `alert` events when a notification fires (section 6). Notifications fire in the control
  process, so alerts reach the web app through Redis (a numbered list of recent alerts); each
  stream sends the ones newer than its last, within a second. A new stream starts at the latest
  number: no replay of old alerts.
- A `: ping` comment every 15 s keeps proxies and the app's read timeout from closing an idle
  stream; `retry: 5000` sets the client reconnect delay.
- Each client's loop ends when its connection closes; the `_check_control_status` check runs once
  per server (not per client), as today.

| Event | `data` |
|---|---|
| `dash` | dash payload (same as `socket_dash_data`) |
| `pellets` | `{uuid, pellets}` (same as `socket_pellet_data`) |
| `events` | `{uuid, events}` (same as `socket_event_data`) |
| `settings` | full settings |
| `alert` | `{alert_id, type, server_uuid, time, data, fallback}`: the relay send body (`docs/PUSH.md` 3.6) without `push_key` |

The app reconnects with backoff on any drop and treats the next snapshot as fresh state.

---

## 6. Push

Phones register their relay push key with each PiFire they use.

| Method | Path | Body | Returns |
|---|---|---|---|
| `POST` | `/mobile/v2/push/devices` | `{push_key, relay_url, device_name, app_version}` | `{id}` (upsert by key) |
| `GET` | `/mobile/v2/push/devices` | – | `[{id, device_name, app_version, registered}]` |
| `DELETE` | `/mobile/v2/push/devices/<id>` | – | `null` |
| `DELETE` | `/mobile/v2/push/onesignal/<player_id>` | – | `null` (`404 unknown_device` if absent) |

- `id` is the first 16 hex chars of `sha256(push_key)`. Keys are never returned, logged, or put
  in the settings payload (`settings_data` goes to every client).
- `relay_url` comes from the app (must be `https://`), so PiFire hardcodes no relay and a
  self-built app with its own relay just works.
- Stored in `push_devices.json` in PiFire's folder (mode 0600, gitignored, file-locked: the web
  app and control process both write it), max 20 devices.
- The app re-registers on every connect (cheap upsert), so a reinstall or new key heals itself;
  "remove this device" in the app calls `DELETE`.
- On a v2 connect the app also removes its own OneSignal entry (from when it used Socket.IO), so
  the phone isn't notified twice. `update/settings` merges keys and can't delete one, hence the
  endpoint.
- PiFire's `notify_services.mobile_push.enabled` (default on, shown in the web settings and the
  app) turns app push off on both paths: with it off, PiFire neither streams alerts nor sends them
  to the relay. The web settings page lists app
  push devices and shows the OneSignal section only while OneSignal devices remain.

**Dispatch.** `send_notifications` calls one function from the blueprint after building its
message. It:
1. Builds the alert: `alert_id` (uuid4), `type` and `data` per `docs/PUSH.md` 3.9, `fallback` =
   PiFire's existing English title and body.
2. Sends it as an `alert` event on every open stream.
3. In a background thread, `POST <relay_url>/v1/send` per device (5 s timeout). `410` deletes the
   device; `404` deletes it only after repeated 404s over a day; `429`/`5xx` retry with backoff,
   max 3 tries; network errors are logged. Never blocks or crashes the control loop.

The OneSignal provider stays as is until v1 end of life.

---

## 7. App side

- A `PiFireTransport` interface (`core/network/transport`: connect / get / post / listen) with two
  implementations: `SocketManager` and `HttpSseTransport` (OkHttp, `okhttp-sse`). `*ApiImpl`s
  depend on the interface. The HTTP transport turns the v2 envelope back into the Socket.IO
  response shape (`{data, result, message}`) and maps Socket.IO event names to stream events, so
  repos, DTOs and parsers are unchanged.
- `PiFireConnection` chooses per connection: Remote Config `feature_support_config.MobileApiV2`
  `enabled` (off until the PiFire PR merges; debug builds can force it on in App settings),
  `/hello` answering, and the `server_version`/`server_build` it reports falling in the entry's
  `minVersion`/`minBuild`/`maxVersion`/`maxBuild` range (a max can rule out a PiFire build whose
  v2 misbehaves). The choice is kept for the session. A dropped stream reconnects with backoff
  (2 s doubling to 30 s), like Socket.IO.
- HTTP client: same headers, basic auth and certificate handling as the socket.
- Push: after a v2 connect (and with the user's push consent from setup), `PushRepo.registerWithServer()`
  registers the relay key and removes this phone's OneSignal entry if the server has one. Socket.IO
  servers keep the OneSignal flow.
- Stream alerts: `StreamAlertNotifier` shows `alert` events while connected over v2, when the
  user gave push consent. PiFire checks `mobile_push.enabled` before sending on either path, so the
  app doesn't (its copy of the settings can lag behind a change). PiFire also sends every
  alert through the relay (it can't know whether the app is in the foreground, and a frozen app's
  connection can look open for minutes); the shared `AlertDeduper` drops whichever copy arrives
  second. The stream is the fast path and works on the LAN without internet.
- Settings: on a v2 server the notification settings show App Push (`mobile_push.enabled`
  through `update/settings`) instead of OneSignal, and an App Push screen lists the server's
  devices (`GET /push/devices`, the phone's own marked by its key hash) with delete.
