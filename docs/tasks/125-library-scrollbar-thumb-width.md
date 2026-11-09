# 125 — Thicker library scrollbar thumb

## Goal

Scrollbar thumb is wide enough to grab and drag reliably.

## Approach

Increase visible thumb width and widen the drag hit target (thumb stays aligned to track).

## Files

- `feature/library/LibraryLazyScrollbar.kt`
- `LibraryChromeInsets` scrollbar constants if shared

## Tests

Scrollbar test tag still present; optional assert on minimum thumb width via semantics/layout if feasible.

## Done when

Thumb visibly thicker, drag still scrolls list, tests pass.
