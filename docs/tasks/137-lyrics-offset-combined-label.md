# 137 — Show combined lyric offset in overlay label

## Goal

When offset controls are visible, the ms label shows the **total** sync offset (`meta.offsetMs + userOffsetMs`), including the file `[offset:…]` tag, not only the client nudge.

## Approach

Drive `OffsetRow` from `LyricSyncFrame.combinedOffsetMs` (already `meta.offsetMs + userOffsetMs` in `LyricSynchronizer`). ±100ms still adjusts `userOffsetMs` in the ViewModel; sync recomputes `combinedOffsetMs`. No persistence (session-only user nudge unchanged).

Non-goals: per-song persistence, API changes.

## Files

- `feature/lyrics/.../LyricsPane.kt`
- `feature/lyrics/.../LyricsPaneTest.kt`
- `docs/PLAN.md` (task row)

## Tests

Compose: lyric with `[offset:-200]` shows `Offset: -200ms` before nudge; after later, `-100ms`. Update existing nudge label test to resync frame when `userOffsetMs` changes (matches ViewModel).

## Done when

- Overlay label reflects file meta offset plus user nudge.
- Tests pass; `./gradlew check`.
