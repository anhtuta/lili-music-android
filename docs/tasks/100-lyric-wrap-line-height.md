# 100 — Line height for wrapped lyric lines

## Goal

Wrapped visual lines of one lyric sentence have breathing room (~1.35–1.4× font size). Different sentences keep their existing gap (or a proportional tweak). Karaoke fill from 099 still covers the glyphs.

## Approach

Set paragraph `lineHeight` on the lyric `Text` to `fontSize * 1.4f` with `LineHeightStyle(alignment = Center, trim = None)` so extra leading is around the glyphs, not trimmed. Fill uses `getLineTop` / `getLineBottom`, which include that leading, so 099 stays aligned.

Keep `LyricLineRow` vertical padding as the gap *between* sentences.

Non-goals: changing font size, gigantic-line multiplier, or translation style beyond inheriting paragraph spacing.

## Files

- `feature/lyrics/.../KaraokeFill.kt` (or a small `LyricLineMetrics` helper)
- tests next to karaoke / lyrics pane

## Tests

Unit: multiplier is in `1.35f`–`1.4f`. Compose or layout: a wrapping line’s height is greater than two tight glyph rows (at least `2 * fontSize * 1.35` in px).

## Done when

- Wrapped lines are visibly separated; sentence padding still exists.
- Fill from 099 still matches glyphs.
- Tests pass; `./gradlew check`.
