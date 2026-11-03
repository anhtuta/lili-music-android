# 002 — AI-agent tooling and repo templates

## Goal

Make the repo easy for AI coding agents and humans to work in the same way: one source of instructions, Cursor/GitHub templates, formatting/static analysis, and CI gates.

## Approach

Follow the [AGENTS.md](https://agents.md/) convention used by Codex, Cursor, Copilot, and others:

- **Source of truth:** root `AGENTS.md` (overview, module map, commands, style, tests, mandatory workflow, Conventional Commits).
- **Pointers, not copies:** `CLAUDE.md` (`@AGENTS.md`), `GEMINI.md`, `.github/copilot-instructions.md`.
- **Cursor:** `.cursor/rules/*.mdc` with frontmatter — Kotlin/Compose, testing, architecture boundaries, commits/PRs, docs/task-plan workflow.
- **Task plans:** `docs/tasks/TEMPLATE.md`.
- **GitHub:** PR template checklist (task plan, tests, one-purpose commits) and issue templates (feature / bug / task).
- **Editor:** `.editorconfig`.
- **Static analysis:** ktlint-gradle + detekt, wired into `./gradlew check`.
- **CI:** extend the existing workflow with ktlint, detekt, and commitlint (Conventional Commits).
- **Dependabot:** Gradle and GitHub Actions, weekly.

Do not duplicate long policy in every file. Rules and templates should point at `AGENTS.md` / `docs/PLAN.md` / `docs/tasks/TEMPLATE.md`.

## Files

- `AGENTS.md`
- `CLAUDE.md`, `GEMINI.md`, `.github/copilot-instructions.md`
- `.cursor/rules/*.mdc`
- `docs/tasks/TEMPLATE.md`
- `.github/pull_request_template.md`
- `.github/ISSUE_TEMPLATE/{config.yml,feature.yml,bug.yml,task.yml}`
- `.editorconfig`
- `config/detekt/detekt.yml`
- ktlint/detekt plugins in the version catalog and root `build.gradle.kts`
- `.github/workflows/ci.yml` (extend)
- `commitlint.config.js` (or equivalent) for commit-message lint
- `.github/dependabot.yml`
- `README.md` (link `AGENTS.md`)

## Tests

No new app features. After wiring ktlint/detekt:

- `./gradlew ktlintCheck detekt lintDebug testDebugUnitTest` (or `./gradlew check`) must pass.
- Format existing Kotlin with `ktlintFormat` if the first check fails on style.

## Done when

- Agents reading any tool-specific file land on `AGENTS.md`.
- A new task can be started from `docs/tasks/TEMPLATE.md`.
- PRs have a checklist that references the task plan, tests, and single-purpose commits.
- CI runs build, unit tests, Android lint, ktlint, detekt, and commit-message lint.
- Dependabot is configured.
- `./gradlew check` passes.
