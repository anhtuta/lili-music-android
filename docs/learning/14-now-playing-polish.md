# Phase 8 — Now Playing polish (owner screenshot review)

[Learning guide index](README.md) · Tasks [080–098](../tasks/080-098-now-playing-polish.md) · Follow-up [099](../tasks/099-wrapped-karaoke-fill.md)–[105](../tasks/105-deemphasize-artist.md)

**What was built.** Tú reviewed compact-phone screenshots of Now Playing (dark + light) and asked for a tighter player. Web player remains class `BaiHat.js` / `Player.js`. Android extras that stay: line-tap-to-seek, ±5s, speed on the bar, swipe-down to close.

1. **Play mode (080).** One icon after Next cycles Sequence (`Repeat`) → Shuffle (`Shuffle`) → Repeat one (`RepeatOne`). Tooltip/contentDescription is `Play mode: {current}`. Shuffle still puts the current song first. Web `Player.js` still has two independent toggles; Android uses unused web `playTypeList` order.
2. **`=====` (081).** Keep as lyric **text**. It is an LRC line, not a chrome separator.
3. **Edge fade (082).** Top and bottom of the lyric list use a gradient mask (`graphicsLayer` + `drawWithContent` + `BlendMode.DstIn`) so half-cut lines fade out.
4. **Handle (083).** Short bottom-sheet-style handle under the lyric pane. Drag down (or tap) expands lyrics fullscreen; drag up, tap, or Back restores. Compact lyric height is unchanged until expanded.
5. **One control row (084).** Speed chip, Previous, −5s, Play, +5s, Next, play mode. The chip opens a 0.5–2.0 menu in 0.1 steps. The old speed row is gone.
6. **Big Play (085).** ~64.dp filled circular Play/Pause (`primaryContainer`).
7. **Offset overlay (086).** Hidden until a lyric-line tap (which still seeks) or empty-pane tap. Auto-hide after 3s. Only small low-opacity minus/plus icons. Minus = −100ms = earlier (web `Setting.js` caret-up). Session/per-song only: never API, never DataStore/Room; resets on song change.
8. **No in-pane lyric card (087).** App-bar Image button stays.
9. **Hide album (088).** Now Playing never shows album, even when the song has one.
10. **Slim seek bar (089).** ~4.dp track, small round thumb, played vs remaining contrast in both themes.
11. **Cover gradient (090).** Subtle Palette colour wash on Now Playing; theme-based fallback when there is no cover — never flat grey.
12. **Contrast (091).** Transport icons `onSurface`. Shuffle / Repeat one use the accent (`primary`).
13. **Karaoke colours (092).** Light pane: surface `#DFDFDF`, fill `#2199F9`, unsung `#333`. Dark: fill `#FC0`. `picture-bg` without a cover follows the **in-app** light/dark theme (fixes the screenshot’s grey pane + yellow fill). Sung (already-highlighted) words use the **full** karaoke colour, like the web — an early 55% dim was removed at Tú’s request.
14. **App-bar labels (093).** Back, download, lyric card, settings: `contentDescription` + long-press tooltip via `LiliIconButton`.
15. **Swipe down (094).** Nested-scroll leftover downward overscroll closes Now Playing to the previous screen / mini player. Disabled while lyrics are expanded so it does not fight the handle or lyric scrolling.
16. **Dark-mode audit (095).** Default icon tint is `onSurface`. Library / downloads / settings / lyric card / player Compose tests render in `LiliTheme(darkTheme = true)` and still find Back / Settings / transport by contentDescription.
17. **Status bar (096).** `enableEdgeToEdge` plus `WindowCompat` `isAppearanceLightStatusBars` from **in-app** `AppTheme`, not only system night. Light theme uses dark status-bar icons; the Now Playing app bar uses `surfaceContainerHigh`.
18. **App bar song (097).** No “Now playing” title. Small rounded cover thumbnail, then title and artist (one line each). Duplicate title/artist under the lyric pane is gone. Overflow now marquees (104) instead of ellipsis.
19. **Rounded pane (098).** 16.dp corner radius; content is clipped to it.
20. **Wrapped karaoke fill (099).** Fill walks every *visual* line of the active `Text` (line 1 left→right, then line 2). Web paints a CSS gradient on one word `<span>`; Android concatenates the line into one `Text`, so wrap is `TextLayoutResult` lines, not extra spans.
21. **Wrap line height (100).** Lyric `Text` uses `lineHeight = fontSize * 1.4` with `LineHeightStyle(Center, Trim.None)` so wrapped rows breathe. Padding between *sentences* is unchanged. Fill still uses `getLineTop` / `getLineBottom`, so it covers the glyphs plus that leading.
22. **Centered active line (101).** Auto-scroll puts the active row’s vertical center on the pane’s center (web `scrollLyric` uses `offsetHeight/2 + clientHeight/2`). Uses `item.size`, so pinyin/translation rows count. Dragging the list pauses auto-scroll; the next line change recenters.
23. **Control row symmetry (102).** Speed is a 48.dp `LiliIconButton` (`1.0×` + tooltip) matching play-mode’s 48.dp target (play-mode glyph is slightly larger). Left and right of Play are `weight(1f)` groups so Play is the screen’s horizontal center.
24. **Compact app-bar actions (103).** Download / lyric card / settings glyphs are 20.dp in 40.dp buttons; `minimumInteractiveComponentSize()` keeps a 48.dp tap target. Tooltips and 074 download states stay. Back stays the default size.
25. **Marquee title/artist (104).** Overflowing lines `horizontalScroll` slowly (~35 dp/s), pause ~1.75s at the end, jump back, repeat. Fitting text does not move. Drag to scroll; auto-scroll resumes after a short pause. Web `#song_title` / `#song_artist` in `Player.js` do not marquee.
26. **Muted artist (105).** App-bar artist is `onSurface` at 0.65 alpha, regular `bodySmall`, so the title stands out in light and dark.

**Status:** PR #24 merged 080–098. Follow-up 099–105 is this PR. Phase 7 075–076 merged in [PR #23](https://github.com/anhtuta/lili-music-android/pull/23).

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `PlayMode.cycle()` | Mutually exclusive next value in a fixed list | A React `useState` that walks `playTypeList` instead of two independent booleans |
| `contentDescription` | TalkBack / test name of an icon | `aria-label` (and we reuse it as the tooltip, like HTML `title`) |
| `PlainTooltip` / `TooltipBox` | Long-press popup naming a control | CSS `:hover` tooltip; on phone it is long-press, not hover |
| `graphicsLayer { compositingStrategy = Offscreen }` | Draw this node to its own bitmap so blend modes see only its pixels | An offscreen `<canvas>`; without it `DstIn` can punch through the page background |
| `BlendMode.DstIn` | Keep destination where the source (the fade gradient) is opaque | CSS `mask-image: linear-gradient(...)` on the lyric scroller |
| `drawWithContent` | Draw children, then extra draw commands | Setting a CSS mask after React paints, without a re-render |
| Drag handle | A small grabber that resizes a sheet | A bottom-sheet drag pill (`div` with `onPointerMove`) |
| `detectVerticalDragGestures` | Pointer listener that reports vertical drag delta | `pointerdown` / `pointermove` on a handle; `change.consume()` is `event.stopPropagation()` |
| `BackHandler` | Compose callback for the system Back key/gesture | `popstate` / intercepting the browser Back button |
| `animateContentSize` | Animate layout size when children appear/disappear | CSS `transition: height` after a class toggle |
| `FilterChip` | Compact tappable label that can open a menu | A `<select>` styled as a chip, or a speed `<button>` that opens a popover |
| `DropdownMenu` | Anchored list of actions | A `<select>` / Radix dropdown; items are `0.5×` … `2.0×` |
| `FilledIconButton` | Circular button with a filled container | A FAB-like round CSS button with `background: primary-container` |
| `primaryContainer` | Material 3 fill colour for prominent filled controls | A CSS custom property for the “big play” circle, not the same as `primary` text colour |
| Session-only state | Held in a `MutableStateFlow` on the ViewModel | A React class field (`this.offsetTime`); gone when the song (or process) changes; **not** `localStorage` |
| `LaunchedEffect` + `delay` | Coroutine that restarts when keys change; here auto-hides offset after 3s | `setTimeout` that is cleared and reset on each tap |
| `Palette` (AndroidX) | Samples a bitmap for a dominant colour | `getAverageRGB` in web `BaiHat.js` (web used it on waveform bars, not the page background) |
| `Brush.verticalGradient` | Compose fill that blends two colours top→bottom | CSS `linear-gradient(to bottom, cover, theme-bg)` |
| `onSurface` | Highest-contrast content colour on the theme surface | `color: #eee` in `.body-dark` / `color: #333` in light; not a muted `onSurfaceVariant` |
| Effective lyric theme | Light vs dark colours chosen from lyric theme **and** app theme **and** whether a cover exists | Web `getKaraokeWordStyle` checks `lyric-pg-light` on the playground, not `prefers-color-scheme` alone |
| `enableEdgeToEdge` | Draw under the status/nav bars | `100vh` under the browser chrome; you then colour the inset yourself |
| `SystemBarStyle.auto` | Default status-bar icon contrast from **system** night mode | `prefers-color-scheme` for the browser UI |
| `WindowCompat.isAppearanceLightStatusBars` | Dark icons on a light bar (`true`) or light icons on a dark bar | We override `auto` from **in-app** `AppTheme`, like a CSS class `.theme-light` on `<html>` rather than the OS setting |
| `surfaceContainerHigh` | A step darker/lighter than `surface` | A slightly distinct app-bar background so light-mode status icons sit on a visible strip |
| `NestedScrollConnection` | Parent sees leftover scroll after the child (lyric list) consumes it | A capturing `wheel`/`touchmove` listener on the page after the inner `overflow: auto` div is at the top |
| `onPostScroll` `available.y` | Pixels the list did **not** consume (overscroll) | `scrollTop === 0` and the finger still moves down |
| `TextOverflow.Ellipsis` | Truncate with `…` | CSS `text-overflow: ellipsis; white-space: nowrap` |
| `clip(RoundedCornerShape)` | Round corners and hide children that stick out | `border-radius: 16px; overflow: hidden` on `.lyric-wrapper` |
| `TextLayoutResult` | After `Text` lays out, the positions of every glyph and visual line | `Range.getClientRects()` / a canvas `measureText` plus wrap; web karaoke does not need this because each word is its own `<span>` |
| `getLineForOffset` / `getLineLeft` / `getLineRight` | Which visual line an index sits on, and that line’s x edges | `getClientRects()[n]` for a wrapped DOM node. Do **not** use `getHorizontalPosition(getLineEnd())` — that x can jump to the *next* line |
| `lineHeight` | Paragraph leading as a `TextUnit` (`1.4 * fontSize`) | CSS `line-height: 1.4` |
| `LineHeightStyle` (`alignment = Center`, `trim = None`) | Extra leading is split above and below the glyphs and is **not** clipped off the first/last line | CSS `line-height` with no `leading-trim`; Android otherwise trims like `line-height: 1` on the first/last wrap |
| `LazyListState.animateScrollBy` | Animate a pixel delta after you know `item.offset` / `item.size` | `element.scrollBy({ top, behavior: 'smooth' })`. `animateScrollToItem(index)` is `scrollIntoView` aligned to the **top** |
| `collectIsDraggedAsState` | True while a pointer drag is in progress on the list | `pointerdown`…`pointerup` on the scroller; skip `scrollLyric` while the user is dragging |
| `Modifier.weight(1f)` on sibling `Row`s | Two groups share leftover width **equally** around Play | CSS `flex: 1` on `.left` and `.right` with the play button in between; not `justify-content: space-between` with unequal ends |
| `minimumInteractiveComponentSize()` | Expand a smaller visual button so the tap target is still ~48.dp | CSS `min-width/min-height: 48px` (or padding) around a 20px icon; Apple/Google “minimum touch target” |
| `ScrollState` | Pixel offset + max of a scrollable | `element.scrollLeft` / `scrollWidth - clientWidth` |
| `horizontalScroll(ScrollState)` | Make a child wider than the viewport and let the user drag it | CSS `overflow-x: auto` on a nowrap line |
| `ScrollState.animateScrollTo` | Animate `scrollLeft` to a value with a duration/easing | `element.scrollTo({ left, behavior: 'smooth' })` but we pass a **linear** `tween` so speed is constant (~35 dp/s) |
| `LaunchedEffect` loop | Coroutine that restarts when keys change (`text`, overflow, dragging) | `setInterval` / a `while (true) { await delay; scroll }` that is cleared when the song changes |

Why line tap both seeks **and** reveals offset: line `clickable` already owns the tap. Revealing on that same tap keeps scrolling intact (an outer `clickable` on the pane fights `LazyColumn`). Empty space uses `detectTapGestures` on the list, which fires on tap but not on a drag-scroll.

Why swipe-down uses leftover nested scroll, not a screen-wide drag: a screen drag would steal lyric scrolling and the handle. `onPostScroll` only sees motion the list refused (you are at the top and still pulling down).

## Code to focus on

1. [PlayMode.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/PlayMode.kt) — `cycle()`.
2. [PlayerControls.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerControls.kt) — one row: equal-weight left/right groups around 64.dp Play; compact speed + play mode.
3. [LiliFilledIconButton.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/button/LiliFilledIconButton.kt) — 64.dp Play/Pause.
4. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — app bar cover+title+artist, palette gradient, handle, rounded `LyricSlot`, nested-scroll dismiss.
5. [PlayerLyricHandle.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerLyricHandle.kt) — drag + tap + tooltip.
6. [NowPlayingDismiss.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/NowPlayingDismiss.kt) — leftover downward overscroll.
7. [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) — offset overlay, line tap seeks and reveals; auto-scroll centers the active row.
8. [LyricEdgeFade.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricEdgeFade.kt) — `DstIn` mask.
9. [LyricThemeColors.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/theme/LyricThemeColors.kt) — `isEffectiveLight`, `sungText`.
10. [CoverPalette.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/palette/CoverPalette.kt) — `nowPlayingGradient`.
11. [SystemBarAppearance.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/theme/SystemBarAppearance.kt) + [LiliApp.kt](../../app/src/main/java/com/anhtuta/lilimusic/LiliApp.kt) `ApplySystemBarAppearance`.
12. [PlayerScreenPolishTest.kt](../../feature/player/src/test/java/com/anhtuta/lilimusic/feature/player/PlayerScreenPolishTest.kt) — play mode cycle, handle, single row, tooltips, dark theme, centered Play.
13. [KaraokeFill.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/KaraokeFill.kt) `karaokeFillExtents` — per-visual-line fill.
14. [LyricLineMetrics.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricLineMetrics.kt) — 1.4× `lineHeight`.
15. [LyricPaneScroll.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricPaneScroll.kt) — `centerDelta` / `shouldAutoScroll`.
16. [MarqueeText.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/text/MarqueeText.kt) — slow `horizontalScroll` loop; skip when `maxValue == 0` or the user is dragging.

Play mode cycle (from `PlayMode`):

```kotlin
fun cycle(): PlayMode =
    when (this) {
        SEQUENCE -> SHUFFLE
        SHUFFLE -> REPEAT_ONE
        REPEAT_ONE -> SEQUENCE
    }
```

Edge fade (from `LyricEdgeFade`):

```kotlin
fun Modifier.lyricEdgeFade(fadeHeight: Dp = 24.dp): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(/* transparent → black → transparent */),
                blendMode = BlendMode.DstIn,
            )
        }
```

Status bar follows in-app theme (from `LiliApp`):

```kotlin
val darkTheme = settings.settings.appTheme == AppTheme.DARK
ApplySystemBarAppearance(darkTheme = darkTheme)
WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
```

Wrapped karaoke fill (from `KaraokeFill`):

```kotlin
for (line in startLine..endLine) {
    val segment = visualLineSegment(layout, line, rangeStart, rangeEndExclusive) ?: continue
    segments += segment
}
var remaining = totalWidth * fill
```

Centered active line (from `LyricPaneScroll`):

```kotlin
fun centerDelta(viewportStart: Int, viewportEnd: Int, itemOffset: Int, itemSize: Int): Int =
    (itemOffset + itemSize / 2) - (viewportStart + viewportEnd) / 2
```

## How to test on the arm64 emulator

From the repo root, with the emulator already running (`make emulator` if you use that target):

```bash
make install
```

That installs the debug APK. Then:

1. Open a song so Now Playing is fullscreen (not the mini player).
2. **Play mode:** tap the icon after Next. Sequence → Shuffle → Repeat one → Sequence. Long-press for `Play mode: …`. Confirm shuffle still starts from the current song.
3. **Handle:** drag the pill down; lyrics go fullscreen. Drag up, tap, or Back to restore.
4. **Offset:** tap a lyric line — it seeks **and** minus/plus appear; they vanish after a few seconds. They must not survive a song change or an app restart.
5. **Swipe down** on the pane when lyrics are at the top: Now Playing closes to the mini player. Swipe should still scroll lyrics when not at the top.
6. Toggle **Settings → appearance** light/dark (not only the system theme): status-bar icons flip, the light lyric pane is `#DFDFDF` with blue karaoke (not grey + yellow), transport icons stay visible on dark.
7. **Wrapped karaoke:** a long Chinese TRC line should fill the first wrapped row, then the next (`走` last). Wrapped rows should have a little gap. The active sentence sits in the **middle** of the pane, not the top.
8. **Play button:** it should sit on the screen’s horizontal center; speed (`1.0×`) and play-mode icons look like matching 48.dp ends.
9. **App-bar actions:** download / lyric card / settings glyphs are smaller than Back; long-press still shows a tooltip; download progress still cancels.
10. **App-bar text:** a long title/artist scrolls slowly, then jumps back; a short one stays still. You can drag it. The artist is paler than the title in both themes.
