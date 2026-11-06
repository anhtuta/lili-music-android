# 013 — Debug APK launcher is MainActivity

## Goal

Installing the debug APK on Android 16+ (targetSdk 35) opens `MainActivity` and shows the library screen or its loading/error state. It must not open a blank white `ComponentActivity` with no crash.

## Approach

The debug overlay `app/src/debug/AndroidManifest.xml` registered `androidx.activity.ComponentActivity` with `MAIN`/`LAUNCHER` so Robolectric `createComposeRule()` could resolve it. Android merges that activity **before** `MainActivity`. On Android 16 the launcher starts the first matching activity, so the user got an empty Compose host (`VRI[ComponentActivity]`, Theme.Material.Light.NoActionBar, no `setContent`) and a white screen.

Fix:

- Delete the debug overlay so the shipped debug APK has exactly one `LAUNCHER`: `MainActivity`.
- Keep `debugImplementation(ui-test-manifest)` on `:app` so `ComponentActivity` still exists for Robolectric, **without** a launcher filter.
- Keep `ComponentActivity` + `LAUNCHER` only in `app/src/test/AndroidManifest.xml` (unit-test merge, not the APK).
- Move feature/`core:ui` `ui-test-manifest` from `debugImplementation` to `testImplementation` so library AARs do not contribute a second launcher into the app.
- App nav tests launch `ComponentActivity` by class (`createAndroidComposeRule`) instead of `MAIN`/`LAUNCHER`.
- `MainActivity` keeps the app theme; `LiliApp` draws a full-size Material 3 `Surface` so the first frame is not an empty white window. Library already shows loading/error instead of a blank catalog.

Non-goals: changing the Lumen API, Play signing, instrumented emulator CI (none in this environment).

## Files

- `docs/tasks/013-debug-launcher.md` (this file)
- `app/src/debug/AndroidManifest.xml` (delete)
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/anhtuta/lilimusic/LiliApp.kt`
- `app/src/test/AndroidManifest.xml`
- `app/src/test/java/com/anhtuta/lilimusic/LauncherManifestTest.kt`
- `app/src/test/java/com/anhtuta/lilimusic/LauncherActivityTest.kt`
- `app/src/test/java/com/anhtuta/lilimusic/FakePlaybackBindModule.kt`
- `app/src/test/java/com/anhtuta/lilimusic/navigation/LiliNavHostTest.kt`
- `app/build.gradle.kts`
- `core/ui/build.gradle.kts` and `feature/*/build.gradle.kts` (`ui-test-manifest` configuration)
- `docs/PLAN.md` (risk + this-run note)
- `docs/learning/10-debug-launcher.md` (own commit)

## Tests

- `LauncherManifestTest`: parse `processDebugMainManifest` XML; exactly one `LAUNCHER` activity, and it is `MainActivity`.
- `LauncherActivityTest` (Robolectric + Hilt): launch that launcher class and assert the library title or `LibraryTestTags.LOADING` is displayed.
- Existing `LibraryScreenTest` already covers loading/error UI.
- `LiliNavHostTest` still covers the library start destination without making `ComponentActivity` the APK launcher.

`testDebugUnitTest` depends on `processDebugMainManifest` so the merged APK manifest exists for the parser.

## Done when

- Debug merged manifest has exactly one `LAUNCHER`: `com.anhtuta.lilimusic.MainActivity`.
- Library loading/error is the first catalog frame, not a blank window.
- New tests plus `./gradlew check` pass.
- Learning page updated in its own commit.
