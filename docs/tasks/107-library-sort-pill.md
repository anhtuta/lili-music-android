# 107 — Single sort pill + dropdown

## Goal

The chip row (“Sort”, Song of the year / Type / Date, separate arrow) is gone. One pill shows the current field in caps plus ↑/↓ for order. Tap opens a menu of every current `SongSort` value. Choosing another field sorts with that field’s default direction. Choosing the current field toggles asc/desc. Tooltip: `Sort: Name, ascending. Tap to change`. Refresh / downloads / settings stay in the app bar.

## Approach

Web `BaiHatTable.js` sorts by clicking table headers (`title` ASC, `listens` DESC, `song_of_the_year` DESC, `type` ASC, `createdDate` DESC). Android has no table; a pill + `DropdownMenu` is the phone equivalent.

Do **not** add Artist as a `sortBy` — live API 500s on unknown columns. Fields stay: Name (`title`), Listens, Song of the year, Type, Date.

Place the pill under the search box, trailing. Current menu row gets a check and the current arrow. `LibraryViewModel.onSortFieldChosen` toggles when the field is already selected, otherwise `sort.defaultOrder`.

Non-goals: client-side re-sort without a refetch; extra columns.

## Files

- `feature/library/.../LibraryScreen.kt`
- `feature/library/.../LibraryViewModel.kt`
- `feature/library/.../LibraryRoute.kt`
- strings + tests

## Tests

Pill label/arrow match state. Menu lists every `SongSort`. Selecting another field changes sort + default order. Re-selecting the current field toggles order. Tooltip / contentDescription present.

## Done when

- One pill + menu; no chip row; sort behaviour matches above.
- Tests pass; `./gradlew check`.
