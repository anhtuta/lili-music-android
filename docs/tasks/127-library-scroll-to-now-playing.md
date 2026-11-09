# 127 — Scroll library to now-playing song

## Goal

When the app shows Library (cold start or return from Now Playing), the list scrolls so the current song is visible.

## Approach

Pure helper for list item index of a song. `LaunchedEffect` on `nowPlayingPath` + lifecycle resume scrolls `LazyListState` to that index (account for header items).

## Files

- `LibraryListScroll.kt` (index math)
- `LibraryScreen.kt`
- JVM unit test for index math

## Tests

Unit test for index mapping; Compose/Robolectric scroll test or ViewModel-driven state with scroll assertion where stable.

## Done when

Resume and path change bring current row into view, tests pass.
