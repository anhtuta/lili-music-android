# Examples — lili-feature-phase

## User request (paraphrased)

“New phase: move download/lyric into settings menu, full-width lyrics, bottom padding, 5 font sizes, fix listen count after 60s. One commit per task, docs, learning.”

## Planning commit

```
docs(plan): add Phase 12 tasks 112-117
```

Files: `docs/PLAN.md`, `docs/tasks/112-*.md` … `117-*.md` (no `116` gap documented in PLAN).

## Implementation commits (order)

| Task | Commit     | Message (short) |
| --- | --- | --- |
| 117 | `c92f80f` | `fix(playback): schedule listen count when start position is slightly past zero` |
| 115 | `aa26d4c` | `feat(settings): add five persisted lyric font size presets` |
| 112 | `9db2429` | `feat(player): tuck download and lyric card into overflow menu` |
| 113 | `62cd06a` | `feat(player): show lyrics edge-to-edge without rounded pane clip` |
| 114 | `c5aa446` | `feat(player): add bottom inset on now-playing controls` |
| learn | `0890562` | `docs(learning): add Phase 12 now-playing refinement notes` |

Note: **117 before 112** in implementation order is fine when tasks are independent; still **one commit each**, **commit before the next**.

## Splitting `PlayerScreen.kt` across 112 → 113 → 114

1. After 112 commit: app bar uses `NowPlayingOverflowMenu`; body still has old pane clip/padding.
2. After 113 commit: remove `LyricPaneShape`, drop rounded clip, edge-to-edge lyric column.
3. After 114 commit: add `PlayerChromeFooter` + `navigationBarsPadding()` + `PlayerControlsInsets`.

## Task plan skeleton

```markdown
# 112 — Now Playing app-bar overflow menu

## Goal
…

## Approach
…

## Files
…

## Tests
…

## Done when
- [ ] …
- [ ] `./gradlew check`
```

## Learning section skeleton

```markdown
# Phase 12 — …

## What we built
1. …

## Terms (Java / web)
| Kotlin | Meaning | Analogy |
| --- | --- | --- |

## Code to focus on
1. [File.kt](path)
```
