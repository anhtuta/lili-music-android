# How to run it

[Learning guide index](README.md)

A **JDK 21 or newer** (25 is fine) and an Android SDK. You do **not** need Android Studio. The root [Makefile](../../Makefile) wraps Gradle so you can build and copy an APK from the CLI (`make apk`). Studio still works if you prefer it.

App bytecode stays **JVM 17** (`kotlin.jvm.target=17`, `compileOptions` 17). Gradle runs on whatever JDK launched it; it does **not** download another JDK.

Task [012](../tasks/012-makefile.md).

## CLI-only setup (no Android Studio)

`compileSdk` is **37**, `targetSdk` **35**, `minSdk` **26**.

### 1. JDK 21 or newer

Gradle 9.6 / Kotlin 2.4 / AGP 9.5 run on **JDK 21+**. Oracle 25, Temurin 21, or any other 21+ vendor is fine. Java 17 is not. Bytecode stays 17 regardless of the JDK that compiles.

```bash
# Already have JDK 25 (or 21+) on PATH — use it
java -version
# java version "25.0.1"  ...
export JAVA_HOME="/path/to/jdk-25.0.1.jdk/Contents/Home"   # macOS bundle
export PATH="$JAVA_HOME/bin:$PATH"

# Only if you have no 21+ JDK
# macOS:  brew install --cask temurin@21
# Debian: sudo apt install openjdk-21-jdk
```

### 2. Android command-line tools

1. Download **Command line tools only** from [Android Studio downloads](https://developer.android.com/studio#command-line-tools-only).
2. Unpack into `cmdline-tools/latest` under your SDK root (example `$HOME/Android/Sdk`):

```bash
mkdir -p "$HOME/Android/Sdk/cmdline-tools"
unzip commandlinetools-*.zip -d "$HOME/Android/Sdk/cmdline-tools"
mv "$HOME/Android/Sdk/cmdline-tools/cmdline-tools" "$HOME/Android/Sdk/cmdline-tools/latest"
```

3. Export the SDK and put `sdkmanager` / `adb` on `PATH`:

```bash
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
```

`ANDROID_SDK_ROOT` is the older name for the same directory; `make doctor` accepts either.

### 3. sdkmanager packages

Match `compileSdk` **37** in [app/build.gradle.kts](../../app/build.gradle.kts). There is **no** `platforms;android-37` package. API 37 is published as `platforms;android-37.0` (also 37.1 / 37.2) and installs into `platforms/android-37.0`. AGP `compileSdk = 37` resolves that folder (it uses `android-37.0/android.jar`). Install Build-Tools 37.0.0 for AGP 9.5, not 35.0.0:

```bash
sdkmanager "platforms;android-37.0" "build-tools;37.0.0" "platform-tools"
yes | sdkmanager --licenses
```

`sdkmanager` may print a deprecation warning pointing at the new `android sdk` CLI. That warning is harmless; the packages still install.

`make doctor` accepts an installed folder named `android-37`, `android-37.0`, or `android-37.*`.

### 4. Point Gradle at the SDK

Either keep `ANDROID_HOME` in the environment, or write it once for Gradle:

```
sdk.dir=/absolute/path/to/Android/Sdk
```

in `local.properties` at the repo root (**gitignored**). `make doctor` reads that file too.

Then:

```bash
make doctor    # JDK 21+ (prints the version) and SDK / platforms/android-37.0
make apk       # dist/lili-music-<versionName>-debug.apk
```

`make doctor` prints install hints if something is missing.

### Make targets

| Target | What it does |
| --- | --- |
| `make` / `make help` | List targets (default) |
| `make doctor` | Check JDK 21+ and the SDK |
| `make build` | `./gradlew :app:assembleDebug` |
| `make apk` | Debug APK → `dist/lili-music-<versionName>-debug.apk` (prints the path) |
| `make release-apk` | Release APK → `dist/`. Unsigned until Play signing in **064** |
| `make install` | `adb install -r` the debug APK (device or emulator) |
| `make test` | `./gradlew testDebugUnitTest` |
| `make lint` | `./gradlew lintDebug` |
| `make check` | Same gate as CI |
| `make clean` | Gradle `clean` and delete `dist/` |

`dist/` is gitignored. `versionName` comes from `app/build.gradle.kts` (currently `0.1.0`).

## Android Studio (optional)

1. **Open project** — Open the folder that contains [settings.gradle.kts](../../settings.gradle.kts).
2. **Sync Gradle** — First sync downloads SDK pieces and deps.
3. **Run** — AVD API 26+ or USB device → Run. Library hits the live API.
4. **Same gate as CI** — `./gradlew check` or `make check`

## Gradle commands (same as Make)

| Command | What it does |
| --- | --- |
| `./gradlew assembleDebug` | Debug APK |
| `./gradlew testDebugUnitTest` | JVM unit tests |
| `./gradlew lintDebug` | Android Lint |
| `./gradlew ktlintCheck` | Kotlin style |
| `./gradlew detekt` | Static analysis |
| `./gradlew check` | All of the above (CI) |

API base URL is `BuildConfig.API_BASE_URL` in `:app`.

After playback: tap a song → mini-player; try shuffle / repeat-one / speed. Lock-screen and Bluetooth need a real device.

---

After playback: tap a song → full player; lyrics should highlight with the seek bar. Try ±100ms if a line feels early/late.

After visuals: Library / Player → Settings. Toggle lyric theme, translation, app dark mode; cover should tint now-playing.

Next append after the following merge: Phase 6 offline / share / deep links / store. Same structure — built, terms, files. See [docs/PLAN.md](../PLAN.md) Phase 6 and [After a later phase](README.md#after-a-later-phase).
