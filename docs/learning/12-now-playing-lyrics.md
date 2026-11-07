# Now-playing lyric wrap and full-height pane

[Learning guide index](README.md) · Tasks [066](../tasks/066-lyric-line-wrap.md), [067](../tasks/067-full-height-lyrics.md)

**What was built.** Two owner reports on Now playing:

1. **Long karaoke lines wrap.** A TRC line like “You're coming back down … the dirty” used to overflow the right edge; leftover words stacked one character per row. Cause was Compose `Row`, not the parser. Web `.line span` are inline in a block and wrap at spaces. Android now uses one wrapping `Text` with an `AnnotatedString` and a per-word `SpanStyle.brush` so karaoke fill still runs left-to-right, including across wrapped lines.
2. **Lyrics fill the viewport after scroll.** The pane was a fixed `280.dp` box. `PlayerBody` now sizes that slot to `BoxWithConstraints.maxHeight` (Scaffold body under the app bar). Scroll to the end and lyrics occupy the remaining screen. **Previous** is a weighted `TextButton` (`maxLines = 1`) so the label no longer wraps inside a 48.dp `IconButton`.

**Status:** This PR.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| Compose `Row` | Horizontal layout; leftover children get ~0 remaining width | `display: flex; flex-wrap: nowrap` with overflowing children squeezed |
| `AnnotatedString` | One `Text` with styled ranges | HTML: one block with `<span>` children |
| `SpanStyle.brush` | Fill (solid or gradient) on a span | CSS `background-clip: text` on `.line span` |
| `softWrap` | Break at word boundaries inside `Text` | Browser default wrap |
| `BoxWithConstraints` | Child sees `maxWidth` / `maxHeight` of the parent | Reading `offsetHeight` of a flex child |
| `Modifier.height(maxHeight)` | Lyric slot equals the content viewport | Web `#lyric_playground { height: calc(100vh - 100px) }` |
| `IconButton` | 48.dp minimum touch target | A 48px icon-only button; text inside it clips |
| `Modifier.weight(1f)` (in a `Row`) | Share leftover width equally | `flex: 1` |

Why not `FlowRow`? It would wrap at word widgets, but one `Text` with inline spans is closer to the DOM, keeps karaoke gradient per word, and avoids an experimental layout API.

Web still hides the spinning cover (`#div_left_wrapper { display: none }`) at width ≤720px, so lyrics are the full playground immediately. Android keeps a **single column** (cover + controls, then lyrics). After you scroll, the lyric slot is full body height — not two columns, and the cover is not hidden. Transport on web is icon arrows; Android keeps text labels (`Previous`, `Play`, …).

## Code to focus on

1. [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) — `KaraokeLine` builds the annotated string; `LyricLineRow` is a `Column`, not a `Row` of word `Text`s.
2. [LyricsPaneTest.kt](../../feature/lyrics/src/test/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPaneTest.kt) — 180.dp-wide pane; the whole TRC line stays inside that width.
3. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — `BoxWithConstraints` lyric height; `TransportTextButton`.
4. [PlayerScreenTest.kt](../../feature/player/src/test/java/com/anhtuta/lilimusic/feature/player/PlayerScreenTest.kt) — 720.dp-tall screen; lyric slot > 400.dp; Previous width > 48.dp.

Karaoke wrap (from `KaraokeLine`):

```kotlin
Text(
    text = annotated,
    textAlign = style.textAlign,
    softWrap = true,
    overflow = TextOverflow.Clip,
    modifier = Modifier.fillMaxWidth(),
)
```

Lyric slot height (from `PlayerBody`):

```kotlin
BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val lyricsHeight = maxHeight
    Column(Modifier.verticalScroll(rememberScrollState())) {
        // cover, seek, transport, modes
        Box(Modifier.fillMaxWidth().height(lyricsHeight)) { lyrics() }
    }
}
```
