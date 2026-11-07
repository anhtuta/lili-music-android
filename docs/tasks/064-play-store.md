# 064 — Play Store packaging and signing

## Goal

The owner can produce a Play-uploadable **signed** release AAB/APK from the CLI (`make bundle` / `make release-apk`) when a gitignored keystore is present, or an **unsigned** minified release otherwise. R8, versioning, privacy policy text, and Play App Signing steps are documented. No secrets are committed.

## Approach

Implements PLAN Phase 6 task **064**. Backend unchanged. Listener only. Package `com.anhtuta.lilimusic` (already the `applicationId`).

Already in the tree from earlier tasks (keep, assert in tests):

- `applicationId` / namespace `com.anhtuta.lilimusic`
- Adaptive launcher icons (note on dark `#1A1A2E` + gold `#F5C518`)
- `POST_NOTIFICATIONS`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `FOREGROUND_SERVICE_DATA_SYNC`
- No `RECORD_AUDIO`

This task adds:

1. **Signing** from env (`LILI_STORE_FILE`, `LILI_STORE_PASSWORD`, `LILI_KEY_ALIAS`, `LILI_KEY_PASSWORD`) **or** gitignored `keystore.properties` (see `keystore.properties.example`). If all four values and the JKS exist, `signingConfigs.release` is applied to the release build type. If none are set, release is unsigned. If a subset is set, Gradle fails with a clear error (do not silently unsigned).
2. **R8** on release: `isMinifyEnabled = true`, `isShrinkResources = true`, plus keep rules for Media3 / Hilt / Retrofit / Room / kotlinx.serialization / WorkManager so a minified build still plays, injects, fetches, and caches.
3. **Versioning:** committed `version.properties` (`VERSION_CODE`, `VERSION_NAME`). Env `VERSION_CODE` / `VERSION_NAME` override. Makefile copies use the same values. Default **1.0.0** / **1** for the first Play upload.
4. **Makefile:** `bundle` copies the AAB to `dist/`; `release-apk` copies the APK. Signed names omit `-unsigned`; unsigned names include it and point at `docs/play/signing.md`.
5. **Docs:** `docs/play/signing.md` (`keytool` upload keystore + Play App Signing enrolment) and `docs/play/privacy-policy.md` (owner hosts this URL in Play Console; this repo does not change the web backends).
6. **README / doctor:** JDK 21+ (not 17) so CLI docs match `make doctor`.

### Non-goals

Lyric card (062), App Links (063), release smoke checklist (065), changing hello_lumen / the web player, committing any JKS or passwords, Play Console listing screenshots.

## Files

- `docs/tasks/064-play-store.md` (this file)
- `version.properties`, `keystore.properties.example`, `.gitignore`
- `app/build.gradle.kts` (signing, minify, version)
- `app/proguard-rules.pro`
- `Makefile`, `scripts/test-makefile.sh`, `scripts/test-play-store.sh`
- `docs/play/signing.md`, `docs/play/privacy-policy.md`
- `README.md` (JDK 21+, bundle target)
- Tests under `app/src/test` (packaging assertions)

## Tests

- **JVM:** `BuildConfig.VERSION_NAME` / `VERSION_CODE` match `version.properties` defaults; `APPLICATION_ID` unchanged; merged debug manifest still has FGS types and no `RECORD_AUDIO`.
- **Source (script, no SDK):** `scripts/test-play-store.sh` — minify + shrink on; signing reads env/`keystore.properties`; example + gitignore; ProGuard keep lines for Media3, Hilt, Retrofit, Room, kotlinx.serialization; privacy + signing docs exist.
- **Makefile:** `make help` lists `bundle`; `make -n bundle` runs `bundleRelease`; unsigned `release-apk` message points at `docs/play/signing.md` (not “task 064”).

No emulator. No real keystore in CI (unsigned minify).

## Done when

- Task plan committed before code.
- Release builds minify; signed when config is present, unsigned otherwise.
- Secrets stay gitignored. `keytool` + Play App Signing documented.
- Privacy policy text exists for the owner to host.
- Tests above exist.
