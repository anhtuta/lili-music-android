# Library redesign (owner screenshot review)

[Learning guide index](README.md) · Tasks [106](../tasks/106-compact-library-search.md)–[110](../tasks/110-song-more-menu.md)

**What was built.** Tú’s dark Library screenshot showed a tall search box, a wrapping sort-chip row, and song rows that stacked type + date (`202` / `1/0` / `6/0` / `9`). The list is now: compact search, one sort pill, slimmer rows, overflow menu for info + download.

1. **Compact search (106).** `BasicTextField` + `OutlinedTextFieldDefaults.DecorationBox` at 48.dp with placeholder “Search title or artist”. Default `OutlinedTextField` is 56.dp min. Clear-X tooltip stays.
2. **Sort pill (107).** Chips are gone. One trailing pill shows `NAME ↑` (caps + arrow). Tap opens `DropdownMenu` of Name, Listens, Song of the year, Type, Date. Another field uses that column’s default API order; the current field toggles asc/desc. Tooltip: `Sort: Name, ascending. Tap to change`. **Not** on the web: web `BaiHatTable.js` sorts by clicking table headers and always sends a fixed order per column (title ASC, listens DESC, …). Android has no table, so a pill is the phone control. Artist is **not** a `sortBy` — unknown columns 500 on the live API.
3. **Row fields (108).** Title, artist, listens, soy only. Type and date live in Song info (110). Cover / letter placeholder stay.
4. **Listens + soy (109).** One `Row(Arrangement.SpaceBetween)`: Headphones + count on the left (tooltip “Listens”); soy badge on the right, or nothing.
5. **More menu (110).** `MoreVert` (“More options”) → Song info (`AlertDialog` of every `Song` field, `None` when empty, lyric type from the file extension) and Download with 074 states. `NetworkSong.updated_at` is not on `Song`/Room in v1, so it is not in the dialog.

**Status:** Follow-up on the Now Playing polish PR. Phase 8 080–098 merged in [PR #24](https://github.com/anhtuta/lili-music-android/pull/24).

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `BasicTextField` + `DecorationBox` | You own height/padding; Material only draws outline, icons, placeholder | A plain `<input>` inside a styled box; `OutlinedTextField` is a `<TextField variant="outlined">` with a 56.dp min height you cannot shrink |
| `contentPadding` | Insets inside the outline | CSS `padding` on the input |
| `DropdownMenu` / `DropdownMenuItem` | Anchored list of actions | A `<select>` / Radix dropdown; web catalog uses `<th onClick>` instead |
| Caps pill + arrow | Current sort field and direction in one chip | CSS `border-radius: 999px` button (`NAME ↑`); not a table header |
| `Arrangement.SpaceBetween` | First child start, last child end | CSS `justify-content: space-between` |
| `AlertDialog` | Modal with title, scrollable body, Close | A React modal (`BaiHatTable` picture `Modal`); we use it for song fields, not the cover |
| `MoreVert` | ⋮ overflow | A kebab/overflow menu; web has no per-row download or info menu |

## Code to focus on

1. [LibrarySearchField.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibrarySearchField.kt) — 48.dp outlined `BasicTextField`.
2. [LibrarySortPill.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibrarySortPill.kt) — pill + menu; `onSortFieldChosen`.
3. [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) — same field toggles order; new field uses `defaultOrder`.
4. [LibrarySongRow.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibrarySongRow.kt) — SpaceBetween meta; MoreVert menu.
5. [SongInfoFields.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/SongInfoFields.kt) — every `Song` property + lyric extension.
6. Recovered web `BaiHatTable.js` — table headers + type/date columns (Android no longer mirrors those on the row).

## How to test on the arm64 emulator

```bash
make install
```

1. Library search should be a short field; type a query; clear-X still works.
2. Sort pill on the right under search: tap, pick Listens, list reorders; tap Listens again to flip the arrow.
3. Rows show title, artist, headphones+count, soy if any — no type, no wrapping date.
4. ⋮ → Song info lists soy `None` when missing; Download / Cancel / Delete match download state.
