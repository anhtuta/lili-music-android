# 071 — Icon transport buttons

## Goal

Now-playing Previous / −5s / Play / +5s / Next are **icon buttons**, like web `Player.js` (`ll-btn-prev`, `ll-play-pause`, `ll-btn-next`). Each icon has a contentDescription for TalkBack and tests: Previous, Seek back 5 seconds, Play/Pause, Seek forward 5 seconds, Next.

## Approach

Web (`Player.js` + `Player.css`): circular CSS-arrow buttons; `title="Previous"` / `"Play"` / `"Pause"` / `"Next"`. Seek ±5s is a MediaSession / Android extra already present as text `−5s` / `+5s`.

Android: Material Icons in `IconButton`s (not CSS triangles — keep that difference in PLAN §10):

- Previous → `Icons.AutoMirrored.Filled.SkipPrevious`, contentDescription **Previous**
- Seek back → `Icons.Filled.Replay5`, **Seek back 5 seconds**
- Play/Pause → `PlayArrow` / `Pause`, **Play** or **Pause** (web `title`)
- Seek forward → `Icons.Filled.Forward5`, **Seek forward 5 seconds**
- Next → `Icons.AutoMirrored.Filled.SkipNext`, **Next**

Add `androidx.compose.material:material-icons-extended` (BOM) for Replay5 / Forward5. Strings live in `feature/player` `strings.xml`. Mini-player can keep its text Play/Pause. Non-goals: matching the CSS circle chrome, shuffle/repeat icon redesign, 070 layout.

## Files

- `docs/tasks/071-transport-icons.md` (this file)
- `gradle/libs.versions.toml`, `feature/player/build.gradle.kts`
- `feature/player/.../PlayerScreen.kt` (`PlayerTransport`)
- `feature/player/.../res/values/strings.xml`
- `feature/player/.../PlayerScreenTest.kt`

## Tests

- Compact Now playing: `onNodeWithContentDescription("Previous")` (and seek back/forward, Next) is displayed and clicks fire the same callbacks.
- Play contentDescription is **Play** when paused and **Pause** when playing.
- `onNodeWithText("Previous")` no longer matches a visible label.

## Done when

- Task plan committed before code.
- Transport is icon-only with the contentDescriptions above.
- Tests pass.
