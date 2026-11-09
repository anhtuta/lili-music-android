# Task 131 — Scrollbar drag visibility

## What we built

The scrollbar now tracks pointer-drag activity separately from lazy-list scrolling. Programmatic `scrollToItem` calls can briefly report the list as idle between drag updates, but the scrollbar remains visible until the pointer gesture ends; the normal idle delay then hides it.

## Terms (Java / web)

| Kotlin / Android | Meaning | Java / web analogy |
| --- | --- | --- |
| `onDragStart` / `onDragEnd` | Lifecycle callbacks for a pointer drag | `pointerdown` / `pointerup` handlers |
| `onDragCancel` | Cleanup when the gesture is interrupted | `pointercancel` handler |
| Separate Compose state | Keeps UI state for pointer and list independently | Two independent booleans in a component state object |

## Code to focus on

1. [LibraryLazyScrollbar.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt) — gesture-active visibility state
2. [LibraryScrollbarTest.kt](../../feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt) — visibility during a scrollbar drag and after release
