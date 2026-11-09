# 126 — Library now-playing cover badge

## Goal

Current queue song row shows animated playing or static pause GIF overlay on the cover.

## Approach

Wire `PlayerController.state` into `LibraryUiState` (`nowPlayingPath`, `isPlaying`). Add drawables under `feature/library/src/main/res/drawable/`. Overlay on `SongCover` with Coil `AsyncImage`.

## Files

- `icon_*_white.gif` → `res/drawable/`
- `LibraryUiState.kt`, `LibraryViewModel.kt`
- `LibrarySongRow.kt`
- `strings.xml` content descriptions

## Tests

ViewModel combine test; Compose test asserts overlay tag on matching row only.

## Done when

GIFs in module res, overlay toggles play/pause, tests pass.
