# 110 — More menu: song info + download

## Goal

The per-row Download icon is a 3-dots `MoreVert` button (tooltip “More options”). The menu has (a) Song info (Info icon) opening a dialog of every domain/API field, with “None” for null/empty, including missing soy and lyric type; (b) Download with 074 states (Download / cancel+progress / done / retry) and matching labels. Menu entries are labeled.

## Approach

`DropdownMenu` from `LiliIconButton(MoreVert)`. Info: `AlertDialog` + scrollable list built from a helper that walks `Song` (all properties) plus lyric type from the lyric file extension (`lrc` / `trc` / `xlrc` / `xtrc` / None). `NetworkSong.updated_at` is not on `Song`/Room in v1 — do not add a DB migration here; document it. Download menu item calls the same `onDownloadAction`.

Non-goals: editing songs; fetching extra admin fields.

## Files

- `feature/library/.../LibrarySongRow.kt`
- `feature/library/.../SongInfoFields.kt`
- `feature/library/.../SongInfoDialog.kt`
- strings + tests

## Tests

More opens a menu with Song info and a download-state label. Info dialog lists every field; missing soy is “None”. Download from the menu fires the same callback. 074 cancel/delete labels still appear from the menu when status is downloading/completed.

## Done when

- Dots menu + info dialog + download states; no standalone row Download button.
- Tests pass; `./gradlew check`.
