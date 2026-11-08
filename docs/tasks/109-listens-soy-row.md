# 109 — Listens and soy on one SpaceBetween row

## Goal

Listens sit on the left of one meta row; the soy badge sits on the right (`Arrangement.SpaceBetween`). No soy → nothing on the right. Listens use a small Material icon (Headphones or PlayArrow) with tooltip “Listens”. Soy badge style is unchanged.

## Approach

`Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween)`: left cluster is `TooltipBox` + Headphones + count; right is the existing soy chip or empty. Icon is decorative; tooltip/contentDescription is “Listens”.

Non-goals: changing soy colours; showing type/date.

## Files

- `feature/library/.../LibrarySongRow.kt`
- strings + tests

## Tests

Listens tag is displayed; Headphones has contentDescription/tooltip “Listens”. Soy appears on the same row when present and is absent when null. Listens x is left of soy x when both exist.

## Done when

- One meta line: listens start, soy end.
- Tests pass; `./gradlew check`.
