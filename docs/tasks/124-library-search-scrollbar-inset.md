# 124 — Library search clears scrollbar strip

## Goal

Search field right edge lines up with the settings icon; search no longer extends under the scrollbar track.

## Approach

Reserve scrollbar track width via `LazyColumn` `contentPadding.end` (shared constant with scrollbar width). Do not widen search beyond chrome trailing edge.

## Files

- `feature/library/LibraryChromeInsets.kt`
- `feature/library/LibraryScreen.kt`
- Alignment test update if bounds shift

## Tests

`LibraryRowMoreAlignTest` still passes (search/settings/more right bounds within tolerance).

## Done when

Visual alignment per screenshot, tests pass.
