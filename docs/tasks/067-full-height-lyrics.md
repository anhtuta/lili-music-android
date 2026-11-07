# 067 — Full-height lyrics on now-playing scroll

## Goal

After the user scrolls to the bottom of Now playing, the lyric area fills the **viewport below the top app bar** (not a short ~280.dp box). The Previous transport label is fully visible (`Previous`, not `Previ` / `ous`).

## Approach

**Lyrics height.** `PlayerBody` is a `verticalScroll` column: cover, headings, seek, transport, modes, then `Box(height(280.dp)) { lyrics() }`. That 280.dp slot is the short box in the owner screenshot.

Web (`BaiHat.js` + `BaiHat.css`): `#lyric_playground` / `.lyric-wrapper` / `#div_result` are **height 100%**. On viewports ≤720px the spinning cover column (`#div_left_wrapper`) is `display: none`, so lyrics are the full playground. Android is a **single column** (cover + controls, then lyrics) — we keep that, but size the lyric slot to the content viewport:

```
BoxWithConstraints(fillMaxSize) {
  Column(verticalScroll) {
    chrome (cover, seek, transport, modes)
    Box(fillMaxWidth + height(maxHeight)) { lyrics() }
  }
}
```

`maxHeight` is the Scaffold body (below the app bar). Scrolling to the end parks a full-viewport lyric pane. Nested `LazyColumn` in `LyricsPane` already `fillMaxSize()`s that box.

**Previous label.** Transport uses `IconButton` (48.dp) for Previous / Next / ±5s, so `Previous` wraps. Play is already a `TextButton`. Use `TextButton` + `weight(1f)` + `maxLines = 1` for all five transport actions so labels share the row.

Non-goals: lyric wrap (066), web two-column playground, hiding the cover on small screens.

## Files

- `docs/tasks/067-full-height-lyrics.md` (this file)
- `feature/player/.../PlayerScreen.kt`
- `feature/player/.../PlayerTestTags.kt` (`LYRICS`)
- `feature/player/.../PlayerScreenTest.kt`

## Tests

- Lyrics slot height on an 720.dp-tall `PlayerScreen` is **far above 280.dp** (matches the viewport minus chrome of the slot itself — equal to `BoxWithConstraints.maxHeight`).
- `Previous` semantics/layout width is **greater than 48.dp**; `onNodeWithText("Previous")` is displayed.
- Existing player callback tests still pass (scroll-to as needed).

## Done when

- Task plan committed before code.
- Scrolled-to-end lyrics fill the body viewport; Previous does not clip.
- Tests above exist.
