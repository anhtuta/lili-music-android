# 120 — Library title, search, and sort scroll with list

## Goal

Scrolling the song list moves **Library** header, search, and sort off-screen together (not pinned above the list). App actions (refresh, downloads, settings) stay reachable in the scrolling header row.

## Approach

Replace `Column` + weighted `LazyColumn` with one `LazyColumn`: items for chrome (title/actions row, optional offline banner, search, sort), then song rows. Remove sticky `TopAppBar` title strip or fold title into first item. Keep `PullToRefreshBox`.

## Files

- `feature/library/LibraryScreen.kt`
- New `LibraryScrollableHeader.kt` (optional small extract)

## Tests

Compose: after scrolling first song under fold, search/sort are not displayed (or use lazy scroll test).

## Done when

Sticky chrome gone, pull-to-refresh works, tests pass, `./gradlew check`.
