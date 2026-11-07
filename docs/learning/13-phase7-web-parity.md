# Phase 7 — compact now-playing, transport icons, lyric-card fonts, cancel download

[Learning guide index](README.md) · Tasks [070](../tasks/070-compact-lyrics.md), [071](../tasks/071-transport-icons.md), [072](../tasks/072-lyric-card-fonts.md), [073](../tasks/073-cancel-download.md), [074](../tasks/074-download-action-icon.md)

**What was built.** Owner-picked follow-ups after PR #21:

1. **Compact now-playing (070).** Web `BaiHat.css` hides `#div_left_wrapper` (the spinning cover) at `max-width: 720px`, so `#lyric_playground` (`calc(100vh - 100px)`) is lyrics immediately and `.ll_player` stays as the bar under it. Android now does the same at **&lt; 720.dp**: no cover, lyrics `weight(1f)` fill the body, seek / transport / speed pinned at the bottom. At **≥ 720.dp** cover and lyrics sit side by side with the same bar. The 067 “scroll past the cover to a full-height lyric slot” column is gone.
2. **Transport icons (071).** Previous / −5s / Play / +5s / Next are `IconButton`s (`SkipPrevious`, `Replay5`, `PlayArrow`/`Pause`, `Forward5`, `SkipNext`) with contentDescriptions, matching web `ll-btn-prev` / `ll-btn-next` / play-pause as icons rather than text.
3. **Lyric-card fonts (072).** The web card loads Google Fonts `Lavishly Yours`, `Lobster`, `Pacifico`, `Borel` (OFL). Those Regular TTFs are now `res/font` in `:feature:lyrics`. The picker and JPEG export use them. Open Sans / Verdana / Georgia stay system sans/serif so we do not ship extra families.
4. **Cancel download (073).** Queued or downloading songs get **Cancel**. That stops Media3 (`engine.remove` → `DownloadService.sendRemoveDownload`), deletes the partial cover and lyric cache, and deletes the Room `downloads` row so the UI returns to not-downloaded. Completed/failed still use Delete (which keeps the lyric cache for online play).
5. **Download action icons (074).** Library rows and Now playing no longer show a text **Download** button. Idle is `Icons.Filled.Download` (contentDescription **Download**), queued/downloading is a `CircularProgressIndicator` (**Cancel**, tap still runs 073), completed is `DownloadDone` (**Delete**), failed is `Refresh` (**Retry**). The Downloads screen still uses text Cancel / Delete / Retry.

**Status:** This PR.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `BoxWithConstraints` | Child composable sees `maxWidth` / `maxHeight` of the parent | A `@media (max-width: 720px)` check, but measured from the parent, not the window |
| `dp` | Density-independent pixel | CSS `px` on a 160dpi baseline; `720.dp` ≈ web `720px` at mdpi |
| `WindowSizeClass.Compact` | Material width class, Compact **&lt; 600.dp** | A coarser breakpoint (`sm`) — **too early** vs web 720px, so we did not use it |
| `Modifier.weight(1f)` in a `Column` | Take leftover height after siblings | `flex: 1` on the lyric playground; the bar does not flex |
| `IconButton` | 48.dp minimum touch target, icon only | An icon-only control; TalkBack needs `contentDescription` |
| `contentDescription` | Spoken / test name of an icon | `aria-label` / `title` on a CSS-arrow button |
| `Icons.Filled.*` | Material Symbols as Compose `ImageVector` | Inline SVG / CSS triangles (`ll-btn-prev` borders) |
| `material-icons-extended` | Extra icons (`Replay5`, `Forward5`) not in the core set | An extra icon font, not the default Material subset |
| `res/font` | TTF/OTF compiled into the APK | `@font-face` with the file shipped, not a Google Fonts CSS `@import` |
| `ResourcesCompat.getFont` | Load that `R.font` as an Android `Typeface` | `new FontFace("Lobster").load()` then `ctx.font` |
| Compose `FontFamily(Font(R.font.…))` | Same file for Compose `Text` | CSS `font-family: 'Lobster'` after `@font-face` |
| OFL | SIL Open Font License; license files in `res/raw` + `docs/licenses/` | Shipping the webfont zip’s `OFL.txt` next to the TTF |
| `DownloadService.sendRemoveDownload` | Tells Media3 to abort and drop the cache entry | `abort()` on a fetch + delete the partial blob |
| `LyricRepository.deleteCached` | Delete the Room lyric **body** | Clearing `localStorage` for that lyric file; `remove()` of a completed download **keeps** this cache |
| `item.isActive` | `QUEUED` or `DOWNLOADING` | A row still in the download queue |
| `CircularProgressIndicator` | Material spinner; determinate takes `progress = { 0f..1f }` | A CSS spinner / `<progress>`; tap target is still the `IconButton` |
| `DownloadDone` | Check-on-download Material icon | A “downloaded” badge, not a separate “Delete” text label |

Why 720.dp and not Compact 600.dp? Web CSS is `max-width: 720px`. Material Compact would hide the cover on a 7″ tablet in portrait (~600–700.dp) while the web still showed it. `BoxWithConstraints.maxWidth` is the Now playing pane, which matches “how wide is this player” better than the activity’s window class.

Why cancel also deletes lyrics, but delete-completed does not? Online play still needs the lyric Room row. Cancelling means “I did not want this download”; leftover sidecar files would look like a successful cache of a download the user abandoned.

## Code to focus on

1. [PlayerLayout.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerLayout.kt) — `CompactMaxWidth = 720.dp`.
2. [PlayerScreen.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerScreen.kt) — `PlayerBody` switches compact vs expanded; `CompactPlayerBody` / `ExpandedPlayerBody`; `PlayerTransport` icons.
3. [PlayerScreenTest.kt](../../feature/player/src/test/java/com/anhtuta/lilimusic/feature/player/PlayerScreenTest.kt) — class `@Config(qualifiers = "w800dp-h720dp")` so expanded tests are not clipped by Robolectric’s default ~320.dp window; compact tests wrap in `Box.size(400.dp, 720.dp)`.
4. [LyricCardFonts.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricCardFonts.kt) — `R.font` → Compose family + Canvas `Typeface`.
5. [LyricCardFont.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/LyricCardFont.kt) — same `<select>` names as web `CardConfiguration`.
6. [DefaultDownloadRepository.kt](../../core/data/src/main/java/com/anhtuta/lilimusic/core/data/repository/DefaultDownloadRepository.kt) — `cancel` vs `remove`.
7. [DownloadsScreen.kt](../../feature/downloads/src/main/java/com/anhtuta/lilimusic/feature/downloads/DownloadsScreen.kt) — Cancel on `isActive`, Delete otherwise.
8. [LibraryViewModel.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryViewModel.kt) / [PlayerViewModel.kt](../../feature/player/src/main/java/com/anhtuta/lilimusic/feature/player/PlayerViewModel.kt) — download action on QUEUED/DOWNLOADING calls `cancel`.
9. [DownloadActionButton.kt](../../core/ui/src/main/java/com/anhtuta/lilimusic/core/ui/download/DownloadActionButton.kt) — shared icon; tests use `onNodeWithContentDescription("Download")`.

Compact vs expanded (from `PlayerBody`):

```kotlin
BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    if (PlayerLayout.isCompact(maxWidth)) {
        CompactPlayerBody(/* lyrics fill; no cover */)
    } else {
        ExpandedPlayerBody(/* Row: cover | lyrics */)
    }
}
```

Compact lyrics + pinned bar:

```kotlin
Column(Modifier.fillMaxSize()) {
    Box(Modifier.weight(1f).fillMaxWidth()) { lyrics() }
    PlayerControlsBar(/* seek, transport, speed; headings on compact */)
}
```

Cancel (from `DefaultDownloadRepository`):

```kotlin
override suspend fun cancel(songPath: String) {
    val existing = local.getByPath(songPath) ?: return
    engine.remove(songPath)
    coverStore.delete(songPath)
    lyricRepository.deleteCached(existing.lyricFileName)
    local.deleteByPath(songPath)
}
```

`remove()` is the same except it **does not** call `deleteCached`, so a completed download you delete still has lyrics when you play online.

Idle download icon (from `DownloadActionButton`):

```kotlin
IconButton(
    onClick = onClick,
    modifier = modifier.semantics { contentDescription = description },
) {
    when (status) {
        DownloadStatus.NONE -> Icon(Icons.Filled.Download, contentDescription = null)
        DownloadStatus.QUEUED, DownloadStatus.DOWNLOADING -> CircularProgressIndicator(/* … */)
        DownloadStatus.COMPLETED -> Icon(Icons.Filled.DownloadDone, contentDescription = null)
        DownloadStatus.FAILED -> Icon(Icons.Filled.Refresh, contentDescription = null)
    }
}
```
