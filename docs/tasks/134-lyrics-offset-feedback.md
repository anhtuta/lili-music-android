# 134 — Show lyric offset value after nudge

## Goal

After tapping offset earlier/later, users see the current offset in milliseconds.

## Approach

Display `lyrics_offset_value` in the offset row (test tag `OFFSET_VALUE`), driven by `LyricsUiState.userOffsetMs` / frame combined offset.

## Files

- `feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt`
- `feature/lyrics/src/test/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPaneTest.kt`

## Tests

Compose: after nudge, offset label shows updated ms value.

## Done when

- Offset label visible when controls are shown and updates after nudge.
- Tests pass; `./gradlew check`.
