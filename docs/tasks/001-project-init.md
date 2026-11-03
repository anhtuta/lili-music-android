# 001 — Project initialization

## Goal

Scaffold a buildable Kotlin Android app so later feature tasks have a place to land. No player features yet.

## Approach

- Gradle wrapper 8.10.2 + Android Gradle Plugin 8.8.2 + Kotlin 2.1.10 (JDK 17 bytecode, host JDK 17+).
- Version catalog at `gradle/libs.versions.toml` for all plugins and libraries listed in `docs/PLAN.md`.
- Modules from the plan: `:app` plus empty `:core:*` and `:feature:*` stubs that compile.
- Hilt on `:app` only (`@HiltAndroidApp` + `@AndroidEntryPoint` activity).
- API base URL as `BuildConfig.API_BASE_URL` (`https://blog.nenmongvietnam.com/laravel/public`).
- Android `.gitignore`, human README, GitHub Actions: assemble, unit tests, Android Lint.
- One trivial JVM unit test so the test task is proven.

minSdk 26, compileSdk/targetSdk 35, namespace `com.nenmongvietnam.lilimusic`.

## Files

- `gradlew`, `gradlew.bat`, `gradle/wrapper/*`
- `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/libs.versions.toml`
- `app/` application module
- `core/{common,model,network,database,datastore,data,playback,lyrics,ui}/`
- `feature/{library,player,lyrics,settings,downloads}/`
- `.gitignore`, `README.md`, `.github/workflows/ci.yml`
- `local.properties` is generated locally only (never committed)

## Tests

`app/src/test` smoke test that asserts a stable constant (application id or `2+2`). `./gradlew testDebugUnitTest` and `./gradlew lintDebug` must pass.

## Done when

- `./gradlew build` and `./gradlew test` succeed locally.
- CI workflow runs build + unit tests + lint on PRs.
- `:app` has Hilt + `BuildConfig.API_BASE_URL`.
- Empty module stubs are included in the Gradle build.
- No playback, network, or UI features beyond a blank Compose `MainActivity`.
