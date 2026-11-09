# 122 — Library row ⋮ aligns to chrome right edge

## Goal

Per-row **More** (⋮) aligns with the right edge of the search field and the library settings icon in the header.

## Approach

Share `LibraryChromeInsets` horizontal end inset with search/header; reduce trailing row padding so `LiliIconButton` more aligns to that edge (document dp value).

## Files

- `feature/library/LibrarySongRow.kt`
- `LibraryChromeInsets.kt` (or existing metrics object)
- Row alignment test

## Tests

Compose layout: more icon right bound matches search outline right (tolerance ~2dp).

## Done when

Visual alignment per screenshot, tests pass, `./gradlew check`.
