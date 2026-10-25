# Backlog

Standing decisions, known bugs and deferred work. Format: `- [area] file: what, and the suggested fix`.
Paths are relative to `app/src/main/java/com/weberbox/pifire/`. Verify an entry against the code
before acting on it, and delete it once it's done.

## Decisions
- (2026-10-22) `BaseViewModel` keeps Compose `State` for `viewState` (same as the reference
  project), not `StateFlow`.
- (2026-10-22) "The reference project" is a separate app whose patterns we copy; its location is
  in `CLAUDE.local.md` (not committed).
- (2026-10-22) Settings stays one feature with `screen/<group>/` sub-folders.
- (2026-10-25) The default pellet profile in `PelletsData` ("Generic", "Alder", "Generic Alder")
  mirrors PiFire's built-in profile and is replaced by server data, so it stays untranslated. The
  recipe print HTML `<title>` isn't shown (the print job uses the file name).
- (2026-10-25) `HomeScreen` is the app shell around the pager, so its layout stays inline; pager
  pages and the setup steps have no Scaffold of their own.
- (2026-10-25) Repos share `core/repo/ServerDataSync` (fetch/cache/listen) and
  `core/network/httpRequest` (HTTP errors). `SettingsRepoImpl` keeps one forwarding method per
  server setting: ViewModels may only call repos, and each is a one-line `transformResult(...)`.
- (2026-10-25) `PickerWheel` reads the scroll offset only in layout (row height) and draw (text
  scale, color): text is drawn at the selected size and scaled down.
- (2026-10-25) No foreground service for local alerts: cooks run 12-16 hours, so it would be
  killed (Android 15 caps `dataSync` at 6 h/day) and an open socket drains the battery. Local
  alerts arrive only while the app is open; missed ones are shown from the events list on open.
  Users who need alerts with no internet keep PiFire's other notifiers (Apprise).
- (2026-10-25) The restructure is done (steps 1-8, see git history). Exceptions to the layout
  rules are listed with reasons in `ArchitectureTest`.

## Open
- [ui] `core/ui/components/PickerWheel`: verify the scaled-text look on a device.

## v2 API and push plan (PiFire + app)
Replaces OneSignal v1 push (details in `docs/PUSH.md`) and the Socket.IO transport. PiFire is
built in full on a fork branch and submitted as one PR; only this app uses `socket_io.py`.
- PiFire side: a new self-contained blueprint (`blueprints/mobile/v2/`, mounted at `/mobile/v2`)
  with HTTP endpoints + one SSE stream (snapshot on connect, then changes, plus alert events),
  an `{ok, code, data}` envelope with error codes, a `hello` endpoint (api version +
  capabilities), origin checks, and its own store for push keys (never in the settings payload).
  Outside the blueprint: register it in `app.py` and one call from the notification dispatch
  path. Keep `gunicorn -k eventlet -w 1` (SSE needs it; installs may not get a new
  `webapp.conf`). The existing `/api` stays untouched. Keep the OneSignal provider until v1 EOL.
- App side: v2 `*ApiImpl`s (Retrofit + OkHttp SSE) behind a `hello` probe (404 -> Socket.IO)
  and a Remote Config switch in `feature_support_config` (off until the PiFire PR merges);
  repos/ViewModels unchanged. New push via the relay (structured alert data, text built from
  string resources, `alert_id` dedupe); OneSignal kept for Socket.IO servers. Check the server
  version over HTTP on every connect, not via the socket: old apps read it from `get_app_data`
  and fall back to the cached version, so they never see an unsupported-server prompt on a
  socket-less server.
- Relay (Cloudflare Worker): lives in this repo under `relay/` and is committed. It's the app's
  own backend (sends through the app's Firebase project, payload is a contract with this app),
  not something we package for others to host.
  - Separate from the Android build: not in `settings.gradle.kts`, not covered by Spotless/ktlint,
    own `package.json`, `tsconfig.json` and `.gitignore` (`node_modules/`, `.wrangler/`,
    `.dev.vars`). Add a `CLAUDE.md` note that the Android rules don't apply under `relay/`.
  - Secrets never committed: FCM credentials via `wrangler secret put` (`.dev.vars` locally);
    account and KV/D1 ids in a gitignored local config, not the committed `wrangler.toml`.
  - Versioning is for our own tracking: a version constant bumped on deploy and returned by a
    health endpoint, plus a `Relay` section in commit messages. No tags or GitHub releases (keeps
    it clear of the app's release flow). Deploy by hand with `wrangler deploy`; a workflow
    filtered to `relay/**` only if that gets tedious.
  - Payload is a contract with installed apps: only add fields, never rename or remove.
  - App reads the relay URL from `local.properties` as `BuildConfig.*`, with an empty fallback in
    `local.defaults.properties` (empty turns relay push off), so our deployment's URL stays out of
    the repo. Self-builders need their own Firebase project, `google-services.json`, relay deploy
    and URL; document the steps in `relay/README.md` / `docs/PUSH.md`.
- Order:
  1. Relay live.
  2. App release, tested against the fork branch, v2 switched off.
  3. `app_update_config` entry for that release as FLEXIBLE.
  4. Once the rollout and Sentry look clean, switch it to IMMEDIATE (reaches every 3.x app,
     independent of the server).
  5. PiFire PR.
  6. After merge: v2 on in Remote Config, `maxServerInfo` for old version codes set to the
     last Socket.IO PiFire.
- Open:
  - Check the version distribution (Play Console / Firebase Analytics) before forcing.
  - Word the forced-update message so 3.0.0-3.3.1 Play users (in-app update crash, fixed in
    3030200) know to update from the Play Store.
  - Remove `socket_io.py` in the PR only if the forced update has been live a few weeks;
    otherwise keep it one more PiFire release.
- Upstream (fold into the PR): Socket.IO uses `cors_allowed_origins="*"` with no auth, so any web
  page can call `post_app_data` (reboot, shutdown, factory reset); `settings_data` returns
  notifier credentials; a second client gets no snapshot on connect; `app.py` creates
  `SocketIO(app)` twice. Removing the socket fixes the first and third.
