# 065 — Release smoke test

## Goal

A `make smoke` target proves the **minified** release APK assembles and its package / permissions / launcher are sane (no emulator). A written checklist tells the owner how to exercise playable-only library, notification controls, offline, lyrics, lyric card, and deep links on an **arm64 macOS emulator** via Make.

## Approach

Implements PLAN Phase 6 task **065** (closes Phase 6 / M9 packaging). Backend unchanged.

**Automated (this environment / CI):**

1. `scripts/test-release-smoke.sh` — `./gradlew :app:assembleRelease` (unsigned in CI), assert R8 `mapping.txt` exists, find the release APK, `aapt dump badging`:
   - package `com.anhtuta.lilimusic`
   - `versionName` / `versionCode` match `version.properties` (unless env override)
   - launchable activity is `MainActivity`
   - permissions: `INTERNET`, `POST_NOTIFICATIONS`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `FOREGROUND_SERVICE_DATA_SYNC`
   - **no** `RECORD_AUDIO`
2. `make smoke` wraps the script. `SMOKE_SKIP_ASSEMBLE=1` reuses an existing APK.
3. CI job **Minified release APK** runs the script on JDK 21 (parallel with `check`). `./gradlew check` stays debug unit tests + lint + ktlint + detekt.

**Manual (owner, arm64 macOS emulator):**

`docs/play/release-smoke.md` — create/start an arm64 AVD, `make doctor`, `make apk`, `make install`, then a checkbox list: playable-only list, play + notification, lyric formats, download/offline, lyric card share, `adb` custom-scheme deep link, settings. Use the **debug** APK (`make apk`); an unsigned release APK cannot be installed. Signed `make release-apk` is optional when `keystore.properties` exists.

Playable-only, FGS types, and no `RECORD_AUDIO` already have JVM/Robolectric tests from earlier tasks; this task wires them into a release-artifact gate and a human checklist.

### Non-goals

Signing (064), App Links hosting (063), changing hello_lumen, instrumented emulator CI, Firebase Test Lab.

## Files

- `docs/tasks/065-release-smoke.md` (this file)
- `scripts/test-release-smoke.sh`
- `Makefile` (`smoke`)
- `scripts/test-makefile.sh`
- `.github/workflows/ci.yml` (assemble-release job)
- `docs/play/release-smoke.md`
- `app/src/test/.../ReleaseSmokeTest.kt` (script + mapping/APK conventions; no emulator)

## Tests

- **Script:** `make -n smoke` mentions `assembleRelease` / `test-release-smoke`.
- **CI / local:** `scripts/test-release-smoke.sh` passes (minified APK + aapt).
- **JVM:** `ReleaseSmokeTest` asserts the smoke script exists and encodes the package / no-mic / MainActivity checks (so a missing script fails `check` even if CI’s assemble job is skipped).

No emulator in CI.

## Done when

- Task plan committed before code.
- `make smoke` assembles a minified release APK and validates badging.
- Manual arm64 macOS emulator checklist is in `docs/play/`.
- CI runs the automated smoke.
