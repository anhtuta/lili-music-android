# 128 — Library scrollbar half-pill thumb

## Goal

Make the library scrollbar thumb wider and visually match the supplied reference with a large half-pill shape at the trailing edge of the list.

## Approach

Increase the library scrollbar thumb width and apply a shape with fully rounded leading corners and a flat trailing edge, allowing the thumb to extend to the screen edge while retaining the existing draggable hit target and position mapping. Leave the song-row overflow buttons unchanged.

## Files

- `feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryLazyScrollbar.kt`
- `feature/library/src/test/java/com/anhtuta/lilimusic/feature/library/LibraryScrollbarTest.kt`
- `docs/PLAN.md`

## Tests

Keep the long-catalog Compose smoke test and add a semantics/layout assertion that the scrollbar thumb is wider than the reserved track.

## Done when

- The library thumb is visibly wider and has the requested half-pill silhouette.
- Dragging the thumb still scrolls the catalog.
- Existing three-dot song actions remain unchanged.
- Tests and `./gradlew check` pass.
