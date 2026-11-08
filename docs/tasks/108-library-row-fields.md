# 108 — Song row shows title, artist, listens, soy only

## Goal

Each library row shows only title, artist, listen count, and Song of the year. Type and date are gone so a date cannot wrap as `202\n1/0\n6/0\n9`. Cover thumbnail (or letter placeholder) stays.

## Approach

Remove `type` and `formatCreatedDate` from `SongMetaRow`. Those values still exist on the model and will appear in the 110 info dialog. Cover / placeholder / click-to-preview stay.

Non-goals: changing cover size; hiding listens/soy (109 restyles them).

## Files

- `feature/library/.../LibrarySongRow.kt`
- `feature/library` tests

## Tests

Title, artist, listens (and soy when present) are shown. Type and date tags/text are absent even when the song has them.

## Done when

- Rows no longer show type or date.
- Tests pass; `./gradlew check`.
