# Backlog

Standing decisions, known bugs and deferred work. Format: `- [area] file: what, and the suggested fix`.
Paths are relative to `app/src/main/java/com/weberbox/pifire/` (current, pre-restructure layout).
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
1. Port `navigateFrom` / `popBackStackFrom` / `rememberSourceEntry` from the reference project into
   `NavHostUtils`; replace raw `popBackStack()` (~45 sites) and `safeNavigate`.
2. Merge `common/` into `core/` (`core/common`, `core/ui`, `core/network`, `core/session`,
   `core/remoteconfig`, ...); move `NavRoutes`/`NavTypes`/`RootNavGraph`/`NavHostUtils` to `navigation/`.
   Also move `settings/data/serializer/IntOrDoubleSerializer.kt` to `core/network` (dashboard and
   recipes DTOs use it).
3. Per feature, move to `feature/<name>/`, rename `component`->`components`, `screens`->`screen`,
   `sheets` -> `components/sheets` (or `screen/` if it has a ViewModel), DTO mappers from `domain/`
   to `data/mapper`, and split its Hilt bindings out of `core/di/{RepoModule,ApiModule}` into
   `data/di/<Name>DataModule`. Rename screen files without the `Screen` suffix.
4. Per feature, stop data depending on presentation: repos and DataStore serializers use data
   models, not `presentation/model` types.
5. Remove ViewModel access to apis/DataStore (see Conventions below).
6. Rename `RemoteConfigRepository(Impl)` -> `RemoteConfigRepo(Impl)`.
7. Tooling: `.editorconfig` + Spotless/ktlint (copy the reference project's config), unit tests (JUnit, MockK,
   coroutines-test, Konsist `ArchitectureTest` for the layout rules), then add them to CLAUDE.md.

## Bugs: data / network
- [socket] core/singleton/SocketManager.kt:83-124: `initSocket()`/`connected()` never resume when
  `socket` is null (bad URL, emit after disconnect), so callers hang. Return early and wrap waits in
  `withTimeoutOrNull`.
- [socket] SocketManager.kt:84-92: failed `initSocket` leaves an auto-reconnecting socket that later
  flips `connectedState` true. `disconnect()` before resuming false.
- [socket] common/data/util/AckWithTimeout.kt: `called` not atomic (Timer vs EventThread can
  double-resume and crash); a `Timer` thread per emit. Use `AtomicBoolean` or `withTimeout`.
- [socket] SocketManager.kt:179-197 `onFlow`: listeners bound to the current socket die after
  re-init; `trySendBlocking` on the EventThread can stall acks.
- [socket] SocketManager.kt:45: a URL with a subpath (`https://host/pifire`) becomes a Socket.IO
  namespace. Split it into `options.path = "<sub>/socket.io"`.
- [dash] dashboard/domain/DashDtoToDataMapper.kt:84,89: food probe `lowLimitShutdown =
  it.highLimitShutdown`, `lowLimitReq` falls back to `hasNotifications`. Share one probe mapper.
- [json] info/data/model/InfoDto.kt:23: `devPins` values not nullable.
- [errors] DataError.Network.CERTIFICATE never produced; SSL/host/timeout failures in
  `SetupRepoImpl`/`LandingRepoImpl` show raw `e.message`. Map exception types.
- [datastore] settings/data/serializer/HeadersSerializer.kt:89-101: only `SerializationException`
  caught; crypto/Base64 failures crash the Landing poller. Rethrow as `CorruptionException`.
- [crypto] settings/data/util/Crypto.kt:18: one shared `Cipher` across threads. Create per call.
- [settings] settings/data/repo/SettingsRepoImpl.kt:167-174,804, setup/data/util/
  ServerDataCacheImpl.kt:21-51: `getCurrentServerFlow()` captures the server UUID once; `callbackFlow`
  without `awaitClose`. Use `dataStore.data.mapNotNull { ... }`.
- [sentry] core/singleton/SentryIO.kt:39-55: try/catch outside `launch` catches nothing; async init
  misses startup crashes; server extra goes stale; session replay/screenshots may capture URLs.
- [landing] LandingViewModel.kt:153-170: auto-select doesn't check the returned UUID matches.
- [update] core/util/UpdateManager.kt:308-313: no `ActivityNotFoundException` guard; finishes the
  activity on FLEXIBLE; IMMEDIATE does nothing if Play hasn't the update yet (staged rollout).
- [di] core/di/StateModule.kt:4,40-62 and SettingsRepoImpl.kt:4: shaded
  `com.google.android.datatransport.runtime.dagger.Lazy`, and a new `ServerDataManager` per `get()`.

## Bugs: presentation
- [changelog] ChangelogViewModel.kt:38-45: error path leaves `isInitialLoading` true.
- [settings] notifications/MqttSettings.kt:236-290: port/update-seconds use a text keyboard and
  `toInt()` -> crash on "abc".
- [input] common/presentation/sheets/InputValidationViewModel.kt:74-98: validates `toDouble()` but
  callers `toInt()` (crash on "60.5"); comma decimals rejected; `min/max == 0.0` means "no limit"
  (PelletSettings.kt:298 can hit it). Integer mode, locale parsing, nullable bounds.
- [dialog] common/presentation/component/EventAlertDialog.kt:85-139: actions run in a scope that
  dies with the dialog; ConnectViewModel "Continue" can hang with the spinner on.
- [dialog] DialogController + EventDialogState.kt:50: after rotation restored dialog actions are `{}`.
- [landing] LandingViewModel.kt:166-172: non-biometric auto-select never sets `firstLaunch = false`
  -> repeated `selectServer` per poll. Also `pollerJobs` grows forever and polls behind Settings.
- [pellets] pellets/presentation/component/PelletsBottomSheets.kt:40-42: `profilesList[0]` on empty.
- [dash] DashboardViewModel.kt:266-268,338-342: ignored `Result`s; Start sent after failed hold-temp
  save; state set from IO.
- [dash] dashboard/presentation/component/ErrorCard.kt:53-55 (and WarningCard): per-item remember
  without `key(error)`.
- [dash] dashboard/presentation/util/CountDownTimer.kt:24-26, ElapsedTimer.kt:24-25: don't restart
  when end/start changes.
- [dash] DashboardScreen.kt:170-202,464: timer ticks in root state recompose the whole dashboard
  every second.
- [recipes] RecipeImageScreen.kt:85-164: pager initial page, rotation re-scroll, palette set in
  composition for page 0 only.
- [recipes] RecipeItem.kt:61-69, RecipeDetailsViewModel.kt:67-84: Base64 decoding on Main / on
  every emission. RecipesSearch.kt:90: `result?.get(0)` on empty speech result.
- [setup] OutlineFieldWithState.kt:41-52: only syncs from VM while empty (QR scan mismatch).
- [setup] ConnectScreen.kt:186-192: Next enabled while loading (double submit).
- [setup] ConnectViewModel.kt:93: lowercases the whole URL; lowercase scheme/host only.
- [state] common/presentation/state/InputModalBottomSheetState.kt:27: `dataState.value!!` after a
  failed restore.
- [picker] common/presentation/component/PickerWheel.kt:52,99-145: `% items.size` on empty list,
  `remember` without key, scroll read in composition.
- [settings] PwmSheetViewModel.kt:66-103, SmartStartSheetViewModel.kt:69-112: add after deleting all
  -> IOOBE. PwmSettingsViewModel.kt:122-123: temps and duty cycles sorted separately.
- [home] HomeScreenViewModel.kt:243-256: sign-out emitted before `OpenUpdateIntent`.
- [home] HomeScreen.kt:179-181,204-214: drawer effect not keyed; navigation after biometric dropped
  if not RESUMED.
- [dash] dashboard/presentation/util/StringUtils.kt:213-227: 0 degrees shows as "--".
- [events] events/domain/EventsDtoToDataMapper.kt:29: random UUID keys; hex colors in mapper
  (:47-62) re-parsed per composition.
- [recipes] RecipeDetailsScreen.kt:109-117: pop to and re-navigate `HomeDest` recreates Home.
- [misc] remember without keys: ProfilesEditSheet.kt:52-56, DropdownTextField.kt:88,
  RecipeDetailsScreen.kt:280. RecipeImageViewModel.kt:59 no fetch after process death.

## Conventions to fix
- ViewModels using apis/DTOs: DashboardViewModel, HomeScreenViewModel, PelletsViewModel,
  SharedDetailsViewModel, RecipeDetailsViewModel, ProbeSettingsViewModel, FinishViewModel
  (`SettingsApiImpl` + DataStores). DTOs in presentation: `NotifyDto` (DashContract,
  ProbePickerSheet), `PostDto.ProbesDto` (ProbeContract, ProbeSheet).
- SetupViewModel is a plain `ViewModel`, not `BaseViewModel`.
- Hardcoded strings: LandingViewModel.kt:115 `"Smoker ${index+1}"`, PidBottomSheets.kt "Error"
  values, ManualSettings.kt:181, PwmControl.kt:219-227, default names in DashData/PelletsData,
  WeightUtil suffixes, ExportUtils.kt:299, EventDialogState.kt:52-55.
- Hardcoded colors: ServerItem.kt:72, PelletsLevel.kt:58,65, EventsDtoToDataMapper.kt:47-62;
  Compose colors in a domain mapper.
- Lists without keys: BrandsDetails, WoodsDetails, ProfilesDetails, LogsDetails, Changelog,
  PipModulesDetails, LicenseDetails screens.
- Dispatchers in ViewModels (24) with `withContext(Main)` before `setEffect`: move IO into repos.
- Duplication: each feature repo repeats get -> parse -> session -> DataStore; Landing/Setup repeat
  HTTP error mapping 3x; `SettingsRepoImpl` is ~100 one-line forwards. Extract shared helpers.
- Dead code: `DashApiImpl.requestDashUpdate`, `SocketManager.address`, `Secrets.AWS_FCM_*`, public
  mutable `SocketManager.socket`. `DashApiImpl.kt:206` default `Json`; `SettingsApiImpl`
  `jakarta.inject`; `common/data/parser/ReponseParser.kt` misspelled.
