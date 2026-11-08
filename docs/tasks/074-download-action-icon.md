# 074 — Download action icon button

## Goal

The per-song **Download** control is an **icon button** everywhere it appears (library rows, Now playing). Idle shows a download icon with contentDescription **Download**. While queued/downloading it shows a progress indicator (tap still **cancels**, 073). Completed shows a downloaded/check icon (`DownloadDone`). Tests find it by contentDescription.

## Approach

Web has no download control. This is an Android extra on top of 060/061/073.

Shared composable in `:core:ui` (library + player both use it):

| Status | Visual | contentDescription | Click |
| --- | --- | --- | --- |
| `NONE` | `Icons.Filled.Download` | **Download** | enqueue (existing) |
| `QUEUED` | indeterminate `CircularProgressIndicator` | **Cancel** | `cancel` (073) |
| `DOWNLOADING` | determinate progress (percent) | **Cancel** | `cancel` (073) |
| `COMPLETED` | `Icons.Filled.DownloadDone` | **Delete** | `remove` (existing) |
| `FAILED` | `Icons.Filled.Refresh` | **Retry** | `retry` / enqueue (existing) |

Pass `status` + `progressPercent` into the button. Keep existing testTags. Drop the text labels (`Download` / `Cancel` / `Delete` / `Retry`) on those actions.

Non-goals: changing Downloads-screen row actions (those stay text Cancel / Delete / Retry / Play), the library **Downloads** destination chip, pause/resume, new download behaviour.

## Files

- `docs/tasks/074-download-action-icon.md` (this file)
- `core/ui/.../DownloadActionButton.kt` + strings + Compose tests
- `core/ui/build.gradle.kts` (`material-icons-extended`)
- `feature/library/.../LibrarySongRow.kt`, `LibraryScreen.kt`, `LibraryUiState.kt`, tests, unused download label strings
- `feature/player/.../PlayerScreen.kt`, tests, unused download label strings

## Tests

- `:core:ui`: idle node is `onNodeWithContentDescription("Download")` (not a visible “Download” `Text`); click fires; downloading → **Cancel**; completed → **Delete**; failed → **Retry**.
- Library Compose: idle row found by **Download** CD; downloading row by **Cancel**; completed by **Delete**; click still fires `onDownloadAction`.
- Player Compose: same CDs on the Now playing action.

## Done when

- Task plan committed before code.
- No per-song text “Download” button remains on library or Now playing.
- Icons reflect state; in-progress tap still cancels (073).
- Tests pass via contentDescription.
