# 132 — Reveal lyric offset controls on pane touch or scroll

## Goal

Users can open the lyric offset +/- controls by touching the lyric list or scrolling it, without having to tap a line (which seeks playback).

## Approach

Reveal on lazy-list drag/scroll and on pane tap. Decouple `revealOffset` from `onSeekToLine`; line taps still seek but call `onRevealOffset` only when appropriate. Keep auto-hide after idle.

## Files

- `feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt`
- `feature/lyrics/src/test/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPaneTest.kt`

## Tests

Compose: scrolling the lyric list shows offset buttons without `onSeekToLine` firing; line tap still seeks.

## Done when

- Offset row appears after scroll or non-seek pane interaction.
- Tests pass; `./gradlew check`.
