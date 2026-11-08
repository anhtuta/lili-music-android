# 119 — Library sort pill start-aligned

## Goal

The sort pill sits on the **left** under search (web-style field picker), not end-aligned.

## Approach

`LibrarySortPill` row uses `Arrangement.Start` (remove `End`).

## Files

- `feature/library/LibrarySortPill.kt`
- `LibraryScreenTest` / sort pill layout test if any

## Tests

Compose: sort pill’s left edge aligns with search field start (same horizontal inset).

## Done when

UI matches goal, tests pass, `./gradlew check`.
