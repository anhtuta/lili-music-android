# Phase 13 — Library chrome & overflow trigger (118–122)

## What we built

1. **Overflow trigger (118).** Now Playing menu opens from ⋮ (`MoreVert`); Settings stays only inside the menu.
2. **Sort pill (119).** `DATE ↓` / field pill is start-aligned under search.
3. **Scrollable chrome (120).** Title, search, and sort are `LazyColumn` items so they scroll away with songs.
4. **Scrollbar (121).** `LibraryLazyScrollbar` overlays a draggable thumb from `LazyListState` scroll metrics.
5. **Right-edge alignment (122).** `LibraryChromeInsets` shares 16.dp start + 4.dp end across header, search, and row ⋮.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `LazyColumn` + `rememberLazyListState` | One scrollable list for chrome + rows | Virtualized list with a sticky header removed — everything in one scroller |
| `LazyListScrollbarMath` | Thumb size/offset from visible item indices | Custom scrollbar `thumb` CSS from `scrollTop / scrollHeight` |
| `LibraryChromeInsets` | Shared horizontal padding constants | Design-token spacing for a column grid |
| `MoreVert` vs `Settings` icon | Overflow vs settings action | `⋮` menu vs gear in a toolbar |

## Code to focus on

1. [NowPlayingOverflowMenu.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/NowPlayingOverflowMenu.kt)
2. [LibraryScreen.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryScreen.kt) — single list + scrollbar `Box`
3. [LibraryLazyScrollbar.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt) + [LazyListScrollbarMath.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LazyListScrollbarMath.kt)
4. [LibraryChromeInsets.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryChromeInsets.kt)
