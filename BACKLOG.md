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
  Sentry project (the former playstore one) with `environment = <flavor>-<buildType>`. Left: the
  google-services plugin still fails without `app/google-services.json`; decide between a
  committed placeholder and applying the plugin only when the file exists.
- [sentry] app/build.gradle.kts `sentry { ignoredBuildTypes = release }`: the plugin skips
  release builds, so no tracing instrumentation there. Check that's intended.

## Restructure plan
One step per commit, compile all flavors after each.
1. Done (2026-10-24): `navigateFrom` / `popBackStackFrom` / `rememberSourceEntry` in `NavHostUtils`;
   contracts use `NavRoute(route, popUpToRoute, inclusive)`. Only `MainActivity` navigates raw.
2. Done (2026-10-24): `common/` merged into `core/` (`analytics`, `common/model`, `common/util`,
   `data`, `log`, `model`, `network`, `notification`, `remoteconfig`, `session`, `ui`); navigation
   in `navigation/`; `MainActivity`/`MainViewModel`/`MainContract` in `application/`. Left for
   later steps: `core` still imports feature types (`SettingsData.Server`, `HeadersManager`,
   `SettingsRepo`, `Crypto`); move them to `core` with their feature (step 3/4).
3. Done (2026-10-24): every feature in `feature/<name>/` with `components`/`screen`/`util`,
   mappers in `data/mapper`, bindings in `data/di/<Name>DataModule` (`ApiModule`/`RepoModule`
   removed), screen files and `*ScreenDestination` renamed. Left: private composables inside the
   renamed settings/landing screens still lack the `Screen` suffix (fix with the five-part pattern),
   and `core/di` (`DataStoreModule`, `StateModule`, `HeadersModule`, ...) still imports features.
4. Per feature, stop data depending on presentation: repos and DataStore serializers use data
   models, not `presentation/model` types.
5. Remove ViewModel access to apis/DataStore (see Conventions below).
6. Rename `RemoteConfigRepository(Impl)` -> `RemoteConfigRepo(Impl)`.
7. Tooling: `.editorconfig` + Spotless/ktlint (copy the reference project's config), unit tests (JUnit, MockK,
   coroutines-test, Konsist `ArchitectureTest` for the layout rules), then add them to CLAUDE.md.

## Bugs: presentation
- [picker] core/ui/components/PickerWheel.kt: scroll offset is read in composition to
  interpolate size/color, so every scroll frame recomposes the wheel. Move it to
  `graphicsLayer`/draw.

## Conventions to fix
- ViewModels using apis/DTOs: DashboardViewModel, HomeScreenViewModel, PelletsViewModel,
  SharedDetailsViewModel, RecipeDetailsViewModel, ProbeSettingsViewModel, FinishViewModel
  (`SettingsApiImpl` + DataStores). DTOs in presentation: `NotifyDto` (DashContract,
  ProbePickerSheet), `PostDto.ProbesDto` (ProbeContract, ProbeSheet).
- SetupViewModel is a plain `ViewModel`, not `BaseViewModel`.
- Hardcoded strings: LandingViewModel.kt:115 `"Smoker ${index+1}"`, PidBottomSheets.kt "Error"
  values, ManualSettings.kt:181, PwmControl.kt:219-227, default names in DashData/PelletsData,
  WeightUtil suffixes, ExportUtils.kt:299.
- Hardcoded colors: ServerItem.kt:72, PelletsLevel.kt:58,65.
- Lists without keys: BrandsDetails, WoodsDetails, ProfilesDetails, LogsDetails, Changelog,
  PipModulesDetails, LicenseDetails screens.
- Dispatchers in ViewModels (24) with `withContext(Main)` before `setEffect`: move IO into repos.
- Duplication: each feature repo repeats get -> parse -> session -> DataStore; Landing/Setup repeat
  HTTP error mapping 3x; `SettingsRepoImpl` is ~100 one-line forwards. Extract shared helpers.
- Dead code: `DashApiImpl.requestDashUpdate`, `Secrets.AWS_FCM_*`. `DashApiImpl.kt:206` default `Json`.
