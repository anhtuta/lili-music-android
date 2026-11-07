# 066 — Wrap long karaoke lyric lines

## Goal

A long synced lyric line stays readable inside the lyric area. Words wrap onto following lines instead of overflowing the right edge and stacking one character per row. Karaoke fill still applies per word across wrapped lines.

## Approach

Owner screenshot: highlighted prefix of a TRC line fits; the rest (`the… dirty`) pours down the right margin one glyph per row.

**Cause:** `LyricLineRow` lays words in a Compose `Row`. `Row` does not wrap. After the filled words consume the width, leftover `Text` children get ~0 remaining width, so each word’s `Text` wraps internally as a single-character column. That is Compose layout, not the parser.

**Web:** `BaiHat.css` `.line span` are **inline** in a block (`.line { padding: 10px }`). The browser wraps the span sequence at spaces. Karaoke fill is per span (`background-clip: text`). Android should wrap at word boundaries the same way.

**Fix:** replace the `Row` with `FlowRow` (`Modifier.fillMaxWidth()`, arrangement from `lyricAlign`). Each `KaraokeWord` stays its own `Text` so per-word `Brush` fill is unchanged across wraps. A single word wider than the pane still wraps inside that `Text` (`softWrap` default).

Non-goals: full-height lyric pane (067), Previous label, parser changes, web/backend.

## Files

- `docs/tasks/066-lyric-line-wrap.md` (this file)
- `feature/lyrics/.../LyricsPane.kt` (`LyricLineRow`)
- `feature/lyrics/.../LyricsPaneTest.kt`
- `feature/lyrics/.../LyricsTestTags.kt` (optional word/line-words tag)

## Tests

Compose (Robolectric), narrow width (~180.dp), a long **TRC** active line (many `<ms>word ` tokens, karaoke on):

- Last word’s layout **width** is more than one glyph (not a 0-width column).
- Last word’s **top** is below the first word’s top (it wrapped).
- First and last words remain findable as whole strings (not split into letter nodes).

Existing karaoke / tap / offset tests still pass.

## Done when

- Task plan committed before code.
- Long TRC lines wrap inside the pane; no character-column overflow.
- Tests above exist.
