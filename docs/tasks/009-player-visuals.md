# 009 — Player visuals and settings

## Goal

A listener can open Settings, persist player/lyric visuals (alignment, lyric theme, karaoke, blur, gigantic line, word/line appear, translation, app light/dark), and see those choices on the lyrics pane. Now-playing chrome tints from cover art via Palette. `picture-bg` uses the cover as a blurred lyric backdrop.

## Approach

Implements PLAN Phase 5 (050–053). Backend unchanged. Listener only. Package `com.anhtuta.lilimusic`.

Phase 4 (008) already shipped highlight, autoscroll, karaoke fill, countdown, `.xlrc` translations, and session ±100ms. This task adds the **remaining visual toggles**, lyric themes, cover Palette, and the Settings screen. Downloads, sleep timer, lyric-card export, waveform / `RECORD_AUDIO`, and HTTPS App Links stay later phases.

Behaviour is copied from the recovered web player (`anhtuta/lili-player` source maps: `Setting.js`, `BaiHat.js`, `BaiHat.css`, `UtilService.js`, `Constants.js`). Private `liliana-player-reactjs` remains unreadable.

### Settings (DataStore, web `LILIANA_SETTINGS`)

| Key | Values | Default when missing |
| --- | --- | --- |
| `lyricAlign` | `left` / `center` / `right` | **center** (web `#div_result` inline style) |
| `theme` | `dark` / `light` / `picture-bg` | **`picture-bg`** |
| `karaokeMode` | bool | **true** |
| `blurMode` | bool | off |
| `giganticLine` | bool | **off (PLAN.md)** — web HTML has `div-res-big-active-line` on by default; this task follows PLAN, not that class |
| `wordAppear` | bool | off |
| `lineAppear` | bool | off |
| `translate` | bool | off (Phase 4 always showed `[tranN]`; this phase gates them, matching web `.translate`) |
| `appTheme` | `light` / `dark` | **light** (separate from lyric playground theme) |

Speed ±0.1× is already persisted (task 006 / `PlaybackPreferences`). Settings exposes the same control via `PlayerController.setSpeed` (web keeps speed in the settings panel). In-app volume is **not** ported (PLAN: system/media volume). `showWaveform`, `flipWaveform`, `fullscreen`, lyric-card export stay out of v1 / later phases.

User lyric offset stays session-only (008).

### Visuals (050–052)

- **Alignment** — lyric list `TextAlign` / row alignment.
- **Gigantic line** — active line ~1.8× (web `.div-res-big-active-line`).
- **Word appear** — future words opacity 0 until their time (web `.not-pass-word`).
- **Line appear** — inactive lines opacity 0; future words on the active line also hidden (web `.line-appear`).
- **Translation** — `.xlrc` `[tranN]` visible only when `translate` is on.
- **Blur** — white drop-shadow glow on words (web `drop-shadow`).
- **Karaoke** — fill on by default; off uses solid active colour (web `.karaoke-off`). Light fill `#2199F9`, dark / picture-bg fill `#FC0`.
- **Lyric themes** — `dark` (`#333` / `#dfdfdf`), `light` (`#dfdfdf` / `#333`), `picture-bg` (blurred cover + `rgba(0,0,0,0.4)` overlay, light text).
- **Cover Palette** — Coil loads the cover with `allowHardware(false)`; Android Palette dominant/vibrant swatch tints now-playing chrome. Placeholder cover → Material background. Web `getAverageRGB` (brightness 1.6) only tinted waveform bars, which we are not porting.

### Modules

- **`:core:model`** — `LyricAlign`, `LyricTheme`, `AppTheme`, `PlayerSettings` (pure Kotlin).
- **`:core:datastore`** — `SettingsPreferences` on the existing preferences DataStore (sort / playback keys unchanged).
- **`:core:data`** — `SettingsRepository` wrapping DataStore (PLAN: Settings → repository).
- **`:core:ui`** — `LyricThemeColors`, Palette helper, `LiliTheme(darkTheme)`.
- **`:feature:settings`** — Settings screen + Hilt ViewModel (replaces the empty stub).
- **`:feature:lyrics`** — apply visual settings + cover URL for `picture-bg`.
- **`:feature:player`** — Palette chrome; settings action on the player app bar.
- **`:feature:library`** — settings action on the library app bar.
- **`:app`** — `SettingsDestination`, host route, apply `appTheme` at the composition root.

### Non-goals

Downloads, sleep timer, lyric card export/share, HTTPS App Links, waveform / `RECORD_AUDIO`, Zing streaming, in-app volume slider, admin/login, changing hello_lumen.

## Files

- `docs/tasks/009-player-visuals.md` (this file)
- `docs/PLAN.md` (Phase 5 progress)
- `core/model/` — `PlayerSettings`, `LyricAlign`, `LyricTheme`, `AppTheme` + tests
- `core/datastore/` — `SettingsPreferences` + parser + DataStore tests
- `core/data/` — `SettingsRepository` + fake + tests
- `core/ui/` — lyric theme colours, Palette helper, theme wiring tests
- `feature/settings/` — screen, ViewModel, strings, tests (stub filled in)
- `feature/lyrics/` — visual settings on `LyricsPane` / ViewModel
- `feature/player/` — Palette background, settings nav callback
- `feature/library/` — settings nav callback
- `app/` — settings route, `appTheme` on `LiliTheme`

## Tests

- **model (JVM):** defaults match the table above; unknown storage values fall back; `fromStorage` round-trips web strings (`picture-bg`, `left`/`center`/`right`, `light`/`dark`).
- **datastore (parser JVM + Robolectric DataStore + Turbine):** missing keys → defaults; persist/restore every key; unknown theme → `picture-bg`.
- **data (fakes + Turbine):** repository emits DataStore updates; setters forward.
- **ui (Robolectric):** `LyricThemeColors` for each theme; Palette helper from a solid-colour bitmap; `LiliTheme(darkTheme = true/false)`.
- **settings ViewModel (Turbine + fakes):** load defaults; each setter updates state; speed ±0.1 goes through `PlayerController`.
- **settings Compose (Robolectric):** tapping alignment / theme / toggles / app theme / speed fires callbacks.
- **lyrics ViewModel (Turbine):** `uiState.settings` follows repository; cover URL from current song.
- **lyrics Compose:** translation hidden by default, shown when `translate`; pane test tags for align/theme/modes; word-appear hides a future word’s text.
- **player Compose:** Palette colours tint the chrome test tag; settings action callback; missing cover still uses placeholder (no crash).
- **library Compose:** settings action callback.
- **app:** settings route is reachable from the nav host.

No emulator. No Media3 downloads.

## Commit breakdown

1. `docs(tasks): add 009 player visuals plan` — this file only.
2. `feat(model): add player visual settings types`
3. `feat(datastore): persist lyric and app settings`
4. `feat(data): add SettingsRepository`
5. `feat(ui): add lyric theme colours and cover Palette`
6. `feat(settings): add settings screen and ViewModel`
7. `feat(lyrics): apply visual settings to the lyrics pane`
8. `feat(player): tint now-playing with cover Palette`
9. `feat(library): add settings navigation entry`
10. `feat(app): host settings route and app theme`
11. `docs: mark Phase 5 player visuals progress in PLAN`
12. `docs(learning): add player visuals guide` — alone, after code.

Implementation commits include tests for that layer.

## Done when

- Task plan committed **before** code (this file).
- Settings persist across process death (DataStore). Defaults match PLAN (gigantic line **off** even though web HTML starts with the class).
- Lyrics pane respects alignment, theme, karaoke, blur, gigantic line, word/line appear, translation.
- `picture-bg` uses the song cover as a blurred backdrop; now-playing chrome uses Palette.
- App chrome light/dark follows `appTheme` (default light).
- Navigation: Library and Player can open Settings.
- No downloads, sleep timer, waveform, or `RECORD_AUDIO`.
- Learning-guide page appended in its own commit.
- `./gradlew check` passes.
