# 103 — Compact Now Playing action icons

## Goal

Download, lyric-card, and settings in the Now Playing app bar look smaller (~20.dp glyphs, smaller button) without losing a ≥40–48.dp touch target or long-press tooltips. Download queued / progress / done / retry from 074 still work.

## Approach

Default `Icon` is 24.dp inside a 48.dp `IconButton`. Add optional `iconSize` / `containerSize` on `LiliIconButton` and `DownloadActionButton` (progress indicator uses the same `iconSize`). App-bar actions pass **20.dp** glyphs and **40.dp** containers; apply `minimumInteractiveComponentSize()` so the hit target stays 48.dp. Back stays the default size.

Non-goals: shrinking transport / mini-player / offset icons; changing download behaviour.

## Files

- `core/ui/.../LiliIconButton.kt`
- `core/ui/.../DownloadActionButton.kt`
- `feature/player/.../PlayerScreen.kt`
- tests next to those

## Tests

Compact `LiliIconButton` / `DownloadActionButton`: glyph ~20.dp; tagged control ≥40.dp. Tooltips still show. Download none/queued/downloading/completed/failed contentDescriptions unchanged. App-bar Settings / Lyric card / Download stay findable.

## Done when

- The three action glyphs are visibly smaller; taps and tooltips still work; 074 states remain.
- Tests pass; `./gradlew check`.
