# 102 — Control row symmetry

## Goal

Speed chip and play-mode icon have the same touch target / similar visual weight. The Play button is exactly horizontally centered. Row stays: speed, previous, −5s, play, +5s, next, play mode. Tooltips stay.

## Approach

`SpaceEvenly` with a wide `FilterChip` on the left and a 48.dp icon on the right shifts Play off-center.

Use two `Row(Modifier.weight(1f))` groups around the 64.dp Play button so left and right get equal width. Put speed in a 48.dp `LiliIconButton` (compact `1.0×` label + tooltip) matching play-mode’s 48.dp target; keep play-mode as an icon button (icon can be slightly larger). `DropdownMenu` still opens from the speed control.

Non-goals: changing speed steps, play-mode cycle, or Play size (085).

## Files

- `feature/player/.../PlayerControls.kt`
- `feature/player/.../PlayerTestTags.kt`
- `feature/player/.../PlayerScreenPolishTest.kt`

## Tests

Left and right group tags have equal width. Play’s horizontal center matches the controls row’s center (small dp tolerance). Speed and play-mode still have contentDescriptions / tooltips. Order unchanged.

## Done when

- Play is centered; end controls match visually.
- Tests pass; `./gradlew check`.
