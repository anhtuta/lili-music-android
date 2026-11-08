# 099 — Karaoke fill across wrapped visual lines

## Goal

When an active lyric line wraps (screenshot: `再勇敢一点点 我就跟你` / `走`), karaoke fill progresses left-to-right on visual line 1, then visual line 2 — not only the last line. Smooth draw-phase interpolation from 075 stays.

## Approach

Web `BaiHatService.runKaraokeEffect` paints a CSS `linear-gradient(left)` on the **current word `<span>`**. Android concatenates words into one `Text`, so wrap is a `TextLayoutResult` with multiple lines. `getHorizontalPosition` on `getLineEnd()` can jump to the *next* line’s x, so the first visual line’s segment is empty/wrong and fill only animates on the last line (and can SrcAtop a sliver of the first line at the same x).

Paint per visual line using `getLineForOffset`, `getLineLeft` / `getLineRight` / `getLineTop` / `getLineBottom`, and `getHorizontalPosition` only for offsets that stay on that line. Progress fill across those segments in order (total width × fraction). Same path for TRC/XTRC word ranges and LRC (whole line as one range).

Non-goals: changing sung colour, parser, or 075 interpolation.

## Files

- `feature/lyrics/.../KaraokeFill.kt`
- `feature/lyrics/.../KaraokeFillTest.kt` (or a dedicated wrap test)

## Tests

Measure a string that wraps onto 2+ lines (`TextMeasurer` + narrow width). At low fill, only line 0 has a positive filled width; at high fill, line 0 is fully filled and line 1 is partial. A range that lives only on line 1 must not emit extents on line 0.

## Done when

- Wrapped active line fills in reading order across every visual line.
- 075 vsync interpolation still drives `fill`.
- Tests pass; `./gradlew check`.
