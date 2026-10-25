# Backlog

Standing decisions, known bugs and deferred work. Format: `- [area] file: what, and the suggested fix`.
Paths are relative to `app/src/main/java/com/weberbox/pifire/`. Lines written before the 2026-10-24
restructure may name old paths (`settings/...` is now `feature/settings/...`, `screens/` is
`screen/`).
Findings from the 2026-10-22 review; verify each against the code before fixing, and delete the
line once it's done.

## Decisions
- (2026-10-22) `BaseViewModel` keeps Compose `State` for `viewState` (same as the reference project), not
  `StateFlow`.
- (2026-10-22) Order: fix the confirmed bugs below in the current layout first (small commits),
  then restructure. Settings stays one feature with `screen/<group>/` sub-folders.
- (2026-10-22) "The reference project" is a separate app whose patterns we copy; its location is
  in `CLAUDE.local.md` (not committed).
- (2026-10-22) Add the reference project's tooling: Spotless/ktlint + `.editorconfig`, JUnit/MockK unit tests,
  Konsist `ArchitectureTest`.
- (2026-10-22) Clean clones should build. Done: `Secrets.kt` values moved to `local.properties` ->
  `BuildConfig` (fallbacks in `local.defaults.properties`); one `app/google-services.json`; one
  Sentry project (the former playstore one) with `environment = <flavor>-<buildType>`; a
  committed `app/google-services.placeholder.json` is used when the real file is missing.
- (2026-10-25) The default pellet profile in `PelletsData` ("Generic", "Alder", "Generic Alder")
  mirrors PiFire's built-in profile and is replaced by server data, so it stays untranslated. The
  recipe print HTML `<title>` isn't shown (the print job uses the file name).
- (2026-10-25) Screens follow the five-part pattern and ViewModels stay on Main (repos do their own
  IO; `ArchitectureTest` checks no ViewModel imports `Dispatchers`). `HomeScreen` is the app shell
  around the pager, so its layout stays inline; pager pages and the setup steps have no Scaffold
  of their own.
- (2026-10-25) Sentry instruments release builds too. R8 mappings upload only with a Sentry auth
  token (`SENTRY_AUTH_TOKEN` or `sentry.properties`); without one, release crash reports stay
  obfuscated.

## Restructure plan
One step per commit, compile all flavors after each.
1. Done (2026-10-24): `navigateFrom` / `popBackStackFrom` / `rememberSourceEntry` in `NavHostUtils`;
   contracts use `NavRoute(route, popUpToRoute, inclusive)`. Only `MainActivity` navigates raw.
2. Done (2026-10-24): `common/` merged into `core/` (`analytics`, `common/model`, `common/util`,
   `data`, `log`, `model`, `network`, `notification`, `remoteconfig`, `session`, `ui`); navigation
   in `navigation/`; `MainActivity`/`MainViewModel`/`MainContract` in `application/`.
3. Done (2026-10-24): every feature in `feature/<name>/` with `components`/`screen`/`util`,
   mappers in `data/mapper`, bindings in `data/di/<Name>DataModule` (`ApiModule`/`RepoModule`
   removed), screen files and `*ScreenDestination` renamed.
4. Done (2026-10-25): no `data/` or `core/` code imports `presentation`. Cached server models
   (`SettingsData` + its PWM/probe/smart-start/OneSignal types and `Setting`, `DashData`,
   `EventsData`, `InfoData`, `PelletsData`, `RecipesData`) moved to `core/model/<feature>/`;
   feature-only ones to `feature/<name>/data/model`; `RunningMode` and changelog `Log.icon` split
   into data + UI extensions; info/pellets formatters to `data/util` (they still store display
   strings with English unit suffixes, see Conventions).
4b. Done (2026-10-25): `core` imports no feature code. Server settings data (`SettingsRepo`,
   `SettingsApi`, `ServerDataManager`, settings mappers and DTOs) in `core/repo` and
   `core/network`, bound in `core/di/RepoModule`; `Pref`/`Crypto`/`SettingsSerializer` in
   `core/data`; headers in `core/network/headers`; feature DataStores, `LandingApi`/`SetupApi`
   and `ServerDataCache` provided by the features' `<Name>DataModule`.
5. Done (2026-10-25): ViewModels inject repos only (plus `SessionStateHolder` and the manager
   classes): api calls go through feature repos, `FinishViewModel` uses `SetupRepo.completeSetup`,
   new `core/repo` `PrefsRepo` and `ConnectionRepo` replace `Prefs`/`SocketManager`. No DTOs in
   presentation (`ProbeNotify`, `ProbeInfoUpdate`, `PwmProfile`, `SmartStartProfile` instead).
6. Done (2026-10-25): `RemoteConfigRepository(Impl)` renamed `RemoteConfigRepo(Impl)`.
7. Done (2026-10-25): JUnit/MockK/coroutines-test/Turbine, first unit tests, a Konsist
   `ArchitectureTest` for the CLAUDE.md rules, and Spotless/ktlint from `.editorconfig` (the
   reformat commit is in `.git-blame-ignore-revs`).

## Bugs: presentation
- [picker] core/ui/components/PickerWheel.kt: scroll offset is read in composition to
  interpolate size/color, so every scroll frame recomposes the wheel. Move it to
  `graphicsLayer`/draw.

## Conventions to fix
- SetupViewModel is a plain `ViewModel`, not `BaseViewModel`.
- Duplication: each feature repo repeats get -> parse -> session -> DataStore; Landing/Setup repeat
  HTTP error mapping 3x; `SettingsRepoImpl` is ~100 one-line forwards. Extract shared helpers.
