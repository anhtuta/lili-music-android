# 012 — Makefile for CLI APK builds

## Goal

The owner can install JDK 17, the Android command-line SDK, and then run `make apk` (no Android Studio) to get a debug APK under `dist/`.

## Approach

Root `Makefile` with a `.PHONY` list and a self-documenting `help` default target. Targets wrap the existing Gradle wrapper; they do not change app code, the Lumen API, or Play signing (064).

| Target | Behaviour |
| --- | --- |
| `help` | Default. Lists targets from `##` comments. |
| `doctor` | Fail with install hints unless `java` is 17+ and an SDK dir exists (`ANDROID_HOME`, `ANDROID_SDK_ROOT`, or `local.properties` `sdk.dir`). |
| `build` | `./gradlew :app:assembleDebug` |
| `apk` | `build`, copy to `dist/lili-music-<versionName>-debug.apk`, print the path |
| `release-apk` | `./gradlew :app:assembleRelease`, copy to `dist/`. If there is no `signingConfigs` block, say the APK is unsigned and signing is task **064**. |
| `install` | `adb install -r` the debug APK from `dist/` |
| `test` | `./gradlew testDebugUnitTest` |
| `lint` | `./gradlew lintDebug` |
| `check` | `./gradlew check` |
| `clean` | `./gradlew clean` and delete `dist/` |

`dist/` is gitignored. `versionName` is read from `app/build.gradle.kts`.

Non-goals: Android Studio, Play signing, changing `compileSdk` / `targetSdk`, new Gradle plugins.

## Files

- `docs/tasks/012-makefile.md` (this file)
- `Makefile`
- `.gitignore` (`dist/`)
- `scripts/test-makefile.sh`
- `.github/workflows/ci.yml` (cheap dry-run step)
- `docs/learning/05-how-to-run.md` (CLI-only setup; own commit)

## Tests

`scripts/test-makefile.sh` (no SDK, no assemble):

- `make` / `make help` lists every public target
- `make -n` for `doctor`, `build`, `apk`, `release-apk`, `install`, `test`, `lint`, `check`, `clean` prints the expected command (`assembleDebug`, `assembleRelease`, `adb install`, `testDebugUnitTest`, `lintDebug`, `check`, `clean`, `dist`)

Run that script in CI before `./gradlew check`.

## Done when

- Task plan committed before the Makefile.
- `make help` is the default target.
- `make doctor` checks Java 17+ and the SDK path.
- `make apk` / `make release-apk` copy into `dist/` and print the path.
- Unsigned release prints the 064 signing note.
- `scripts/test-makefile.sh` and `./gradlew check` pass.
- Learning page updated in its own commit.
