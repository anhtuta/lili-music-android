# 105 — De-emphasize the app-bar artist line

## Goal

The artist under the Now Playing title is visually secondary in both light and dark: `onSurface` at ~0.6–0.7 alpha (or `onSurfaceVariant`), regular weight, slightly smaller than the title so the song name stands out.

## Approach

Keep `titleMedium` for the title. Artist stays `bodySmall` with `FontWeight.Normal` and `MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)` so hue follows the theme and contrast stays similar in light and dark. Extract the alpha/color helper so tests can assert it without screenshots.

Non-goals: changing the library list, mini player, or lyric pane artist styling.

## Files

- `feature/player/.../PlayerScreen.kt` (or a small `NowPlayingAppBarStyle` helper)
- tests in `feature/player`

## Tests

Helper: alpha in `0.6f..0.7f`; RGB matches `onSurface` in a light sample and a dark sample. Compose: artist is shown in `LiliTheme` light and dark.

## Done when

- Title is stronger than artist in both themes.
- Tests pass; `./gradlew check`.
