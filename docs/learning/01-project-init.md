# PR #1 — Project init + agent tooling

[Learning guide index](README.md) · [PR #1](https://github.com/anhtuta/lili-music-android/pull/1)

**What was built.** Multi-module shell, version catalog, Hilt Application, CI (commitlint + `./gradlew check`), `AGENTS.md`, Cursor rules, PR/issue templates, ktlint/detekt. Stubs only — no real library UI. Tasks [001](../tasks/001-project-init.md) and [002](../tasks/002-agent-tooling.md).

**Status:** Merged.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| Gradle | Build tool + `libs.versions.toml` | Maven / npm lockfile |
| Kotlin | App language | Modern Java (null-safety, coroutines) |
| `applicationId` | `com.anhtuta.lilimusic` on Play Store | Installed-app coordinates |
| minSdk / targetSdk / compileSdk | 26 / 35 / 37 | engines + browser targets |
| AndroidManifest | App, activities, permissions, services | web.xml / app config |
| Hilt | `@HiltAndroidApp` dependency injection | Spring / Guice DI |
| KSP | Generates Hilt/Room code | Annotation processors |
| BuildConfig | `API_BASE_URL` at build time | `.env` baked in |
| Compose | Declarative UI toolkit | React components |
| ktlint / detekt | Style + static analysis in CI | ESLint / Checkstyle |

`compileSdk` 37 is the current pin (needed later for core-ktx 1.19.1 in [PR #7](02-kotlin-bump.md)). Task 001 started at compileSdk 35.

## Code to focus on

1. [app/build.gradle.kts](../../app/build.gradle.kts) — `applicationId`, SDKs, `BuildConfig.API_BASE_URL`.
2. [gradle/libs.versions.toml](../../gradle/libs.versions.toml) — all library versions.
3. [LiliMusicApplication.kt](../../app/src/main/java/com/anhtuta/lilimusic/LiliMusicApplication.kt) — `@HiltAndroidApp` (like `@SpringBootApplication`).
4. [AndroidManifest.xml](../../app/src/main/AndroidManifest.xml) — Application + launcher Activity.
5. [AGENTS.md](../../AGENTS.md) — plan first, tests always, small Conventional Commits.
6. [ci.yml](../../.github/workflows/ci.yml) — commitlint + `./gradlew check`.

From `LiliMusicApplication.kt` (WorkManager `Configuration.Provider` was added in [PR #15](04-playback.md)):

```kotlin
@HiltAndroidApp
class LiliMusicApplication :
    Application(),
    Configuration.Provider {
```
