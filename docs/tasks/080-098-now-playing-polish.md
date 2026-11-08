# 080–098 — Now Playing polish (Phase 8)

## Goal

Owner review of the compact Now Playing screen (dark + light phone screenshots) asked for a tighter, more readable player: one cycling play-mode icon, faded lyric edges, a drag handle to expand lyrics, a single transport row, a large Play button, auto-hiding offset icons, no in-pane lyric-card button, no album line, a slim seek bar, a cover-colour gradient (never flat grey), stronger icon contrast, karaoke colours that follow the effective theme, labelled app-bar icons, swipe-down to close, a dark-mode icon audit, a readable light status bar, song info in the app bar, and a rounded lyric pane.

Each numbered item is its own Conventional Commit (`080`–`098`). This plan is committed **before** code.

## Web source (authoritative player)

Attached `liliana-web-src` / `src/pages/BaiHat/BaiHat.js` (class) plus `Player.js`, `Setting.js`, `BaiHat.css`, `BaiHatService.js`, `constants/Constants.js`.

### New differences from the web (report first)

These are **this phase**, not the already-documented extras in [PLAN.md §10](../PLAN.md).

| # | Web (`BaiHat.js` / `Player.js`) | Android after Phase 8 |
| --- | --- | --- |
| 080 | Two toggles: `.ll-shuffle` and `.ll-repeat`, each flipping back to `SEQUENCE`. `playTypeList` in `Constants.js` (`SEQUENCE` → `SHUFFLE` → `REPEAT_ONE`, icons `fa-long-arrow-right` / `fa-random` / `fa-repeat`) is unused in `Player.js`. Shuffle still Fisher–Yates then **current song to index 0**. | **One** icon after Next. Cycles Sequence (`Icons.Filled.Repeat`) → Shuffle (`Shuffle`) → Repeat one (`RepeatOne`) → Sequence. Tooltip/contentDescription `Play mode: {current}`. Shuffle-current-first and repeat-one next/prev stay. |
| 081 | `=====` is lyric **text** from the LRC (plain `.line`). | **Keep as-is.** Do not hide, restyle, or treat as a UI separator. |
| 082 | `#div_result { overflow: hidden }` — clipped lines, no fade. | Top/bottom gradient mask (`graphicsLayer` + `drawWithContent` `BlendMode.DstIn`). |
| 083 | Fullscreen is `fa-arrows-alt` (`body-fs`) on the playground. No drag handle. | Short horizontal handle between lyrics and controls. Drag down → animate lyrics fullscreen. Drag up, Back, or tap → restore. Tooltip/contentDescription. |
| 084 | Player bar: shuffle \| prev \| play \| next \| repeat. Speed ± lives in **settings**, not the bar. | One row: speed chip, previous, −5s, play, +5s, next, play mode. Speed is a compact `1.1×` chip (menu of ±0.1 steps, clamp 0.5–2.0). Separate speed row removed. |
| 085 | `.ll-play-pause` is a CSS triangle. | ~64.dp filled circular Play/Pause (`primaryContainer`). |
| 086 | Offset is in the settings overlay: caret-up `updateSyncLyric(-100)` title *Sync lyric: −100ms (client only)*; caret-down `+100`. `offsetTime` is a class field; reset when a new lyric loads; never `saveSetting`, never API. | Hidden until the user taps the lyric pane; auto-hide after a few seconds. Only small low-opacity minus/plus icons (no “Offset” text). Tooltips: *Lyric 100ms earlier* / *Lyric 100ms later* (minus = −100ms = earlier, matching web). Session/per-song only. |
| 087 | Lyric card opens from settings (`createLyricCard`), not inside `#div_result`. | Remove the in-pane Image button. Keep the app-bar one. |
| 088 | `.song_album` shows `({album})` or `(No Album)` in `.ll_player .info`. | Hide album **everywhere** on Now Playing, even when the song has an album. Mini-player stays title+artist only (already). |
| 089 | `.progress` is a thin filled span. | Slim ~4.dp track, small round thumb, played vs remaining contrast in both themes. |
| 090 | `getAverageRGB` (brightness 1.6) tints **waveform bars only** (not ported). `picture-bg` uses the cover as a full-bleed lyric backdrop. | Subtle **gradient** from the cover Palette on Now Playing chrome. Soft theme-based gradient fallback when there is no cover — **never flat grey**. |
| 091 | CSS arrows are full opacity; `.active` is the accent. | Transport/control icons `onSurface` (not dim grey). Active state uses the accent (`primary`). |
| 092 | Karaoke fill: light `#2199F9` + unsung `#333`; dark/picture `#FC0` + unsung `#FFF` (`BaiHatService.getKaraokeWordStyle` checks `lyric-pg-light`). Passed words stay 100% fill. Light lyric pane is `#dfdfdf`, not Material grey. | Dim already-sung text slightly so the current word stands out. Fill/unsung follow the **effective** surface (light vs dark), not only `LyricTheme.LIGHT`. Light app + `picture-bg` without a cover must not look like the screenshot’s flat grey + yellow `#FC0`. Dark keeps `#FC0`. |
| 093 | `title=` on CSS buttons (Back is in the table/nav, not a now-playing app bar). | Every app-bar icon (back, download, lyric card, settings) has contentDescription + long-press tooltip via `LiliIconButton` (076). |
| 094 | Single-page app; no now-playing route to dismiss. | Swipe down closes to the previous screen / mini player. Must not steal the handle drag (083) or lyric scrolling. |
| 095 | `.body-dark` sets `color: #eee`; Font Awesome icons inherit. | Audit library, now playing, downloads, settings, lyric card for dark-on-dark icons/buttons and fix tints. |
| 096 | N/A (browser chrome). | `enableEdgeToEdge` + `SystemBarStyle` so status-bar icons are dark in light **in-app** theme and light in dark theme (not only the system night mode). Light status-bar area slightly distinct/darker if needed. |
| 097 | No “Now playing” title. `.ll_player .info` is title + artist (+ album, which 088 hides). | Drop the “Now playing” title. App bar: small rounded cover thumbnail, then title (1 line, ellipsis) and artist (1 line) stacked. Remove the duplicate title/artist block below the lyric pane. |
| 098 | `.lyric-wrapper` / `.lyric-pg-picture-bg .lyric-wrapper` `border-radius: 18px`. | ~16.dp corner radius on the lyric pane; clip content to it. |

### Offset sign (web)

```js
// Setting.js
title="Sync lyric: -100ms (client only)"  // caret-up
onClick={() => this.updateSyncLyric(-100)}
title="Sync lyric: +100ms (client only)"  // caret-down
onClick={() => this.updateSyncLyric(100)}

// BaiHat.js
updateSyncLyric = (offset) => { this.offsetTime += offset; }
// word times: startTimes[i] + offsetTime/1000 vs audio.currentTime
```

Negative user offset makes lyric timestamps fire sooner → lyrics appear **earlier**. Android already adds `userOffsetMs` the same way in `LyricSynchronizer`. Minus icon = −100ms = earlier; plus = +100ms = later.

### Offset tap vs line-seek (cleaner option)

**Any tap on the lyric pane reveals offset, and a tap on a line still seeks.** Line `clickable` calls both `onSeekToLine` and `onRevealOffset`. Empty space (the pane `Box`, not a line) only reveals. Offset icons sit in an overlay so they do not steal line taps when hidden. Auto-hide ~3s after the last offset interaction or pane tap.

Line-tap-to-seek is an Android extra (web lyric taps do not seek) and stays.

### 081 — `=====` lyric line (no production change)

The screenshot’s `=====` is timed lyric text (a separator line in that song’s LRC), not a chrome control. Web renders it as a normal `.line`. **Do not** map it to `LyricMetaFooter`, hide it, or restyle it. Add a Compose test that a line whose text is `=====` is displayed as that string.

## Approach

- `feature:player` owns chrome (app bar, gradient, handle, transport, seek, swipe-to-close).
- `feature:lyrics` owns the pane (fade, offset overlay, karaoke colours, rounded clip). `:app` wires system bars from the in-app `appTheme`.
- `core:model` `PlayMode.cycle()` / labels; `PlaybackSpeed.steps()` for the chip menu.
- `core:ui` shared buttons, palette gradient helpers, lyric colours, control tints.
- Play mode still goes through `PlayerController.setPlayMode` (shuffle puts the current song first).
- Offset stays a `MutableStateFlow` on `LyricsViewModel`. Never call the lyric/listens API. Never write DataStore/Room. Reset when `currentSong.path` changes (already true; keep the test and assert settings are untouched).
- Non-goals: waveform, backend, login, `.xtrc` translations, persisting offset.

## Files

- `docs/tasks/080-098-now-playing-polish.md` (this file)
- `docs/PLAN.md` (Phase 8 table)
- `core/model/.../PlayMode.kt` (+ test)
- `core/model/.../PlaybackSpeed.kt` (+ test)
- `core/ui/.../LiliIconButton.kt` / filled play button / palette / `LyricThemeColors.kt` / theme
- `feature/player/.../PlayerScreen.kt` and extracted composables (`PlayerTransport`, seek, speed chip, handle, background, swipe)
- `feature/player/.../PlayerCallbacks.kt`, `PlayerViewModel.kt`, `PlayerRoute.kt`, `PlayerTestTags.kt`, `strings.xml`
- `feature/lyrics/.../LyricsPane.kt`, fade modifier, offset overlay, `KaraokeFill.kt`, `LyricVisualStyle.kt`
- `app/.../MainActivity.kt`, `LiliApp.kt` / `LiliTheme.kt` for system bars
- `feature/library|downloads|settings|lyrics` screens for the dark-mode audit
- matching `*Test.kt` files
- `docs/learning/14-now-playing-polish.md` (own commit after code)

## Tests

| ID | Tests |
| --- | --- |
| 080 | `PlayMode.cycle()` unit; Compose: one `Play mode: Sequence` after Next; click Sequence→Shuffle→Repeat one→Sequence; Shuffle/Repeat-one **text chips gone**; shuffle still current-first via existing playback tests |
| 081 | Lyrics pane shows a line `=====` as that text |
| 082 | Pane (or list) tagged for fade exists; lines still displayed |
| 083 | Handle found by contentDescription; tap/drag-down callback expands (tag or height); Back restores when expanded |
| 084 | Speed, prev, −5s, play, +5s, next, play mode on one row (Y of tags); speed chip shows `1.1×`, opens menu, `0.5`/`2.0` clamp; old speed ± row and shuffle chips gone |
| 085 | Play/Pause still by contentDescription; play control size ≥ ~64.dp |
| 086 | Offset icons **not** displayed initially; tap pane or line reveals minus/plus (no “Offset: 0ms” text); line tap still seeks; ViewModel: nudge does not write settings; reset on song change |
| 087 | `LyricCardTestTags.OPEN` does not exist on the pane; app-bar lyric card still fires |
| 088 | Album tag / `(Album)` / `(No Album)` do not exist on Now Playing |
| 089 | Seek tag still present and seeking still works |
| 090 | Palette tag exists with cover override; no-cover screen still has a background tag (gradient fallback, not missing chrome) |
| 091 | Play mode selected uses accent (tag or selected semantics); transport still clickable |
| 092 | `LyricTheme.colors(effectiveLight=)` : light fill `#2199F9` + unsung `#333`; dark `#FC0`; passed-word brush is dimmed fill; picture-bg without cover on light app uses light lyric surface |
| 093 | Back / Download / Create lyric card / Settings: contentDescription + long-press tooltip |
| 094 | Swipe-down on chrome (not handle) fires `onBack`; handle drag does not |
| 095 | Dark theme: library/player/downloads/settings/lyric-card icon buttons exist and use `onSurface` (Compose under `LiliTheme(darkTheme = true)`) |
| 096 | `SystemBarAppearance.isAppearanceLightStatusBars(darkTheme)` is `!darkTheme` |
| 097 | “Now playing” text gone; app-bar title + artist tags; no duplicate title/artist below lyrics on compact |
| 098 | Lyric slot tagged as clipped/rounded; lyrics still shown |

## Done when

- Task plan committed before code; Phase 8 listed in `docs/PLAN.md`.
- Each of 080–098 is its own Conventional Commit (081 is tests-only documenting keep-as-is).
- Learning page `docs/learning/14-*.md` committed **alone** after the code.
- `./gradlew check` passes on JDK 21+.
- No backend/API changes; no `RECORD_AUDIO`; offset never persisted.
