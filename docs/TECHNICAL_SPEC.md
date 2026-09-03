# TeamScore (Rev4) — Technical Specification

**Package:** `com.golfpvcc.teamscore_rev4`
**Module layout:** single Gradle module (`:app`), no multi-module / KMP split
**Platform:** Android, Jetpack Compose, single-user, offline-first, local-only persistence
**Revision label in code:** `2.1064.98`

## 1. Purpose

TeamScore is a single-Activity Android app used by a golf group to run one round of team-format golf at a time: set up a course, set up up to 5 players, enter hole-by-hole gross scores during the round, and view a computed summary (net, Stableford, points quota, "6-6-6", "Nines" side-game payouts, and "junk" side bets) at the end. The summary can be emailed via the device's mail app, and the whole SQLite database can be backed up/restored to a user-chosen file via the Storage Access Framework (SAF).

There is no backend, no network I/O, and no multi-user/account model — everything is local to the device.

## 2. High-Level Architecture

```
                    ┌───────────────────────────┐
                    │        MainActivity        │  single Activity, hosts Compose tree
                    └──────────────┬─────────────┘
                                   │ setContent { TeamScore_Rev4Theme { TeamScoreApp() } }
                                   ▼
                    ┌───────────────────────────┐
                    │   SetupNavGraph (NavHost)   │  Jetpack Navigation-Compose
                    │   route = "root"            │
                    └──────────────┬─────────────┘
              ┌────────────────────┴─────────────────────┐
              ▼                                            ▼
  nested graph "Configuration"                  nested graph "GameOn" (start destination)
  ┌─────────────────────────┐                   ┌─────────────────────────────┐
  │ CoursesScreen             │                   │ SummaryScreen (start dest)   │
  │ CourseDetailScreen?id=    │                   │ ScoreCardScreen               │
  │ PlayerSetupScreen?id=     │                   │ "Exit" (process.exitProcess) │
  └─────────────────────────┘                   └─────────────────────────────┘
```

Each screen follows the same pattern:

* A `@Composable` **Screen** function (stateless UI, driven by a `state` snapshot object)
* A **ViewModel** (`androidx.lifecycle.ViewModel`, backed by a custom `ViewModelProvider.Factory`, no DI framework/Hilt/Koin — factories call `TeamScoreCardApp.getXxxDao()` directly)
* A single mutable Compose `State` holder per screen: `var state by mutableStateOf(ScreenState())`, mutated in place, with an explicit `repaintScreen()` helper (`state = state.copy(mRepaintScreen = !mRepaintScreen)`) used as a manual recomposition trigger for structures Compose can't observe (raw `IntArray`s inside data classes)
* An **Action** sealed class/enum per screen (e.g. `ScoreCardActions`, `DialogAction`, `SummaryActions`) dispatched from the UI into a single `xxxActions(action)` router method on the ViewModel — a light MVI-flavored pattern, hand-rolled rather than the team's usual MVI skill conventions (no `Event`/`ObserveAsEvents`, no `SavedStateHandle` use for process death)

There is **no repository layer** — ViewModels call Room DAOs directly (`TeamScoreCardApp.getScoreCardDao()`, etc.), and DAOs return domain records with `IntArray`/`String[]` fields converted to/from CSV strings by Room `TypeConverter`s. There is no domain/UseCase layer either; scoring math lives in `ui/screens/summary/utils/*` and `ui/screens/scorecard/utils/*` as plain top-level functions operating on the ViewModel's Compose state.

## 3. Module / Package Structure

```
app/src/main/java/com/golfpvcc/teamscore_rev4/
├── MainActivity.kt                     Single Activity entry point
├── TeamScoreCardApplication.kt         Application subclass; Room singleton + static DAO accessors
├── database/
│   ├── model/          Room @Entity data classes (7 tables) + ScoreCardWithPlayers (relation)
│   ├── dao/             Room @Dao interfaces, one per entity
│   ├── converters/       DataConverter — IntArray <-> CSV string, Array<String> <-> CSV string
│   └── room/
│       ├── TeamScoreDatabase.kt        @Database (version 5, exportSchema = false)
│       ├── WriteReadExternalFile.kt    Raw file-copy backup/restore of the .db file
│       └── BackupLaunchers.kt          SAF ActivityResultLauncher composables
├── ui/
│   ├── navigation/       NavRoutes, SetupNavGraph, two nested NavGraphBuilder extension fns
│   ├── theme/            Material3 theme, colors, typography
│   └── screens/
│       ├── courses/          Course list CRUD (CoursesScreen + CourseViewModel)
│       ├── coursedetail/     Course create/edit (par/handicap/notes per hole)
│       ├── playersetup/      Player roster + handicaps for the round
│       ├── scorecard/        Hole-by-hole score entry (the core gameplay screen)
│       │   ├── dialogenterscore/   Modal dialog for entering one hole's scores
│       │   ├── displayoptions/     Popup menu to switch score display mode
│       │   └── utils/              Scoring/display helper functions (pure functions on state)
│       └── summary/          End-of-round summary, payouts, junk, points, email, backup/restore
│           └── utils/         Score calculation engines + email body builder
└── utils/
    ├── Constants.kt         All magic numbers/colors/limits, DATABASE_NAME
    ├── PointsTableUtils.kt  Seeds default PointsTable rows
    └── TeamObjects.kt       Misc shared value objects
```

## 4. Data Layer

### 4.1 Persistence technology

* **Room** (`androidx.room:room-runtime` 2.8.2) over SQLite, single database file `TeamDatabase.db`.
* `RoomDatabase.JournalMode.TRUNCATE` (not WAL) — chosen so the on-disk `.db` file is always a complete, self-contained snapshot, which the manual backup/restore feature depends on (WAL would leave data in a separate `-wal` file).
* `fallbackToDestructiveMigration()` — **no formal Room `Migration` objects are defined**; any schema version bump wipes and recreates all tables. Current `@Database(version = 5, ...)`.
* `exportSchema = false` — no schema JSON history is checked into the repo.
* The database instance is a hand-rolled singleton on `TeamScoreCardApp` (Application subclass), not injected via a DI graph:

```kotlin
object TeamScoreCardApp {              // simplified
    fun getRoomDatabase(): TeamScoreDatabase
    fun getCourseDao(): CourseDao
    fun getScoreCardDao(): ScoreCardDao
    fun getPlayerDao(): PlayerDao
    fun getPointsDao(): PointsDao
    fun getJunkDao(): JunkDao
    fun getEmailDao(): EmailDao
    fun getPlayerJunkDao(): PlayerJunkDao
    fun closeTeamScoreDatabase()        // required before raw file copy in backup/restore
}
```

### 4.2 Schema (7 tables)

| Table | Entity | Primary Key | Notes |
|---|---|---|---|
| `CourseTable` | `CourseRecord` | `mId` (autoGenerate) | `mCoursename`, `mUsstate`, `mPar: IntArray[18]`, `mHandicap: IntArray[18]`, `mNotes: Array<String>[18]` — arrays stored as CSV via `DataConverter` |
| `ScoreCardRecord` | `ScoreCardRecord` | `mScoreCardRecId` (**not** auto-generated — see §4.3) | `mCourseName`, `mTee`, `mDatePlayed`, `mCurrentHole`, `mCourseId` (FK by convention, no `@ForeignKey`), `mPar: IntArray[18]`, `mHandicap: IntArray[18]` |
| `PlayerRecord` | `PlayerRecord` | `mId` (**not** auto-generated) | `mName`, `mHandicap: String`, `mScore: IntArray[18]`, `mTeamHole: IntArray[18]`, `mScoreCardRecFk: Int` (logical FK to `ScoreCardRecord.mScoreCardRecId`) |
| `PointsTable` | `PointsRecord` | `mId` (autoGenerate) | `mPoints: Int`, `label: String` — one row per named point value (e.g. quota target, birdie bonus) |
| `JunkTable` | `JunkRecord` | `mId: Long` (autoGenerate) | `mJunkName` — catalog of side-bet ("junk") types, e.g. "Greenie", "Sandy" |
| `EmailTable` | `EmailRecord` | `mId` (autoGenerate) | `mEmailName`, `mEmailAddress` — recipient(s) for the score summary email; app seeds one default row on first run |
| `PlayerJunkTable` | `PlayerJunkRecord` | `mId: Long` (autoGenerate) | `mPlayerIdx`, `mHoleNumber`, `mJunkId` — join table: which player won which junk on which hole |

`ScoreCardWithPlayers` is a Room `@Relation` projection (not a table): embeds one `ScoreCardRecord` plus its `List<PlayerRecord>` joined on `mScoreCardRecId == mScoreCardRecFk`.

**Type conversion:** all `IntArray` and `Array<String>` entity fields are persisted as comma-joined `TEXT` columns via `DataConverter` (`arrayToString`/`stringToArray`, `stringArrayToString`/`stringToStringArray`). There is no JSON serialization and no validation of array length on read — a malformed CSV string (e.g. empty) will throw on `stringToArray`/`toInt()`.

### 4.3 Single-round data model (important design constraint)

The app supports **exactly one "current" score card at a time**, keyed by the hardcoded constant `SCORE_CARD_REC_ID = 2024` (`utils/Constants.kt`). `ScoreCardDao`/`PlayerDao` upserts always target this fixed ID rather than an app-generated round ID, and `mId`/`mScoreCardRecId` are declared `autoGenerate = false`. There is no history of past rounds — starting a new round overwrites the previous one in place. `CourseTable`, `PointsTable`, `JunkTable`, `EmailTable`, and `PlayerJunkTable` are the only tables that accumulate multiple rows over time.

### 4.4 Backup / Restore ("API" surface for external data exchange)

There is no network API in this app. The closest thing to an external "API" is raw file I/O via the Android **Storage Access Framework**:

* `BackupLaunchers.kt` exposes two `@Composable` factory functions returning `ManagedActivityResultLauncher<Intent, ActivityResult>`:
  * `buildBackupLauncher()` — wraps `ActivityResultContracts.StartActivityForResult()`; caller is expected to launch an `ACTION_CREATE_DOCUMENT` intent (created in the Summary screen's backup/restore dialog), and on result opens an `OutputStream` via `ContentResolver` and calls `WriteReadExternalFile.writeBackupFile()`.
  * `buildRestoreLauncher()` — same pattern with `ACTION_OPEN_DOCUMENT` and `readBackupFile()`.
* `WriteReadExternalFile` (in `database/room/`):
  * `writeBackupFile(OutputStream)`: calls `TeamScoreCardApp.closeTeamScoreDatabase()` to release the Room connection, locates the live `.db` file via `context.getDatabasePath(DATABASE_NAME)`, and streams a raw byte-for-byte copy to the caller-supplied `OutputStream` (8 KB buffer). Shows a `Toast`.
  * `readBackupFile(InputStream)`: same teardown, then streams the input over the top of the live `.db` file path, effectively replacing the database file on disk. The Room instance is re-created lazily on next `TeamScoreCardApp.getXxxDao()` call.
* This is a **destructive, unversioned, non-atomic file copy** — there's no checksum/format validation of the restored file, and a failure mid-copy can corrupt the live database. `journalMode = TRUNCATE` is what makes a single-file copy sufficient (no separate `-wal`/`-shm` files to also copy).
* Manifest declares `android:requestLegacyExternalStorage="true"` and `android:allowBackup="true"` (standard Android auto-backup is also enabled, separate from this manual mechanism).

### 4.5 Email "API"

`EmailScores.kt` builds an implicit `Intent(Intent.ACTION_SENDTO)` with `data = mailto:`, `EXTRA_EMAIL`, `EXTRA_SUBJECT`, `EXTRA_TEXT` and starts it via `context.startActivity`. This hands off to whatever mail app is installed — TeamScore does not send email itself. The manifest registers a matching `SENDTO`/`mailto` intent-filter on `MainActivity` (so the app itself could theoretically be picked as a target, though this appears to be a residual/no-op declaration rather than an intentional inbound flow). The message body is built by `getSpreadSheetScore()`, which serializes one player's 18-hole gross scores plus course par as a comma-separated line (front-nine subtotal, back-nine subtotal, 18-hole total) intended to be pasted into a spreadsheet.

## 5. Navigation / Screen Flow

Navigation is Jetpack **Navigation-Compose** with two nested graphs under a root `NavHost` (`SetupNavGraph`, start destination = `GameOn`):

| Route constant | Screen | Args |
|---|---|---|
| `Courses` | `CoursesScreen` | — |
| `CourseDetail?id={id}` | `CourseDetailScreen` | `id: Int` (default `-1` = new course) |
| `PlayerSetup?id={id}` | `PlayerSetupScreen` | `id: Int` (default `-1`) |
| `SummaryScreen` | `SummaryScreen` | *(declares an `id` arg but no caller passes one; app always reads the single fixed `SCORE_CARD_REC_ID` row)* |
| `ScoreCardScreen` | `ScoreCardScreen` | *(same — `id` arg unused in practice)* |
| `Exit` | *(no UI)* | Composable body calls `kotlin.system.exitProcess(-1)` directly during composition — a hard process kill, not a graceful `finish()` |

Typical user flow: `SummaryScreen` (default landing screen) → `Courses`/`CourseDetail` to manage the course library → `PlayerSetup` to pick the round's players and handicaps → `ScoreCardScreen` to play the round hole-by-hole → back to `SummaryScreen` for computed results, side-bet ("junk") tracking, points-table edits, email, and backup/restore.

## 6. Screen-by-Screen Responsibilities

### 6.1 Courses / CourseDetail
* `CoursesViewModel` exposes `LiveData<List<CourseRecord>>` (only `LiveData` usage in the codebase; every other screen uses Compose `mutableStateOf` snapshots) via `CourseDao.getAllCoursesRecordAsc()`.
* CRUD: `addOrUpdateCourse()` (`@Upsert`), `deleteCourse()` (`@Delete`), `getCourseById()`.
* `CourseDetailScreen` edits a single course's per-hole par (1–18), handicap/stroke-index, and free-text notes.

### 6.2 PlayerSetup
* Lets the user pick up to `MAX_PLAYERS = 5` players and set each one's handicap for the round. Persists via `PlayerDao` keyed to the fixed `SCORE_CARD_REC_ID`.

### 6.3 ScoreCardScreen (core gameplay)
* `ScoreCardViewModel.state: ScoreCard` holds: per-player `PlayerHeading` (name, handicap, 18-hole gross score array, "team hole" mask array used for the team-scoring game modes, junk-count array), the course's par/handicap headers, current hole pointer, and the active display mode.
* **Display modes** (`ui/screens/scorecard/utils/*` constants): Gross, Net, Stableford, Point Quota, "6-6-6" team format (further split into front/back-nine or 1st/2nd/3rd-six sub-views), and "Nines" (3-player side game). Switching modes recomputes per-cell display values (`refreshScoreCard`) without re-reading the database.
* **Score entry** is modal: `buttonEnterScore()` opens a dialog (`EnterScoreDialog`) driven by `DialogAction` (Done, Clear, Gross/Net + long-click variants for doubled team stakes, numeric keypad `Number`, junk-selection sub-dialog, hole-note sub-dialog). On `Done`, if any player has a non-zero score for the hole, the round advances (`setShowTotalsFlag`) and `savePlayersScoresRecord()` persists both the `ScoreCardRecord` (course/date/current hole) and every `PlayerRecord` via `viewModelScope.launch(Dispatchers.IO)`.
* **Junk tracking**: `JunkTableSelection` (composed into `ScoreCard.mJunkTableSelection`) loads the `JunkTable` catalog once, then per-hole toggles create/delete `PlayerJunkRecord` join rows and immediately re-query the per-player/per-hole junk count to update the on-screen badge.
* **Hole notes**: free-text note per hole stored back onto `CourseRecord.mNotes[hole]` (so notes belong to the course, not the round) and persisted via `CourseDao.addUpdateCourseRecord`.
* Stroke allocation (who gets a handicap stroke on which hole) and team-score color coding are pure functions over `state` in `ui/screens/scorecard/utils/ScoreCardHelpFunctions.kt` / `ScoreCardUtils.kt` / `NineGame.kt`.

### 6.4 SummaryScreen
* On first read (`getScoreCardAndPlayerRecord`, gated by `mHasDatabaseBeenRead`), loads the score card + players, the points table, junk catalog, and email recipients (seeding one default `EmailRecord` if none exist), then runs the scoring pipeline:
  `calculatePtQuote()` → `calculateStableford()` → `calculateOverUnderScores()` → `calculate_6_6_6_Scores()` → `playerScoreSummary(playerJunkDao)`
  — each a pure function in `ui/screens/summary/utils/*` that reduces the raw per-hole arrays into the aggregate totals shown in `State` (front/back/total for: gross over/under, Stableford, points quota used/available, "6-6-6" sub-scores) and per-player `PlayerSummary` (eagle/birdie/par/bogey/double/other counts, Nines payout, junk payout list).
* **Dialogs** launched from this screen (all local `state` booleans, no navigation): About, Junk catalog editor (add/rename/delete `JunkRecord` rows, auto-adds a blank row if the catalog is emptied), Points table editor (edit `PointsRecord.mPoints`, with cancel restoring pre-edit values via an in-memory `oldValue`), Email composer (edit the single stored `EmailRecord`, validated with `android.util.Patterns.EMAIL_ADDRESS`, then either save or hand off to `EmailScores`/`ACTION_SENDTO`), Backup/Restore (wraps the SAF launchers from §4.4).
* `sendPlayerEmail(playerIdx, context)` builds the spreadsheet-style body (§4.5) and fires the mail intent for one player at a time.

## 7. State Management Pattern (cross-cutting)

* **No `StateFlow`/`SharedFlow`/`Flow`** collection pattern and no unidirectional `Event` channel — screens read `viewModel.state` directly (Compose snapshot state), and one-off transitions (dialog open/close, "done" advancing to next hole) are expressed as further mutations of the same `state` object plus a manual `repaintScreen()` toggle.
* Mutation is often **in-place on nested mutable fields** (e.g. `state.mPlayerHeading[playerIdx].mScore[idx] = score`) rather than through `state.copy(...)`, which only works because those fields are `var`s inside classes that are themselves fields of a `mutableStateOf` root — Compose does not automatically observe the inner array mutation, hence the explicit repaint toggle is required after almost every mutating function.
* All DB writes are dispatched with `viewModelScope.launch(Dispatchers.IO)`; nothing awaits a write's completion before continuing UI logic (writes are fire-and-forget from the caller's perspective), and most DAO *reads* used for initial screen load are called synchronously on the caller's thread (not wrapped in `Dispatchers.IO`), relying on those calls being fast/local SQLite reads.
* Each screen's ViewModel is instantiated per-`NavHost` composable via a hand-written `ViewModelProvider.Factory` (`CoursesViewModelFactor`, `ScoreCardViewModelFactor`, `SummaryViewModelFactor`) rather than DI-provided; there is no shared/nav-graph-scoped ViewModel (a commented-out `sharedViewModel()` helper in `NavHostControler.kt` suggests this was considered and abandoned).

## 8. Notable Constraints, Fixed Limits, and Risks

* `MAX_PLAYERS = 5` — arrays/UI are sized for at most 5 players; not data-driven beyond that constant.
* `HOLE_ARRAY_SIZE = TOTAL_18_HOLE = 18` — the app is hardcoded to 18-hole rounds; there's no 9-hole-only round type (only 9-hole *views* of an 18-hole card).
* `SCORE_CARD_REC_ID = 2024` — single fixed "current round" row; **starting a new round destructively overwrites the previous round's `ScoreCardRecord`/`PlayerRecord` rows** (no round history/archive).
* No Room `Migration`s — any future schema-version bump (`@Database(version = ...)`) will **destroy all local data** on upgrade via `fallbackToDestructiveMigration()`. This includes the Course library, Junk catalog, Points table, and Email recipients, not just the in-progress round.
* Backup/restore is a raw file copy with no format check — restoring a file that isn't actually a matching-schema SQLite DB will corrupt the app's data access on next read.
* `"Exit"` route calls `exitProcess(-1)` from inside Compose composition — bypasses normal Activity lifecycle teardown.
* No automated test coverage beyond a single placeholder unit test (`UtilsUnitTest.kt`, untracked in git per the working tree status) and default Espresso/JUnit scaffolding from the Android Studio template; no CI configuration in the repo.
* No obfuscation/minification, no analytics/crash reporting SDK, no network permissions in the manifest — consistent with a fully local, single-purpose utility app.

## 9. Build / Tooling

* Gradle Kotlin DSL, AGP `9.3.1`, Kotlin `2.2.10`, KSP `2.3.6` (Room annotation processing), Compose BOM `2025.10.00` (applied in `app/build.gradle.kts` via `androidx.compose.bom.v20251000`; the version catalog also declares an unused `composeBom = "2024.09.00"` alias that nothing references), Room `2.8.2`, Navigation-Compose `2.9.5`, coroutines `1.10.2`.
* Single `:app` module — no `:core`, `:data`, `:feature-*` module split despite the size of the codebase; all layers (UI, ViewModel, DAO, entity) live under one Gradle target.
