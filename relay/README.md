# PiFire push relay

Cloudflare Worker that lets a PiFire server send push alerts to the PiFire Android app through
Firebase Cloud Messaging, without an API key on the Pi or in the app. The design, endpoints and
payload contract are in [`docs/PUSH.md`](../docs/PUSH.md).

This is the app's own backend: FCM tokens belong to the app's Firebase project, so a relay only
reaches app builds from that same project. It's separate from the Android build (not in Gradle,
not formatted by Spotless).

## Develop

Node 20 or newer.

```sh
npm install
npm run check   # TypeScript
npm test        # Vitest in the Workers runtime; FCM and Google OAuth are mocked
npm run dev     # local server on http://localhost:8787
```

`npm run dev` needs no secrets for `/v1/health` and request validation. To send real pushes
locally, put the service account (step 3 below) in `.dev.vars` as `FCM_SERVICE_ACCOUNT`, as JSON
on a single line. `.dev.vars` is gitignored.

## Deploy

1. Log in: `npx wrangler login`. The free Workers plan is enough.
2. Create the KV namespace: `npx wrangler kv namespace create PUSH_KEYS`. Put the printed id in
   `deploy.local.json` (gitignored):
   ```json
   { "kvNamespaceId": "<32-character id>" }
   ```
3. In the Firebase console of the project the app's `google-services.json` comes from, open
   Project settings > Service accounts > Generate new private key. Store it as a secret, then
   delete the file:
   ```sh
   npx wrangler secret put FCM_SERVICE_ACCOUNT < service-account.json
   # PowerShell:
   Get-Content service-account.json -Raw | npx wrangler secret put FCM_SERVICE_ACCOUNT
   ```
4. Bump `RELAY_VERSION` in `src/version.ts`, then `npm run check && npm test && npm run deploy`.
   `npm run deploy -- --dry-run` builds without uploading.
5. Check `https://pifire-push-relay.<your-subdomain>.workers.dev/v1/health`.

Never commit the service account, `.dev.vars`, `deploy.local.json` or the namespace id.

## Changing the relay

- The `/v1/send` body and the FCM message are a contract with PiFire servers and installed apps:
  only add fields, never rename or remove one.
- The relay doesn't know alert types. A new type needs PiFire and app changes only.
- KV allows 1,000 writes a day on the free plan: never write to KV per send.
- Never log a push_key, FCM token or alert data; use `keyTag()`.
- Commits that change the relay get a `Relay` section in the message.
