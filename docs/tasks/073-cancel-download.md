# 073 — Cancel an in-progress download

## Goal

A listener can **cancel** a queued or downloading song. That stops the Media3 download, removes partial audio / lyric sidecar / cover file and the Room `downloads` row (reset to not downloaded), and the Downloads / library / player UI updates.

## Approach

Phase 6 `remove()` already stops Media3 (`DownloadService.sendRemoveDownload`), deletes the cover, and deletes the Room row, but **keeps** the lyric cache (used for online playback) and the UI has no Cancel control while a download is active (library/player no-op on QUEUED/DOWNLOADING).

- Add `DownloadRepository.cancel(songPath)`: `engine.remove` (cancels in-progress audio + drops partial cache) + `coverStore.delete` + `LyricRepository.deleteCached(lyricFileName)` + `local.deleteByPath`.
- Add `LyricRepository.deleteCached` → `LyricLocalDataSource.delete` (no-op on blank).
- Downloads screen: **Cancel** on QUEUED/DOWNLOADING (keep Delete for completed/failed).
- Library row + now-playing download action: label **Cancel** while active; the action calls `cancel`.
- Non-goals: pause/resume, cancelling completed downloads (that stays Delete), changing the listen PUT.

## Files

- `docs/tasks/073-cancel-download.md` (this file)
- `core/data/.../LyricRepository.kt`, `OfflineFirstLyricRepository.kt`, `DownloadRepository.kt`, `DefaultDownloadRepository.kt`
- fakes in `core/data` tests + feature fakes
- `feature/downloads` screen, callbacks, ViewModel, strings, tests
- `feature/library` ViewModel, labels, tests
- `feature/player` ViewModel, labels, tests

## Tests

- Repository: cancel of a downloading row removes engine id, cover, lyric cache, and Room row; UI observers emit empty/not-downloaded.
- Cancel is a no-op (or does not re-delete) when there is no row.
- `deleteCached` on `OfflineFirstLyricRepository` removes the Room lyric body.
- Downloads ViewModel: `cancel` forwards to repository; Compose: Cancel button on a downloading row fires the callback.
- Library/Player ViewModel: download action on QUEUED/DOWNLOADING calls cancel.

## Done when

- Task plan committed before code.
- In-progress downloads can be cancelled from Downloads, library, and now playing.
- Partial files and the download row are gone; tests pass.
