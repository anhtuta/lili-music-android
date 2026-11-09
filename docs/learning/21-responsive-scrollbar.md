# Task 130 — Responsive and stable scrollbar

## What we built

The library scrollbar now has a 48.dp invisible interaction rail around the compact visible thumb. Tapping or dragging anywhere on that rail maps directly to a catalog fraction, including an exact jump to the final item at the bottom. The thumb uses a fixed 10% height fraction and explicit top/end clamping, so its shape no longer changes as visible rows enter and leave the viewport.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `detectTapGestures` | Handles a tap location in Compose | A click listener receiving pointer coordinates |
| `detectDragGestures` | Handles continuous pointer movement | Pointer/mouse move events during a drag |
| `LazyListLayoutInfo` | Current virtualized list geometry | A virtual table's visible range and viewport metadata |

## Code to focus on

1. [LibraryLazyScrollbar.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt) — invisible rail, tap/drag mapping, and thumb rendering
2. [LazyListScrollbarMath.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LazyListScrollbarMath.kt) — stable thumb geometry and target-index mapping
3. [LazyListScrollbarMathTest.kt](../../feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LazyListScrollbarMathTest.kt) — stable height and bottom-target coverage
