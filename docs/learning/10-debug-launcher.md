# Task 013 — Debug APK launcher blank screen

[Learning guide index](README.md) · Task [013](../tasks/013-debug-launcher.md)

**What was built.** The debug APK used to open a blank white window on Android 16+ because `ComponentActivity` was registered as a second `LAUNCHER` activity. The overlay is gone. The home screen now starts `MainActivity`, which calls `setContent { LiliApp() }`. The library still shows a spinner or error instead of an empty catalog while the first songs load.

**Status:** This PR.

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| Merged manifest | AGP combines app + library + dependency manifests | webpack merging several `web.xml`s |
| `MAIN` / `LAUNCHER` | The activity the home screen starts | `<a href="/">` for the installed app |
| `androidx.activity.ComponentActivity` | Empty Activity Compose tests host | A blank `index.html` with no React root |
| `ui-test-manifest` | Debug dependency that registers that empty Activity | A test-only route that must not be `/` |
| `createComposeRule()` | Starts `ComponentActivity` with `MAIN`/`LAUNCHER` | Mounts a test host that needs a matching route |
| `createAndroidComposeRule<T>()` | Starts `T` by class name, no launcher filter | `new T()` instead of hitting `/` |
| `ActivityScenario.launch(Class)` | Starts that Activity with `FLAG_ACTIVITY_NEW_TASK` | `new MainActivity()` in the test JVM, not resolving `/` through the unit-test manifest |
| `TestExceptionFormat.FULL` | Gradle prints the exception **message** and cause chain | JUnit’s short log is only `RuntimeException at File.kt:59` — the missing method name is in `e.getMessage()` |
| `processDebugMainManifest` | Writes the **APK** merged manifest | The production nginx config, not the test one |
| `VRI[ComponentActivity]` | Logcat name of the drawn window | The document title of whatever `/` resolved to |

Android 16 starts the **first** matching `LAUNCHER` activity. Merge order put Compose’s empty `ComponentActivity` ahead of `MainActivity`, so the phone never called `MainActivity.setContent`. No crash: the Activity existed, it just drew nothing.

`ui-test-manifest` itself is fine in the debug APK (the Activity has no launcher filter). The bug was `app/src/debug/AndroidManifest.xml` adding `MAIN`/`LAUNCHER` so Robolectric tests could resolve it.

## Code to focus on

1. [app/src/main/AndroidManifest.xml](../../app/src/main/AndroidManifest.xml) — the only `LAUNCHER` is `.MainActivity`.
2. [MainActivity.kt](../../app/src/main/java/com/anhtuta/lilimusic/MainActivity.kt) — `setContent { LiliApp() }`.
3. [LiliApp.kt](../../app/src/main/java/com/anhtuta/lilimusic/LiliApp.kt) — full-size Material `Surface` so the first frame is not an empty window.
4. [LibraryScreen.kt](../../feature/library/src/main/java/com/anhtuta/lilimusic/feature/library/LibraryScreen.kt) — `isLoading` → spinner; offline empty → retry.
5. [LauncherManifestTest.kt](../../app/src/test/java/com/anhtuta/lilimusic/LauncherManifestTest.kt) — parses `processDebugMainManifest`, not the unit-test merge.
6. [LauncherActivityTest.kt](../../app/src/test/java/com/anhtuta/lilimusic/LauncherActivityTest.kt) — asserts the APK launcher name, then `ActivityScenario.launch(MainActivity::class.java)` (adds `FLAG_ACTIVITY_NEW_TASK`; uses the Hilt-transformed class). Do **not** build a `MAIN`/`LAUNCHER` Intent via `Class.forName` — that path threw `NoSuchMethodException` on CI.

Merged debug APK (only `MainActivity` has `LAUNCHER`; `ComponentActivity` is test-host only):

```xml
<activity
    android:name="com.anhtuta.lilimusic.MainActivity"
    android:exported="true"
    android:theme="@style/Theme.LiliMusic">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity
    android:name="androidx.activity.ComponentActivity"
    android:exported="true"
    android:theme="@android:style/Theme.Material.Light.NoActionBar" />
```
