# AGENTS.md

Instructions for AI coding agents and humans working in this repository. Tool-specific files (`CLAUDE.md`, `GEMINI.md`, `.github/copilot-instructions.md`) point here instead of duplicating policy.

## Project

Lili Music is a native Android **listener-only** client for the existing Lumen catalog API. Backend `https://blog.nenmongvietnam.com/laravel/public` must stay unchanged. v1: Play Store, no admin/login, no waveform/`RECORD_AUDIO`, play only songs with a non-empty `file_name`, hide Zing-only tracks.

Full scope, API contract, phases, and risks: [docs/PLAN.md](docs/PLAN.md). Owner-oriented Android/Kotlin notes (Java/web analogies): [docs/learning/README.md](docs/learning/README.md).

Package: `com.anhtuta.lilimusic`. minSdk 26, compileSdk 37, targetSdk 35, JDK 17.

## Architecture / modules

```
:app                          Application, Hilt, NavHost, deep links
:core:common                  Dispatchers, result types (JVM)
:core:model                   Song, lyric, settings models (JVM)
:core:network                 Retrofit + OkHttp
:core:database                Room
:core:datastore               Preferences
:core:data                    Repositories
:core:playback                Media3 player, session, downloads
:core:lyrics                  LRC / XLRC / TRC parser (JVM)
:core:ui                      Theme and shared Compose UI
:feature:library              Song list, sort, search
:feature:player               Now playing
:feature:lyrics               Synced lyric UI
:feature:settings             Settings
:feature:downloads            Offline downloads
```

Rules:

- `feature:*` may depend on `core:*`. `core:*` must not depend on `feature:*` or `:app`.
- `core:playback` and `core:lyrics` stay UI-free.
- Inject with Hilt. Do not construct Retrofit/Room/ExoPlayer in Compose or ViewModels.
- New screens go in the matching `feature` module, not `:app`.

## Commands

Run from the repo root. JDK 17.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest          # JVM unit tests
./gradlew lintDebug
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew detekt
./gradlew check                      # tests + lint + ktlint + detekt
```

One test class:

```bash
./gradlew :app:testDebugUnitTest --tests com.anhtuta.lilimusic.BuildConfigTest
```

Do not start the Gradle daemon in CI (`--no-daemon` is already in the workflow). Locally the daemon is fine.

## Mandatory workflow

1. If scope changes, update `docs/PLAN.md`.
2. **Before writing code**, copy [docs/tasks/TEMPLATE.md](docs/tasks/TEMPLATE.md) to `docs/tasks/NNN-short-name.md`, fill it in, and **commit that file alone**.
3. Implement only that task. Every production change has tests.
4. Commits are small and do one thing. Use [Conventional Commits](https://www.conventionalcommits.org/):

   `feat|fix|docs|test|chore|refactor|ci|style(scope): summary`

   Examples: `feat(lyrics): parse xlrc translations`, `test(playback): shuffle keeps current song first`.
5. Finish only when `./gradlew check` passes.
6. After every phase/task, append a section to the [learning guide](docs/learning/README.md): what was built, new Android/Kotlin terms with Java/web analogies, and code to focus on. Commit that append **alone**.

## Code style

- Kotlin official style, Android Kotlin style via ktlint (`android` mode).
- Jetpack Compose + Material 3. Prefer immutable UI state (`data class` + `StateFlow`).
- No wildcard imports. Trailing commas in multi-line parameter lists.
- User-facing strings in `strings.xml` (or a feature `res/values/strings.xml`).
- URL-encode `file` / `path` query params. Resolve relative `image_url` with the API base URL.
- Do not add `RECORD_AUDIO`. Do not call Zing streaming endpoints.

## Testing

- JVM unit tests first (parser, repository, ViewModel, shuffle, URL helpers).
- MockWebServer for HTTP. In-memory Room for DAOs.
- Compose UI tests for interactions, not appearance-only screenshots.
- Robolectric only when Android framework types are required.
- A change without tests is not done.

## Safety

- Never commit secrets, keystores, or `local.properties`.
- Never change the Lumen backend or assume new API fields without verifying live JSON.
- Do not implement admin or login in v1.
