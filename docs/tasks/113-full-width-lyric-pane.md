# 113 — Full-width lyric pane (no outer rounded box)

## Goal

Remove the outer rounded clip around the lyric slot so lyrics use the full screen width; lyric **text** keeps horizontal padding aligned with the back affordance and bottom transport row (~16.dp).

## Approach

Drop `LyricSlot` `RoundedCornerShape` clip; adjust `CompactPlayerBody` / `ExpandedPlayerBody` padding so only the lyric **background** is edge-to-edge while line padding in `LyricsPane` matches controls. Update or remove `LyricPaneShape` tests if the constant is unused in the player shell.

## Files

- `feature/player/PlayerScreen.kt`, `LyricPaneShape.kt`
- `feature/lyrics/LyricsPane.kt` if pane insets change
- Player module tests

## Tests

Compose or unit: lyrics slot has no clip tag/shape; horizontal inset of active line matches controls.

## Done when

Visual goal met on compact width, tests pass, `./gradlew check`.
