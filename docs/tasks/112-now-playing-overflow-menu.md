# 112 — Now Playing app-bar overflow menu

## Goal

On Now Playing, Download and Lyric card live in a dropdown opened from a **smaller** gear icon; Settings remains a menu item that opens the settings screen.

## Approach

Replace the three separate app-bar action buttons with one `NowPlayingOverflowMenu` anchored to the gear. Menu items: Download (reuse download labels from 074), Lyric card, Settings. Gear uses smaller metrics than other chrome (`~16.dp` icon, compact touch target). Top bar keeps back + cover/title/artist only besides the menu.

## Files

- `feature/player/.../PlayerScreen.kt`, new `NowPlayingOverflowMenu.kt`
- `feature/player/res/values/strings.xml` if needed
- `PlayerTestTags`, `PlayerScreenTest`, `PlayerScreenPolishTest`

## Tests

Compose: gear opens menu; download / lyric card / settings callbacks; download states still reachable from menu.

## Done when

Menu UX matches goal, tests pass, `./gradlew check`.
