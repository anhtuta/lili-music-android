# How to run it

[Learning guide index](README.md)

JDK 17 + Android SDK. Open the repo root in Android Studio, sync Gradle, run `:app` on an API 26+ emulator or device.

1. **Open project** — Open the folder that contains [settings.gradle.kts](../../settings.gradle.kts).
2. **Sync Gradle** — First sync downloads SDK pieces and deps.
3. **Run** — AVD API 26+ or USB device → Run. Library hits the live API.
4. **Same gate as CI** — `./gradlew check`

| Command | What it does |
| --- | --- |
| `./gradlew assembleDebug` | Debug APK |
| `./gradlew testDebugUnitTest` | JVM unit tests |
| `./gradlew lintDebug` | Android Lint |
| `./gradlew ktlintCheck` | Kotlin style |
| `./gradlew detekt` | Static analysis |
| `./gradlew check` | All of the above (CI) |

After playback: tap a song → mini-player; try shuffle / repeat-one / speed. Lock-screen and Bluetooth need a real device.

---

Next append after the following merge: Phase 4 lyrics (`.lrc` / `.xlrc` / `.trc`). Same structure — built, terms, files. See [docs/PLAN.md](../PLAN.md) Phase 4 and [After a later phase](README.md#after-a-later-phase).
