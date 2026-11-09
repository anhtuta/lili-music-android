# 118 — Now Playing overflow trigger (⋮)

## Goal

The app-bar overflow trigger shows a **three-dot** icon; **Settings** appears only as a menu item (no duplicate gear in the bar).

## Approach

In `NowPlayingOverflowMenu`, use `Icons.Filled.MoreVert` on `LiliIconButton`. Keep `Icons.Filled.Settings` on the Settings `DropdownMenuItem`. Labels/tooltips unchanged (`player_more_options`).

## Files

- `feature/player/.../NowPlayingOverflowMenu.kt`
- Player overflow/layout tests if icon assumptions change

## Tests

Compose: overflow still opens; Settings menu item still present; trigger contentDescription remains “More options”.

## Done when

Screenshot shows single ⋮ in app bar, tests pass, `./gradlew check`.
