# PiFire Android

Android companion app (Kotlin, Jetpack Compose, MVI, Hilt, DataStore, Socket.IO) for
[PiFire](https://github.com/nebhead/PiFire), the Raspberry Pi smoker/grill controller. **The
backend is not ours.** Every user self-hosts their own PiFire server, at whatever version they
installed, and connects to it by URL (LAN or reverse proxy, optional basic auth and extra headers,
sometimes self-signed certs). We only ship the app, so anything that would normally be decided
server-side (app updates, which server versions are supported, feature gating) is driven by
Firebase Remote Config instead.

**This is a public GPL repo.** Never put secrets or personal details in anything committed (code,
`CLAUDE.md`, `BACKLOG.md`, docs, commit messages, PR text): no API keys, DSNs, OneSignal/Firebase/
Sentry IDs, keystore details, emails, or personal domains and server addresses. Keys go in
`local.properties` only; use placeholders like `example.com` or `192.168.1.x` in docs and previews.

Follow the rules below in new and edited code; don't refactor unrelated files unless asked.
Write everything in American English (code, identifiers, comments, strings, docs, commit messages).
Standing decisions, known bugs and parked work are in `BACKLOG.md`: check it before
changing an area it lists, and add new follow-ups there.

**Migration in progress.** The code predates these rules. The package layout below is in place,
but some code still breaks the rules inside it (dispatchers in ViewModels, screens not yet in the
five-part pattern; see `BACKLOG.md`). When
you edit a file, make the touched code follow the rules, but do larger moves only as part of a
planned restructure step (tracked in `BACKLOG.md`), one feature at a time, so diffs stay
reviewable.

## Build & verify
Windows machine; use `./gradlew` from the repo root. Flavors are `github` and `playstore`
(dimension `version`); build types `debug` (`.debug` suffix) and `release` (R8 + resource shrinking).

| What | Command |
|---|---|
| Compile both flavors | `./gradlew :app:compileGithubDebugKotlin :app:compilePlaystoreDebugKotlin` |
| Lint | `./gradlew :app:lintPlaystoreDebug` |
| Format (ktlint) | `./gradlew spotlessApply` (CI-style check: `spotlessCheck`) |
| Unit tests (incl. `ArchitectureTest`) | `./gradlew :app:testPlaystoreDebugUnitTest` |
| Release build | `./gradlew :app:assemblePlaystoreRelease :app:assembleGithubRelease` |

- Always compile **both** flavors. `AppConfig.IS_PLAY_BUILD` switches behavior at runtime (Play
  in-app updates vs the GitHub release link), so a change can compile in one and misbehave in the
  other.
- Keys (`SENTRY_DSN`, `ONESIGNAL_APP_ID`) live in `local.properties`, with empty fallbacks in the
  committed `local.defaults.properties`, and reach code as `BuildConfig.*`; an empty value turns
  the integration off. Add new keys to both files. One `app/google-services.json` serves every
  flavor (same Firebase project). One Sentry project serves every flavor, split by
  `environment = <flavor>-<buildType>`. Never commit `local.properties`, `google-services.json`
  or keystores.
- After changing DTOs, `@Serializable` models, DataStore serializers, or anything Hilt/Firebase
  finds at runtime, build a release APK and add keep rules to `app/proguard-rules.pro`, grouped and
  commented.
- Unit tests (JUnit 4, MockK, coroutines-test, Turbine) live in `app/src/test`, mirroring the main
  package. `architecture/ArchitectureTest` (Konsist) checks the layout and dependency rules below;
  keep it passing, and when a rule needs an exception, add it there with a comment saying why.
  Add tests for new pure logic (mappers, parsing, validation, helpers).
- Formatting is Spotless + ktlint with the rules in `.editorconfig` (android_studio style, 120
  columns, trailing commas). Run `./gradlew spotlessApply` before finishing; after editing
  `.editorconfig`, run `./gradlew --stop` so the daemon reloads it. Beyond what ktlint enforces,
  keep `modifier = Modifier` on one line and then one call per line once a chain has two or more
  calls.

## Project layout
App code is in `app/src/main/java/com/weberbox/pifire/`. Target layout:

```
application/                # App, MainActivity, MainViewModel
core/
├── common/                 # model/ (DataError, Result, Mapper), util/ (plain Kotlin, UiText)
├── data/                   # pref/ (Prefs), crypto/, datastore/, util/
├── di/                     # App-wide Hilt modules (Json, DataStore, Socket, Retrofit, RemoteConfig, ...)
├── log/                    # Timber trees, Sentry
├── model/                  # Models several features use (Server, SemanticVersion, TempUnit, ...)
├── network/                # SocketManager, AckWithTimeout, response parser, shared DTOs, headers
├── notification/           # OneSignal
├── remoteconfig/           # RemoteConfigRepo, UpdateManager, ServerSupportManager, FeatureSupportManager
├── repo/                   # Repos used by several features (e.g. server/settings data)
├── session/                # SessionStateHolder
└── ui/                     # base/, components/, icons/, state/, theme/, util/ (Compose helpers)
feature/<name>/             # changelog, dashboard, events, home, info, landing, pellets, recipes, settings, setup
├── data/
│   ├── api/                # <Name>Api + <Name>ApiImpl: socket/HTTP calls to the PiFire server
│   ├── di/                 # <Name>DataModule (@Binds for the feature's repos and apis)
│   ├── mapper/             # DTO -> data model
│   ├── model/              # DTOs (<Name>Dto) and data models
│   ├── repo/               # <Name>Repo + <Name>RepoImpl
│   └── serializer/         # DataStore serializers
├── domain/                 # Optional: use cases for shared/complex logic only
└── presentation/
    ├── components/         # Feature composables, sub-folders allowed (e.g. components/sheets)
    ├── contract/           # <Feature>Contract
    ├── mapper/             # data model -> UI model
    ├── model/              # UI models used only by this feature
    └── screen/             # <Feature>Screen + <Feature>ViewModel (sub-folders allowed, e.g. settings/screen/probe)
navigation/                 # NavRoutes, NavTypes, RootNavGraph, NavHostUtils
```

- Folder names are always `components`, `screen`, `model`, `util` (never `component`, `screens`,
  `models`, `utils`). Helpers go under the layer using them (`data/util`, `presentation/util`).
- Screen files and their composables end in `Screen` (`ProbeSettingsScreen.kt`, not
  `ProbeSettings.kt`). A bottom sheet with its own ViewModel and contract is a screen: it lives in
  `screen/` and follows the MVI rules. A sheet that only takes state and lambdas is a component.

### Dependency direction
- `feature` may import `core`. **`core` never imports `feature`.**
- A feature imports another feature only for navigation or a deliberately shared composable. If
  several features need a model or repo, it belongs in `core`.
- `data` never imports `presentation`. Repos and DataStore serializers work with data models, not
  UI models.
- Plain-Kotlin helpers go in `core/common/util`; anything needing Compose/Android UI in `core/ui`.

### Where code goes
- **Server calls**: only `*ApiImpl` talks to `SocketManager` or Retrofit. Only `*RepoImpl` calls
  an `*Api`. **ViewModels inject repos only**: never an `*Api`, `*ApiImpl`, `SocketManager`,
  `DataStore` or `Prefs` directly. A ViewModel needing a server action gets a repo method for it.
- **Repos**: in the feature's `data/repo/`, or `core/repo/` if two or more features use it. Name
  them `<Name>Repo` / `<Name>RepoImpl` (never `*Repository`), `@Binds` them in
  `feature/<name>/data/di/<Name>DataModule` (or `core/di/RepoModule` for core repos). Repos return
  `Result<T, DataError>`; they catch and map exceptions, nothing throws into a ViewModel.
- **Models**: used in one file, keep them there (`private` where possible); across one feature,
  in its `data/model/` or `presentation/model/`; by several features, in `core/model/`. Anything an
  API returns or DataStore stores is `data`; anything that exists only to render UI is
  `presentation`.
- **ViewModels and composables never use DTOs** (`*Dto`, `PostDto.*`). Map them to data models in
  the repo or `data/mapper`. A data model may go straight to the UI when no UI-only shape is needed.
- **No full domain layer.** Add a use case (`domain/<Verb><Noun>UseCase.kt`) only when the logic is
  shared by more than one ViewModel or is complex enough to need its own tests. Never one that only
  forwards a repo call. Mappers are not domain code.

## Backend compatibility (PiFire servers we don't control)
- Assume any server version since the oldest one Remote Config supports. Never "fix" something by
  expecting a backend change; if PiFire behavior must change, note it in `BACKLOG.md` as an
  upstream request.
- **One bad field must not break a payload.** DTO fields are nullable with defaults, and the shared
  `Json` (`core/di/JsonModule`) keeps `ignoreUnknownKeys`, `coerceInputValues`, `explicitNulls =
  false`. Temperatures and other numbers can arrive as Int or Double (Celsius servers send
  decimals): use `Double` or `IntOrDoubleSerializer`, never a bare `Int` for a temp. Never parse
  server strings with `toInt()`/`toDouble()`; use `toIntOrNull()`/`toDoubleOrNull()` with a fallback.
- Features that only exist on some server versions go behind `FeatureSupportManager`
  (`Feature` + `FeatureGate`); legacy handling is marked with `@Compatibility`. Keep the supported
  server range in Remote Config `server_support_config`, not in code.
- Map network failures to specific `DataError.Network` values (timeout, unreachable host,
  certificate, auth) so the user learns what to fix on *their* server; never show raw exception text.
- Never log credentials, auth headers, or a URL that may contain `user:pass@`. Timber logs become
  Sentry breadcrumbs.

## Firebase Remote Config (our "backend")
- Remote Config keys `app_update_config`, `server_support_config`, `feature_support_config` drive
  app updates, the supported PiFire version range and feature gating. Defaults ship in
  `app/src/main/assets/config_*.json`; keep them in sync with the console when the schema changes.
- Old app versions in the wild read the same keys: only add fields (with defaults), never rename
  or remove one without a new key.
- If a fetch or decode fails, fall back to the bundled defaults, never crash or block the UI.
  Fetch once per need, not on every state emission.
- Updates: `playstore` uses Play in-app updates (IMMEDIATE/FLEXIBLE from Remote Config); `github`
  opens the GitHub release link. Both paths go through `UpdateManager`.

## Core rules

### 1. No hardcoded UI strings
- All user-facing text (labels, buttons, dialogs, hints, content descriptions, notifications, unit
  suffixes, default names like "Grill"/"Probe") goes in the matching `res/values/strings_<area>.xml`
  file with snake_case keys, using `plurals` for counts. Add a new `strings_<area>.xml` only for a
  new area.
- In composables, `stringResource(id = R.string.x)`. In ViewModels, contracts, repos and mappers,
  `UiText(resId = R.string.x)` or `UiText(resId = ..., args = uiTextArgsOf(...))`.
- `UiText(value)` (DynamicString) is only for server-provided or user-entered text.
- Not user-facing, so stays as literals: log messages, routes/keys, socket event names, animation
  labels, `@Preview` sample data.

### 2. Colors come from the theme
- `MaterialTheme.colorScheme.*` or the app's extended theme colors. New colors go in
  `core/ui/theme/Color.kt` with light **and** dark values, wired through `Theme.kt`.
- Allowed exceptions: `Color.Transparent`, icon vector definitions, and a color that must stay the
  same in both themes, as a named `private val` at the top of the file, never inline `Color(0x...)`.
- Mappers and data models don't hold Compose `Color`s; they carry a semantic value (enum/type) the
  UI turns into a color.

### 3. Sizes
- `MaterialTheme.spacing` (`Spacing.kt`) for padding and gaps, `MaterialTheme.size` for repeated
  component sizes. Other one-off sizes are raw `dp` inline (a top-of-file `private val` if it
  repeats there). Text uses `MaterialTheme.typography.*`, shapes `MaterialTheme.shapes.*`. Don't add
  to `dimens.xml`.

### 4. Kotlin style
- Named arguments for every call with more than one argument, and for any argument whose meaning
  isn't obvious. Exceptions: trailing lambdas, stdlib (`listOf`, `mapOf`, `require`), and Java APIs
  where Kotlin forbids names (Socket.IO, OkHttp).
- Trailing commas in multi-line parameter and argument lists. 4-space indent.
- Import types; no inline fully qualified names (except for a name clash), no unused imports. Use
  `javax.inject`/`dagger` imports, never shaded copies (`com.google.android.datatransport...dagger`,
  `jakarta.inject`).
- `private` by default; `data object` for stateless sealed members. No `!!` (handle null or
  `requireNotNull` with a message); generated icon vectors are the only exception.
- Coroutines: `viewModelScope` or an injected scope, never `GlobalScope`. ViewModels don't switch
  dispatchers; repos/apis do their own `withContext(Dispatchers.IO)`, so `setState`/`setEffect`
  always run on Main. Every `suspendCancellableCoroutine` / ack wait has a timeout and resumes on
  every path. `callbackFlow` always ends with `awaitClose`; use `map`/`mapNotNull` when no callback
  is involved.
- Collect flows lifecycle-aware in Compose (`collectAsStateWithLifecycle`).
- Logging uses Timber, never `println` or `Log.*`.
- Comments explain *why*, briefly; no narration, commented-out code or stale TODOs.

## MVI pattern
Every screen uses `BaseViewModel<Event, State, Effect>` (`core/ui/base`).

- **Contract**: `presentation/contract/<Feature>Contract.kt`, holding sealed `Event : ViewEvent`,
  `data class State : ViewState` and sealed `Effect : ViewSideEffect`, with
  `Notification(message: UiText, isError: Boolean = false)` and a nested sealed `Navigation`
  (`Back`, `NavRoute(route: Any, popUpToRoute: Any? = null, inclusive: Boolean = true)`).
  Events are things the ViewModel handles; purely local UI toggles (open a sheet) stay in the
  screen instead of becoming events the ViewModel ignores.
- **ViewModel**: next to the screen; `@HiltViewModel`, `@Inject constructor` (repos only),
  overrides `setInitialState()` / `handleEvents(event)`, uses `setState { copy(...) }` / `setEffect`.
  Every load path ends loading/refreshing flags on both success and error, and success clears error
  flags. Check every `Result`; never fire a follow-up action after an ignored failure.
- **State** holds no ticking values (timers) that would recompose the whole screen every second;
  give them their own state read only by the composable showing them.

**Screen**: `presentation/screen/<Feature>Screen.kt` has five parts:
1. `<Feature>ScreenDest`: public. Gets the ViewModel (`hiltViewModel()`) and maps
   `Effect.Navigation` to `navController.navigateFrom(...)` / `popBackStackFrom(...)`
   (`navigation/NavHostUtils.kt`) with `from = rememberSourceEntry()`, which drops effects from a
   screen no longer on top (no double push/pop). Never raw `navigate` / `popBackStack`.
2. `<Feature>Screen`: private and stateless. Calls `HandleSideEffects` and holds the `Scaffold`.
3. `<Feature>ScreenContent`: private, layout only, no ViewModel references.
4. `HandleSideEffects`: private. `LaunchedEffect(SIDE_EFFECTS_KEY)` collects effects, shows
   notifications and forwards navigation.
5. Previews: `@Preview` in light and dark (`UI_MODE_NIGHT_YES` / `NO`), wrapped in `PiFireTheme`,
   and `private`.

Other screen rules:
- Reusable composables go in `presentation/components/` (or `core/ui/components/` if shared) and
  take state and lambdas, never a ViewModel.
- Navigation results (`navBackWithResult`) are read from the receiving screen's own back stack
  entry; a ViewModel scoped to a nav graph (`sharedViewModel`) doesn't see them through its
  `SavedStateHandle`.
- Dialog and sheet button actions are dispatched as events to the ViewModel, not run as suspend
  lambdas in a composable scope that dies with the dialog.
- `remember` keys every input it derives from; per-item state in a loop is wrapped in `key(item)`;
  `LazyColumn` items have stable `key`s from the data (never `UUID.randomUUID()` in a mapper).
- Heavy work (Base64 image decoding, parsing) runs off the main thread, not in composition.
- Numeric input goes through `InputValidationSheet` with matching keyboard and validation (integer
  vs decimal, locale decimal separator); parse the result with `toIntOrNull()`/`toDoubleOrNull()`.

## Data & persistence
- No Room. Cached server data and app settings are in Jetpack DataStore with kotlinx serializers
  (`data/serializer`); small prefs in `Prefs`. Encrypted values go through `Crypto`.
- A DataStore serializer that fails to read throws `CorruptionException` (handled by
  `ReplaceFileCorruptionHandler`); it never silently returns defaults that the next write would
  persist over the user's saved servers.
- When a stored model changes shape, add fields with defaults so existing installs still decode.
- Multiple servers are supported; anything server-specific is keyed by the server UUID and read
  through flows that follow `currentServerUuid`, not a value captured once.

## Commits
- Only commit when asked. Work on `development`; PRs go to `master`.
- Create PRs to `master` only when asked; never merge them (the user reviews and merges, using a
  merge commit, not squash).
- Message style: a one-line summary, then sections (e.g. `Dashboard`, `Settings`, `Setup`,
  `Under the hood`) of short `- ` bullets describing user-visible effect. Note Remote Config
  changes that must be made in the Firebase console under `Remote Config:`.
- **`PENDING_CHANGES.md`** (repo root, gitignored) logs uncommitted work across sessions. After
  finishing a change, add commit-ready bullets under the matching section heading (create the file
  or heading if missing). Update or remove earlier bullets this session changed or reverted. For a
  commit message, build it from this file, checked against `git status` / `git diff --stat`;
  empty the file after the commit succeeds.

## Checklist before finishing a task
- [ ] Both flavors compile. Release build checked if DTOs, serialization or reflection changed.
- [ ] Unit tests pass, including `ArchitectureTest`; `spotlessCheck` passes.
- [ ] ViewModels inject repos only; no DTOs in ViewModels, contracts or composables.
- [ ] DTO changes tolerate missing, null, Int-vs-Double and unknown fields from any PiFire version.
- [ ] No hardcoded UI strings or colors; spacing uses `MaterialTheme.spacing`.
- [ ] Touched files are in the right package per "Where code goes" (or the move is in `BACKLOG.md`).
- [ ] Screen follows the five-part pattern; previews in light and dark.
- [ ] Navigation goes through `navigateFrom` / `popBackStackFrom`.
- [ ] Loading flags reset on every path; every `Result` checked.
- [ ] Named arguments, imports clean, no `!!`, no dead code left behind.
