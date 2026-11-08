# 111 — LauncherActivityTest must survive CI ActivityScenario.launch

## Goal

PR CI `./gradlew check` launches the merged-manifest launcher Activity in `LauncherActivityTest` without `NoSuchMethodException`. The library title or `LibraryTestTags.LOADING` still appears.

## Approach

CI on `e0de547` failed `:app:testDebugUnitTest` with `RuntimeException` / `NoSuchMethodException` at `ActivityScenario.launch` (line 59). Local JDK 21 reruns pass. Gradle’s default short test log omits the exception message, so the missing method name is not in the job log.

- Keep asserting the debug APK merged manifest launcher is `MainActivity`.
- Launch that class with `ActivityScenario.launch(MainActivity::class.java)` so the Intent includes `FLAG_ACTIVITY_NEW_TASK` and uses the Hilt-transformed class from the test classloader (avoid `Class.forName` + MAIN/LAUNCHER without `NEW_TASK`).
- If launch still throws, rethrow `AssertionError` with the cause chain (class + message) so CI logs name the missing method.
- Print full unit-test exception traces on failure (`TestExceptionFormat.FULL`) so a later CI miss is diagnosable.

Non-goals: changing Library search/sort UI, the Lumen API, or the debug APK launcher (still task 013).

## Files

- `docs/tasks/111-launcher-activity-test-ci.md` (this file)
- `app/src/test/java/com/anhtuta/lilimusic/LauncherActivityTest.kt`
- `build.gradle.kts` (unit-test logging)
- `docs/PLAN.md` (task 111 row)

## Tests

- `LauncherActivityTest.launchingMergedManifestShowsLibraryOrLoading` still asserts MainActivity + Library title or loading tag.
- Re-run that class under `:app:testDebugUnitTest`.

## Done when

- Launch uses `MainActivity::class.java` after the manifest assertion.
- Failed unit tests print full stacks.
- `./gradlew :app:testDebugUnitTest --tests com.anhtuta.lilimusic.LauncherActivityTest` and `./gradlew check` pass.
