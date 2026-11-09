# Task 129 — Auto-hide compact scrollbar

## What we built

The library scrollbar now reserves only a compact 12.dp rail and uses a 10.dp half-pill thumb. It observes `LazyListState.isScrollInProgress`, appears during a user scroll, and hides 600ms after the list becomes idle.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `snapshotFlow` | Converts Compose state reads into a Flow of updates | An observable subscription to scroll state |
| `collectLatest` | Cancels the previous idle timer when scrolling resumes | Debounced event handling that drops stale timers |
| `LaunchedEffect` | Runs coroutine work tied to the composable lifecycle | A React effect with cleanup when dependencies change |

## Code to focus on

1. [LibraryLazyScrollbar.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt) — scroll-state observation and idle hide delay
2. [LibraryScrollbarTest.kt](../../feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt) — hidden, shown, compact, and auto-hide behavior
3. [LibraryChromeInsets.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryChromeInsets.kt) — compact trailing rail
