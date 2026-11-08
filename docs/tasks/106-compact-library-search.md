# 106 — Compact library search field

## Goal

The Library search box is about 44–48.dp tall, still readable, with the search icon and placeholder “Search title or artist”.

## Approach

Default `OutlinedTextField` min height is 56.dp. Replace it with `BasicTextField` + `OutlinedTextFieldDefaults.DecorationBox` and smaller `contentPadding`. Keep single-line, clear-X (`LiliIconButton` tooltip), and the existing search test tag. Use a **placeholder**, not a floating label.

Non-goals: changing filter logic; adding voice search.

## Files

- `feature/library/.../LibraryScreen.kt` (or a small `LibrarySearchField.kt`)
- `feature/library` tests

## Tests

Tagged search node height is in 44.dp–52.dp. Typing still updates the query. Placeholder text is shown when empty.

## Done when

- Search is shorter than a default outlined field; query + clear still work.
- Tests pass; `./gradlew check`.
