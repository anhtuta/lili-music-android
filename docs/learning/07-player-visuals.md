# Task 009 — Phase 5: player visuals and settings

[Learning guide index](README.md) · Task [009](../tasks/009-player-visuals.md)

**What was built.** A Settings screen (Library and Player app bars) backed by DataStore. Lyric playground settings from the web player: alignment, lyric theme (`dark` / `light` / `picture-bg`), karaoke, blur glow, gigantic line, word/line appear, show translation. App chrome light/dark (`appTheme`, default light). Cover art tints now-playing via Android Palette. `picture-bg` uses the song cover as a blurred lyric backdrop.

**Status:** This PR.

> [!NOTE]
> **Gigantic line defaults off.** The web HTML starts with class `div-res-big-active-line`, but [PLAN.md](../PLAN.md) says gigantic line is off when the key is missing. Android follows PLAN. Translation also defaults **off** (web `.translate`); Phase 4 always showed `[tranN]` lines.

Speed ±0.1× was already persisted in playback (task 006). Settings exposes the same control through `PlayerController.setSpeed`. In-app volume, waveform, lyric-card export, downloads, and sleep timer are not in this phase.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| DataStore preferences | Typed key-value file that survives process death | `localStorage` key `LILIANA_SETTINGS` |
| `SettingsRepository` | App port over those keys | A Spring `@Service` wrapping a properties file |
| `PlayerSettings` | Immutable snapshot of lyric/app visuals | A React context value / Redux slice |
| `LyricTheme` vs `AppTheme` | Playground colours vs whole-app Material theme | Web `theme` (`lyric-pg-*`) vs `appTheme` (`body-light` / `body-dark`) |
| `LiliTheme(darkTheme)` | Material 3 colour scheme for the app chrome | A CSS `:root` theme class on `<body>` |
| Palette | Picks dominant colour from a bitmap | Web `getAverageRGB` (that one only tinted waveform bars) |
| `rememberCoverPalette` | Compose effect: Coil loads cover → Palette → colours | `useEffect` that draws an image to canvas |
| `picture-bg` | Cover as full-bleed blurred lyric backdrop | `#div_background` + `filter: blur(10px)` |
| Word / line appear | Hide future words / inactive lines until their time | CSS `.not-pass-word { opacity: 0 }` and `.line-appear .line { opacity: 0 }` |
| Gigantic line | Active line ~1.8× font size | `.div-res-big-active-line .line-active span` |
| Blur mode | White glow on lyric words | CSS `filter: drop-shadow(0 0 5px #fff)` |
| FilterChip / Switch | Exclusive choice vs on/off | Radio group vs checkbox |

Kotlin `enum class` with `fromStorage` is a parser plus a closed set of values. Unknown strings fall back (center / `picture-bg` / light) instead of throwing — same idea as `PlayMode.fromStorage`.

## Code to focus on

1. [PlayerSettings.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/PlayerSettings.kt) — defaults from PLAN.
2. [SettingsPreferencesDataSource.kt](../../core/datastore/src/main/java/com/anhtuta/lilimusic/core/datastore/SettingsPreferencesDataSource.kt) — DataStore keys; missing key → default.
3. [DataStoreSettingsRepository.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/repository/DataStoreSettingsRepository.kt) — repository the UI talks to.
4. [SettingsViewModel.kt](../../feature/settings/src/main/java/com/anhtuta/lilimusic/feature/settings/SettingsViewModel.kt) / [SettingsScreen.kt](../../feature/settings/src/main/java/com/anhtuta/lilimusic/feature/settings/SettingsScreen.kt) — chips, switches, speed.
5. [LyricsPane.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricsPane.kt) / [LyricVisualStyle.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricVisualStyle.kt) — apply the settings.
6. [LyricThemeColors.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/theme/LyricThemeColors.kt) — dark / light / picture-bg palettes; karaoke fill `#2199F9` / `#FC0`.
7. [CoverPalette.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/palette/CoverPalette.kt) — RGB → background + onBackground from luminance.
8. [CoverPaletteRemember.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/CoverPaletteRemember.kt) — Coil `allowHardware(false)` so Palette can read pixels.
9. [LiliApp.kt](../../app/src/main/java/com/anhtuta/lilimusic/LiliApp.kt) — `LiliTheme(darkTheme = appTheme == DARK)` and Settings route.

Defaults, from `PlayerSettings.kt`:

```kotlin
data class PlayerSettings(
    val lyricAlign: LyricAlign = LyricAlign.CENTER,
    val lyricTheme: LyricTheme = LyricTheme.PICTURE_BG,
    val karaokeMode: Boolean = true,
    val blurMode: Boolean = false,
    val giganticLine: Boolean = false,
    val wordAppear: Boolean = false,
    val lineAppear: Boolean = false,
    val translate: Boolean = false,
    val appTheme: AppTheme = AppTheme.LIGHT,
)
```

Why `allowHardware(false)`? Coil may decode into a GPU-only bitmap. Palette (and `getAverageRGB` on web) must read pixel bytes on the CPU. Same class of bug as a canvas tainted by CORS on the web.

Why two themes? Lyric playground can be `picture-bg` (dark text on a cover) while the rest of the app is light Material 3 — that is how the web player works (`lyric-pg-picture-bg` inside `body-light`).
