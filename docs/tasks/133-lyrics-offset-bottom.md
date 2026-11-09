# 133 — Move lyric offset controls to bottom of pane

## Goal

Offset earlier/later buttons sit at the bottom of the lyric panel for one-handed reach.

## Approach

Align the offset overlay to `BottomCenter` inside the lyric body with safe padding above the player chrome.

## Files

- `feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt`
- `feature/lyrics/src/test/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPaneTest.kt`

## Tests

Compose layout assertion: offset row bottom is below lyric line content (closer to pane bottom).

## Done when

- Buttons render at bottom of pane.
- Tests pass; `./gradlew check`.
