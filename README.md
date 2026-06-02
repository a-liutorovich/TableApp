# TestTableApp

An Android tablet app that generates interactive dynamic tables from user input.

## Requirements

- Android Studio Meerkat (2024.3) or newer
- Android SDK 26+
- A tablet emulator or device (the layout is optimized for large screens)

## How to run

```bash
# Debug build
./gradlew assembleDebug

# Unit tests
./gradlew test
```

Or open the project in Android Studio and press **Run**.

## Architecture

Clean Architecture + MVVM across three Gradle modules:

| Module | Responsibility |
|---|---|
| `:data` | `TableRepositoryImpl` (in-memory `MutableStateFlow`), random string generation |
| `:domain` | Use cases: generate, get, save table state; toggle highlight; update cell text |
| `:presentation` | Input screen, Table screen, ViewModels, Compose Navigation |

Dependency direction: `:presentation` → `:domain` ← `:data`

## Tech stack

| Tool | Version |
|---|---|
| Kotlin | 2.3.21 |
| Jetpack Compose BOM | 2026.05.01 |
| Navigation Compose | 2.9.8 (type-safe `@Serializable` routes) |
| Koin | 4.0.4 |
| Coroutines | 1.11.0 |
| kotlinx-collections-immutable | 0.3.8 |

## Key design decisions

### Incremental UI state mapping (`TableViewModel`)
Instead of rebuilding all 6 000 `CellUiState` objects on every toggle, `scan` is used to diff the previous UI state against the new domain state: only the one changed cell and its row are reallocated. `flowOn(Dispatchers.Default)` moves this work off the main thread. Compose's strong-skipping mode skips recomposition for reference-equal items.

### Process-death recovery
`TableRepositoryImpl` is in-memory only. After a process kill, Navigation restores the back stack with `TableScreenRoute(rows, cols)`. `TableViewModel.init` reads these arguments from `SavedStateHandle` and regenerates the table if the repository is empty. This gives deterministic recovery with no persistence overhead.

### Single-edit-mode contract
Only one cell can be in edit mode at a time. A `closeEditing` lambda is hoisted to the screen level: tapping any cell first commits the active editor before opening a new one. This avoids managing a `focusedCellId` in the ViewModel state and keeps the ViewModel free of UI-level focus concerns.

### Immutable UI models + `@Stable`/`@Immutable`
`ImmutableList` from `kotlinx-collections-immutable` is used for rows and cells so Compose's compiler plugin can treat them as stable and skip recomposition when references are unchanged.

## Trade-offs and conscious omissions

**No persistence (DataStore / Room / file)**
The table data is generated on demand and recovered via re-generation on process death. Persisting up to 6 000 cells with their edited text would require a serialization layer and a migration story. For a test task this adds noise without demonstrating anything architectural — re-generation is semantically equivalent given that the initial data is random. If the user edits cell text and the process is killed, those edits are lost; this is a known limitation documented below.

**No pagination / virtual scrolling beyond `LazyColumn`**
The max data size is fixed at 6 × 1 000 = 6 000 cells. `LazyColumn` with stable keys handles this comfortably on modern hardware; a more complex paging layer would be over-engineering for this scale.

**`MutableStateFlow` instead of `Channel` for inter-layer communication**
`StateFlow` makes the current state inspectable at any time (`repository.get()`), which simplifies both the ViewModel init recovery logic and the unit tests.

## Known limitations

- **Edited text is lost on process death.** Only the grid dimensions survive (re-generated from route args). Persisting per-cell edits is out of scope — see above.
- **Tablets only.** No adaptive layout for phones; the table's horizontal scroll assumes a wide viewport.
- **No release signing config.** `isMinifyEnabled = false` in release build type; intended for evaluation only.
