# 138 — Persist per-song user lyric offset

## Goal

When the user nudges lyric sync (±100 ms client offset), that **user** adjustment is remembered for each song and restored after leaving Now Playing, reopening the player, or process death. File `[offset:…]` meta stays in the lyric file; this task only persists `userOffsetMs` (same semantics as today).

## Approach

1. On song change (`currentSong.path` or lyric file identity), load saved user offset for that song (default `0L`) into `LyricsViewModel` instead of always resetting to `0L`.
2. On each nudge, update in-memory offset and **persist** (debounce optional if writes are frequent).
3. Overlay label continues to show **combined** offset (`meta + user`); task 137.

### Storage choice — **not decided yet**

| Option | Sketch | Tradeoff |
| --- | --- | --- |
| **DataStore** | Map or prefixed keys `path → userOffsetMs` alongside existing settings | Minimal schema; less natural for per-song catalog data |
| **Room** | Table or column keyed by `song.path` next to catalog/lyric cache | Fits existing `core:database` patterns; small migration |

Owner will pick DataStore vs Room before implementation. Document the decision in this file’s **Approach** section when chosen.

### Non-goals

- Syncing offset to the Lumen API or web `saveSetting`.
- Persisting file meta offset (comes from the lyric file only).
- Changing ±100 ms step or combined-offset display behavior.

## Files (provisional)

Depends on storage choice. Likely touch:

- `feature/lyrics/.../LyricsViewModel.kt` — load/save on song change and nudge
- **DataStore path:** `core:datastore/...` (+ optional `core:data` repository)
- **Room path:** `core:database/...` (+ DAO, migration, `core:data` repository)
- Tests: ViewModel (Turbine), repository or DAO/DataStore fakes

## Tests

- Nudge on song A, switch to B and back: A’s user offset restored; B unchanged.
- New song with no saved row: `userOffsetMs == 0L`; combined label still includes file meta.
- Persistence survives ViewModel recreation (repository/DAO test; ViewModel with fake repo).

## Done when

- Storage approach recorded in this task doc.
- User offset survives back navigation and app restart per song.
- Song change still resets in-memory state to that song’s stored value (not a global zero).
- Tests pass; `./gradlew check`.
