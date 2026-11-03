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

JDK 17 and Android SDK 35 are required.

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew check
```

API base URL is `BuildConfig.API_BASE_URL` in `:app`.

## Contributor workflow

Plan in `docs/` first, write `docs/tasks/NNN-*.md` and commit it before code, add tests for every change, and keep commits small and single-purpose. See [docs/PLAN.md](docs/PLAN.md).
