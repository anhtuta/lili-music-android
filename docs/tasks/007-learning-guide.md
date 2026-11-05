# 007 — Owner learning guide (docs)

> Copy this file to `docs/tasks/NNN-short-name.md`, fill every section, and **commit it before writing code**. See [AGENTS.md](../../AGENTS.md) and [docs/PLAN.md](../PLAN.md).

## Goal

A Java/React-experienced owner who is new to Android and Kotlin can read a GitHub-flavoured Markdown guide under `docs/learning/` that explains each merged phase: what was built, terms with Java/web analogies, and the files/code to read. Future phases must append the same structure.

## Approach

Docs only. Convert the existing interactive learning-guide content into Markdown (split pages plus an index). Verify every file path and excerpt against current `main`; fix mismatches, do not invent APIs or files. Relative links for in-repo paths; GitHub PR URLs stay as-is. Diagrams as `mermaid` blocks.

Add a short AGENTS.md (and docs-workflow Cursor rule) requirement: every later phase/task appends a learning-guide section — what was built, new terms with Java/web analogies, code to focus on — in its own commit.

Non-goals: production code, new tests, backend or API changes, rewriting PLAN.md scope.

## Files

- `docs/tasks/007-learning-guide.md` (this plan; committed first, alone)
- `docs/learning/README.md` (index, how to use, phase status)
- `docs/learning/00-overview.md`
- `docs/learning/01-project-init.md`
- `docs/learning/02-kotlin-bump.md`
- `docs/learning/03-data-and-library.md`
- `docs/learning/04-playback.md`
- `docs/learning/05-how-to-run.md`
- `AGENTS.md` (append-after-phase rule)
- `.cursor/rules/docs-workflow.mdc` (same rule for agents)
- `README.md` (one-line pointer to the guide)

## Tests

No production behaviour. No new tests. `./gradlew check` must still pass. Commit messages must pass commitlint (Conventional Commits).

## Done when

- Task plan committed before any guide files.
- Guide pages exist, paths/excerpts match current `main`, diagrams render as mermaid.
- AGENTS.md and the docs-workflow rule require a learning-guide append after each future phase/task, in its own commit.
- Commits are small, single-purpose, and Conventional Commits.
- `./gradlew check` passes.
- PR uses `.github/pull_request_template.md` and links this task.
