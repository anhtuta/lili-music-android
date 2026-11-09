# 121 — Library list vertical scrollbar

## Goal

A draggable scrollbar on the right of the library list for quick scroll up/down.

## Approach

`rememberLazyListState` + overlay thumb in `Box` beside `LazyColumn`, mapped from `layoutInfo`. Hide when list too short. JVM test for thumb math if extracted.

## Files

- `feature/library/LibraryScrollbar.kt` or `core/ui/...`
- `LibraryScreen.kt` wiring

## Tests

Unit test scrollbar fraction; Compose smoke that scrollbar testTag exists when list has many items.

## Done when

Thumb tracks scroll, tests pass, `./gradlew check`.
