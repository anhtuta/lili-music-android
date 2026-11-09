# Phase 15 — Library scrollbar refinement

## What we built

The library scrollbar now reserves a 28.dp trailing strip and renders its thumb as a wide half-pill: the leading corners are rounded while the trailing edge stays flat at the screen edge. The drag mapping and the song-row overflow actions were left unchanged. A Compose test verifies that the thumb is visibly wider than the former narrow track.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `RoundedCornerShape` | Compose shape applied by `background` | CSS `border-radius` on a scrollbar thumb |
| `contentPadding.end` | Space kept clear at the end of a lazy list | Right padding reserving a fixed overlay rail |
| `assertWidthIsAtLeast` | Compose layout assertion | DOM test checking an element's `getBoundingClientRect().width` |

## Code to focus on

1. [LibraryLazyScrollbar.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt) — thumb shape, width, and drag target
2. [LibraryChromeInsets.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryChromeInsets.kt) — shared trailing scrollbar reservation
3. [LibraryScrollbarTest.kt](../../feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt) — Compose visibility and width coverage
