# Lili Music (Android)

Native Android listener for the [Lili Player](https://github.com/anhtuta/lili-player) catalog. Songs come from the existing Lumen API at `https://blog.nenmongvietnam.com/laravel/public`. The backend is not modified by this app.

v1 is Play Store–ready, **listener only** (no admin, no login). Zing-streamed tracks are hidden; only songs with a `file_name` are playable. Offline downloads, synced lyrics, and lock-screen controls are in scope. See [docs/PLAN.md](docs/PLAN.md).

## Stack

Kotlin, Jetpack Compose + Material 3, Navigation Compose, Hilt, Media3, Retrofit + OkHttp + kotlinx.serialization, Room, DataStore, Coil, Palette, WorkManager. minSdk 26.

## Modules

```
:app
:core:{common,model,network,database,datastore,data,playback,lyrics,ui}
:feature:{library,player,lyrics,settings,downloads}
```

## Build

JDK **21 or newer** (25 is fine) and Android SDK (`compileSdk` 37, `targetSdk` 35). App bytecode stays JVM 17. Prefer the Makefile if you are not using Android Studio: `make doctor`, `make apk`, `make bundle`. Release signing is optional and never committed; see [docs/play/signing.md](docs/play/signing.md).

```bash
./gradlew assembleDebug
./gradlew :app:assembleRelease   # minified; unsigned unless keystore.properties / LILI_* is set
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew ktlintCheck
./gradlew detekt
./gradlew check
```

API base URL is `BuildConfig.API_BASE_URL` in `:app`. Version comes from [version.properties](version.properties) (`VERSION_CODE` / `VERSION_NAME` env overrides).

## Contributor workflow

Read [AGENTS.md](AGENTS.md). Plan in `docs/` first, write `docs/tasks/NNN-*.md` and commit it before code, add tests for every change, and keep commits small and single-purpose (Conventional Commits). See [docs/PLAN.md](docs/PLAN.md). Java/React → Android/Kotlin walkthrough of merged phases: [docs/learning/README.md](docs/learning/README.md).
