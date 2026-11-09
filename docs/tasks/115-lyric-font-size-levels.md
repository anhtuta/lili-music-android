# 115 — Lyric font size (5 levels)

## Goal

Persisted setting with five lyric size presets. Current size is **level 2 (Normal / Medium)**; one step smaller and three larger.

## Approach

Add `LyricFontSize` enum in `:core:model` (e.g. Compact, Normal, Comfortable, Large, Extra large) with scale multipliers around `bodyLarge`. Default `NORMAL`. DataStore key + `SettingsRepository`. Settings screen row (chips or single-select). Apply scale in `KaraokeFill` / `LyricLineMetrics.baseFontSize(settings)`.

## Files

- `core/model`, `core/datastore`, `core/data`, `feature/settings`, `feature/lyrics`
- Strings for level labels

## Tests

JVM: enum scale and DataStore round-trip; lyric font size affects computed sp; Settings screen shows selection.

## Done when

Five levels work in player + settings, tests pass, `./gradlew check`.
