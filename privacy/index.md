---
title: Privacy Policy
---

# PiFire Android Privacy Policy

**Effective November 8, 2026**

This policy describes what the PiFire Android app ("the app") collects, why, and what you can do
about it. The app is developed by WeberBox ("we", "us"). It is an unofficial companion app for
[PiFire](https://github.com/nebhead/PiFire), the open-source smoker and grill controller you run
on your own Raspberry Pi. The app's source code is public at
[github.com/weberbox/PiFire-Android](https://github.com/weberbox/PiFire-Android).

## The short version

- The app talks to **your own PiFire server**. Your grill data, recipes, cook history and settings
  go between the app and that server only; we never receive them.
- We don't have accounts, don't show ads, don't use analytics or the advertising ID, and don't sell
  or share your data.
- **Crash reporting** (Sentry) is on by default and can be turned off in the app's settings.
- **Push notifications** use Google's Firebase Cloud Messaging and, for older PiFire servers,
  OneSignal. Push stays off until you turn it on.

## Your PiFire server

When you add a server, the app stores its address and name, and any username, password and extra
headers you enter (for example a reverse proxy or Cloudflare Access token), on your phone. Usernames,
passwords and extra headers are encrypted with a key kept in Android's secure keystore, and they
are left out of Android's device backups. The app sends them only to the server you configured.

Everything the app shows about your grill (temperatures, probes, timers, history, recipes, pellet
levels, notifications and settings) comes from your PiFire server and is sent back to it when you
make changes. We don't receive or store any of it. How your server handles data is up to you and
the PiFire project.

## Crash reporting (Sentry)

To find and fix bugs, the app sends crash and error reports to [Sentry](https://sentry.io), a
service that stores them for us. Crash reporting is on by default. You can turn it off at any time
under **Settings > Crash Reporting**; the change applies the next time the app starts.

A report can include:

- The error and the code where it happened, the app version, and a log of recent app events.
- Your device model, Android version, language and other technical device details.
- Your PiFire server's version and hardware setup (platform, controller, modules, temperature unit).
  Not its address or your credentials.
- A screenshot of the screen at the time of the error, and for some sessions a short replay of the
  app's screens. Both are **masked**: all text and images are blacked out before anything is sent,
  so only the layout is visible.
- Performance timings of app screens and operations.
- Your email address, **only if** you enter one in the Crash Reporting settings so we can contact
  you about a problem. It's empty unless you add it, and you can remove it at any time.

Sentry deletes this data automatically after its retention period (no more than 90 days).

## Push notifications

Push notifications are optional. They stay off until you turn them on for a server, and you can
turn them off again in the app's notification settings or in Android's settings.

**Newer PiFire servers (app push).** These use Google's
[Firebase Cloud Messaging](https://firebase.google.com/products/cloud-messaging) (FCM), which gives
the app a push token for your phone. The first time you turn on alerts for a server, the app
registers that token with our push relay, a small service we run on Cloudflare. The relay stores
only the token, with the dates it was created and last updated, under a random key. The app gives
that key to each PiFire server you turn alerts on for. When your server sends an alert (for example
a temperature or timer notification), the relay forwards it to your phone through FCM. The relay
does not store alert contents or your server's address.

Turning off alerts for a server removes your phone from that server, so it can no longer send you
alerts. The relay's record is deleted automatically when Google reports the token is no longer
valid (for example after you uninstall the app), or sooner if you ask us (see below). The relay
uses your IP address only to limit how often it can be called and does not store it. Cloudflare
keeps short-lived service logs (a few days) that may include standard request details.

**Older PiFire servers.** These use [OneSignal](https://onesignal.com) for notifications.
OneSignal collects nothing until you agree to it during setup. OneSignal then assigns your device an ID,
which the app sends to your PiFire server so it can address notifications to your phone. OneSignal
stores this ID and the technical details it needs to deliver notifications.

## App configuration (Firebase Remote Config)

The app uses Google's [Firebase Remote Config](https://firebase.google.com/products/remote-config)
to check for app updates and which PiFire versions and features it supports. Firebase identifies
the app installation with a random installation ID for this. No personal data is sent.

## Other Google services

These run through Google Play services on your phone and give the app only the result:

- **QR code scanning** during setup uses Google's code scanner. It handles the camera itself; the
  app never gets camera access and receives only the scanned text.
- **Voice search** uses Android's speech recognition, which may be processed by Google. The app
  receives only the recognized text.
- **App updates** in the Google Play version use Google Play's in-app updates.

**Biometric unlock**, if you turn it on, is handled entirely by Android. Your fingerprint or face
data never reaches the app or us.

## What we don't do

- No accounts or sign-in with us.
- No advertising, no advertising ID, no analytics or tracking across apps.
- No location, contacts, photos, files or microphone access by the app itself.
- We don't sell, rent or share your data with anyone. Sentry, Google (Firebase), Cloudflare and
  OneSignal only process data on our behalf to provide the features above.

## Your choices and deleting your data

- Turn off crash reporting under **Settings > Crash Reporting**, and remove your email there.
- Turn off push notifications per server in the app, or in Android's settings.
- Uninstalling the app deletes everything it stored on your phone. The relay's push registration is
  then deleted when Google reports the token as invalid.
- To have crash reports or push registrations deleted sooner, or for any other privacy request,
  email [support@weberbox.com](mailto:support@weberbox.com). We'll answer within 30 days.

## Security

All data the app sends to us and our providers (Sentry, Firebase, the push relay and OneSignal)
travels over encrypted HTTPS connections. The connection to your own PiFire server uses whatever you
configured: HTTP or HTTPS, with or without a reverse proxy.

## Children

The app is a tool for operating a grill and is not directed at children under 13. We don't
knowingly collect personal data from children.

## Changes to this policy

When the app's data practices change, we'll update this page and its effective date. Significant
changes will also be noted in the app's release notes.

## Contact

Questions about this policy or your data: [support@weberbox.com](mailto:support@weberbox.com)
