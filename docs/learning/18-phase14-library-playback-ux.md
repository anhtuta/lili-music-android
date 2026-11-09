# Phase 14 — Library playback UX (123–127)

## What we built

1. **No pull-dismiss (123).** Removed downward nested-scroll back from Now Playing.
2. **Search inset (124).** `LazyColumn` end padding reserves the scrollbar track so search aligns with settings.
3. **Scrollbar (125).** Thicker thumb (10.dp) and full-track drag target.
4. **Now-playing row (126).** `PlayerController` wired into `LibraryUiState`; cover overlay GIFs for play/pause.
5. **Auto-scroll (127).** On resume and when the current path changes, list scrolls to the active song.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `combine` + `PlayerController.state` | Library UI reacts to playback | Redux selector merged into catalog state |
| `LaunchedEffect` + lifecycle `RESUMED` | Scroll when screen visible again | `visibilitychange` handler scrolling into view |
| `LibraryListScroll.listIndexForSong` | Map song to lazy list index with headers | Offset rows in a virtualized table |
| Coil + `R.drawable` GIF | Animated badge on cover | `<img>` GIF overlay on thumbnail |

## Code to focus on

1. [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) — playback fields on `LibraryUiState`
2. [LibrarySongRow.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibrarySongRow.kt) — cover badge overlay
3. [LibraryListScroll.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryListScroll.kt)
4. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — no dismiss nested scroll
