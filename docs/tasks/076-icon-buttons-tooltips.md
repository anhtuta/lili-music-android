# 076 — Icon buttons with long-press tooltips

## Goal

No text **action** buttons remain. Every tappable action is a Material Icon whose `contentDescription` is also a long-press Material 3 tooltip (for example “Previous song”, “Seek back 5 seconds”, “Increase speed by 0.1x”). Labels, values (`1.0×`, `Offset: 0ms`), FilterChips, and switches stay text.

## Approach

Web `BaiHat.js` / `Player.js` mix CSS triangles and labeled controls; Android already iconified transport (071) and download (074) without tooltips.

- Add `LiliIconButton(icon, label, onClick)` in `:core:ui`: `TooltipBox` + `PlainTooltip` + `rememberTooltipState`; `Icon` `contentDescription` equals `label`.
- A content overload covers `DownloadActionButton` (spinner / `DownloadDone`).
- Replace remaining `TextButton` / `Button` actions: library app bar (refresh, downloads, settings), sort order, retry, search clear; player back / card / settings / speed ± / mini play-pause / transport; lyric offset ± and card open; lyric-card close / select-all / generate / back / share / copy; downloads back / delete-all / play / retry / cancel / delete and dialog confirm/dismiss; settings back / speed ±.
- Keep sort-field chips, shuffle / repeat chips, settings chips, switches, search field, and numeric labels.
- Leading `Search` on the library field is decorative, not a button. App-bar **Refresh** is the catalog reload (pull-to-refresh stays).

Non-goals: changing FilterChip selection UX, waveform, backend.

## Files

- `docs/tasks/076-icon-buttons-tooltips.md` (this file)
- `core/ui/.../LiliIconButton.kt` + tests
- `core/ui/.../DownloadActionButton.kt`
- `feature/library|player|lyrics|downloads|settings` screens and `strings.xml`
- existing screen tests updated to `contentDescription` where they clicked visible text (`Pause` on mini-player)

## Tests

- `LiliIconButton`: found by `contentDescription`; click fires; `onAllNodesWithText(label)` is 0 until long-press, then the tooltip text is shown.
- `DownloadActionButton` still found by Download / Cancel / Delete / Retry; long-press shows the same tooltip.
- Player transport still uses contentDescriptions (update “Previous” → “Previous song”). Mini-player play/pause by contentDescription.

## Done when

- Task plan committed before code.
- No `TextButton` / filled `Button` actions remain in feature UI.
- Tooltips use the same string as `contentDescription`.
- Tests pass; `./gradlew check` passes.
