---
name: lili-feature-phase
description: >-
  Implements new Lili Music Android features as numbered phases/tasks with
  docs-first workflow, one Conventional Commit per task, and a commit before
  starting the next task. Use when the user asks for a new feature, new phase,
  multiple tasks, or says to follow project task workflow, AGENTS.md phases, or
  one commit per task.
---

# Lili Music — feature / phase implementation

Read [AGENTS.md](../../AGENTS.md) and [docs/PLAN.md](../../docs/PLAN.md) first. Cursor rules under `.cursor/rules/` restate the same policies.

## Non-negotiables (top priority)

1. **One commit = one task** (one logical change). Never mix unrelated work.
2. **Commit after each task finishes** (code + tests). **Do not start the next task** until that commit succeeded and the working tree is clean for the next slice.
3. **Docs before code for every task ID**: `docs/tasks/NNN-short-name.md` from [docs/tasks/TEMPLATE.md](../../docs/tasks/TEMPLATE.md) must exist and be committed **before** implementation commits for that NNN (task plan file alone, or phase batch per below).
4. **Every production change has tests**; task is not done until `./gradlew check` passes (or the affected modules’ tests if check is blocked by environment—then say so).
5. **Learning**: after the phase (or last task in the run), append `docs/learning/` in **its own commit**.

Do **not** push or open a PR unless the user asks.

## When to use this skill

- User describes a **new feature**, **phase**, or **list of improvements** (like Phase 12).
- User wants **stable task numbers** (NNN) and PLAN.md updated.
- User says **one commit per task** or **follow project format**.

## Phase kickoff (once per phase)

1. **Inventory** next free task IDs in `docs/PLAN.md` §6 and existing `docs/tasks/`.
2. **Split** the user request into one task per commit (group only if the user explicitly allows).
3. **Update `docs/PLAN.md`**: new phase table (IDs, one-line summary, links to task plans, status).
4. **Write each** `docs/tasks/NNN-*.md` from the template (Goal, Approach, Files, Tests, Done when).
5. **Commit plan docs**:
   - Preferred for a multi-task phase: one `docs(plan): add Phase N tasks NNN–MMM` commit with PLAN + all new task files **only** (no code).
   - Alternative: strictly one task file per commit if the user insists on minimal plan commits.

Then implement tasks **in ID order** (unless PLAN notes a dependency).

## Per-task loop (repeat until phase done)

Use this checklist every time:

```
Task NNN:
- [ ] Task plan already on main / committed alone
- [ ] Implement only NNN scope (right modules per architecture rules)
- [ ] Add/update tests
- [ ] ./gradlew check (or targeted modules)
- [ ] git commit — single Conventional Commit
- [ ] git status clean before NNN+1
```

### Implementation rules

- **Modules**: `feature:*` → `core:*` only; no UI in `core:playback` / `core:lyrics`; Hilt for wiring. See `.cursor/rules/architecture.mdc`.
- **Style**: ktlint Android mode, strings in `strings.xml`, no wildcard imports. See `.cursor/rules/kotlin-compose.mdc`.
- **Tests**: JVM first; Compose UI for interactions. See `.cursor/rules/testing.mdc`.
- **Scope**: smallest correct diff; match surrounding patterns; no drive-by refactors.

### Commit messages

Format: `type(scope): summary` — types `feat|fix|docs|test|chore|refactor|ci|style`.

Examples from this repo:

- `docs(plan): add Phase 12 tasks 112-117`
- `feat(player): tuck download and lyric card into overflow menu`
- `fix(playback): schedule listen count when start position is slightly past zero`
- `docs(learning): add Phase 12 now-playing refinement notes`

Use a HEREDOC for the commit body when needed.

### Same file touched by multiple tasks

If one file (e.g. `PlayerScreen.kt`) spans tasks:

1. Implement task A, commit **only** A’s hunks (restore file between steps if needed).
2. Build task B on top of A’s commit, commit B alone.
3. Do not land A+B in one commit unless the user collapses tasks to a single NNN.

Prefer **independent files per task** when planning to avoid painful splits.

## After the last task in the phase

1. Add or extend `docs/learning/NN-*.md`: what was built, terms (with Java/web analogies), files to read.
2. Link it from [docs/learning/README.md](../../docs/learning/README.md) and tick/update status if applicable.
3. Commit: `docs(learning): …` **alone**.
4. Optionally update PLAN.md task status to Done / PR reference (in a small `docs(plan):` commit if not done earlier).

## Verify before telling the user “done”

```bash
./gradlew check
```

From repo root, JDK 21+. If detekt fails on JDK 25 only, note it and show module tests that passed.

## Anti-patterns

- One giant commit for a whole phase.
- Code in the same commit as the first task plan for that NNN.
- Skipping tests “for later.”
- Starting task 113 while 112 is still uncommitted.
- Mixing learning guide with a feature commit.

## Reference

- Phase 12 example (overflow menu, full-width lyrics, insets, font sizes, listen fix): commits `c60e9d4` → `0890562` on branch `tuta`.
- More detail: [examples.md](examples.md).
