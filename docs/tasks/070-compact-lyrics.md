# 070 — Compact now-playing lyrics (hide cover on phones)

## Goal

On compact width (phones, **&lt; 720dp**, matching web `max-width: 720px`), Now playing shows **lyrics filling the body immediately**. The spinning/cover column is hidden. Transport, seek, and speed stay reachable in a compact bar pinned at the bottom. Wider screens keep cover + lyrics side by side. The 067 “scroll past the cover to a full-height lyric slot” column is removed.

## Approach

Web (`BaiHat.js` class + `BaiHat.css`): `#lyric_playground` is `height: calc(100vh - 100px)`. At `max-width: 720px`, `#div_left_wrapper` (cover) is `display: none`, so the lyric playground is the whole screen and the player bar (`.ll_player` in `Player.js`) stays under it.

Android:

- Measure width with `BoxWithConstraints` (same 720.dp breakpoint as the CSS; `WindowSizeClass` Compact is 600.dp, which would hide the cover too early vs web).
- **Compact (`maxWidth < 720.dp`):** do not compose the cover. `Column(fillMaxSize)` with `Box(weight(1f))` for lyrics and a bottom `PlayerControlsBar` (seek, transport, shuffle/repeat, speed). No `verticalScroll` of cover-then-lyrics.
- **Expanded (`maxWidth >= 720.dp`):** `Row` of cover + headings | lyrics, same pinned controls bar. No 067 scroll-to-full-height column.
- Title/artist stay visible (web `.ll_player .info`); on compact they sit in the controls bar.
- Non-goals: waveform, spinning animation, 071 icon buttons (text labels stay until 071), 072 fonts, 073 cancel.

## Files

- `docs/tasks/070-compact-lyrics.md` (this file)
- `feature/player/.../PlayerScreen.kt` (and a small `PlayerLayout` helper if it keeps methods under detekt)
- `feature/player/.../PlayerTestTags.kt`
- `feature/player/.../PlayerScreenTest.kt`

## Tests

- Compact `400.dp × 720.dp`: cover / placeholder **does not exist**; lyrics tag is displayed **without** `performScrollTo`; lyric slot height is far above 280.dp; seek, play/pause, and speed are displayed.
- Expanded `800.dp × 720.dp`: cover placeholder **is displayed** next to lyrics; lyrics still fill the remaining row height (no scroll-to-end required).
- Existing callback tests still pass (wrap in an explicit size so compact vs expanded is deterministic).

## Done when

- Task plan committed **before** code.
- Compact phones match web: lyrics first, cover hidden, controls pinned.
- 067 scroll-past-cover behaviour is gone.
- Tests above exist and `./gradlew :feature:player:testDebugUnitTest` (and later full `check`) pass.
