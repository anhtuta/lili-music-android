# Phase 6 remainder — lyric card, App Links, Play packaging

[Learning guide index](README.md) · Tasks [062](../tasks/062-lyric-card.md), [063](../tasks/063-app-links.md), [064](../tasks/064-play-store.md), [065](../tasks/065-release-smoke.md)

**What was built.** Phase 6 after downloads:

1. **Lyric card.** From now-playing (and the lyrics toolbar) a listener picks lyric lines, sets colours / overlay / font like web `LyricCard`, previews a square card, copies the text, and shares a JPEG.
2. **Deep links.** `lilimusic://bai-hat/{path}` opens that song. HTTPS App Links are declared for path-style URLs on the catalog host and GitHub Pages; they verify only after you host [assetlinks.json](../play/assetlinks.json).
3. **Play packaging.** Release builds minify with R8. Signing is optional: env `LILI_*` or gitignored `keystore.properties`. `make bundle` / `make release-apk` copy an AAB/APK to `dist/`. Version is [version.properties](../../version.properties). Privacy policy text is [docs/play/privacy-policy.md](../play/privacy-policy.md) (you host the URL in Play Console).
4. **Release smoke.** `make smoke` assembles the minified APK and checks `aapt` badging. Manual arm64 macOS emulator steps are [docs/play/release-smoke.md](../play/release-smoke.md).

**Status:** This PR.

Web `BaiHat.js` still uses HashRouter `#/bai-hat/{path}`. Android cannot match URL fragments as verified App Links. Custom-scheme links work without any web change. HTTPS path-style links are Android-first until a later web rewrite (not in this repo).

## New terms

| Term | Meaning | Java / web analogy |
| --- | --- | --- |
| `FileProvider` | App-private file → a `content://` URI other apps may read once | A signed download URL, not `file://` |
| `ShareCompat` | Starts the system share sheet with that URI | `navigator.share` / a download button |
| Adaptive icon | Vector foreground + colour background (API 26+) | A favicon that the launcher masks |
| Intent-filter `VIEW` + `BROWSABLE` | OS may open our activity for a matching URI | React Router path, registered with the OS |
| Custom scheme | `lilimusic://…` (always ours; not verified) | `myapp://` deep link in a Cordova/PWA wrapper |
| App Links | HTTPS + `autoVerify` + Digital Asset Links | Verified domain → this app, like Universal Links |
| `assetlinks.json` | `https://<host>/.well-known/assetlinks.json` listing package + cert SHA-256 | `apple-app-site-association` |
| URL fragment | `#/bai-hat/{path}` — **not** sent in many HTTPS VIEW intents | HashRouter; the server never sees the hash |
| `launchMode=singleTop` | Reuse the running activity; `onNewIntent` | SPA stays mounted; a new route is dispatched |
| `NavController.handleDeepLink` | Match intent data to a `navDeepLink` | `router.navigate(url)` |
| R8 / ProGuard | Shrink, obfuscate, and optimize release bytecode | ProGuard on a fat JAR; tree-shaking + minify |
| `mapping.txt` | Class/method names before vs after R8 | Source map for a minified bundle |
| Upload key vs app signing key | You sign the AAB; Play re-signs what users install | CI signs an artifact; CDN may resign |
| AAB | Play upload format (split APKs per ABI/density) | A source zip; Play builds the installable bits |
| `versionCode` / `versionName` | Integer Play must increase; user-visible label | Build number vs semver tag |
| `aapt dump badging` | Reads package, version, permissions from an APK | `unzip -p` + parse a manifest |

Kotlin `object SongDeepLink` is a singleton parser (like a Java class with only static methods). `data class ReleaseSigning` in `app/build.gradle.kts` is Gradle-Kotlin, not app code.

Why not HashRouter App Links? Android’s intent matcher looks at scheme + host + **path**. It does not match `#…`. Chrome often drops the fragment when handing a URL to an app. So we (1) always accept `lilimusic://bai-hat/{path}`, (2) declare HTTPS `/bai-hat/{path}` for after you host `assetlinks.json`, (3) rewrite a VIEW URI only when the fragment is actually present.

Why unsigned release in CI? Play needs your upload keystore. CI must not have those passwords. R8 still runs; the APK is named `*-unsigned.apk` and cannot be installed until you sign it. Debug `make apk` is what the emulator checklist uses.

## Code to focus on

1. [LyricCardText.kt](../../core/lyrics/src/main/java/com/anhtuta/lilimusic/core/lyrics/LyricCardText.kt) — selectable lines from `ParsedLyric` (translations follow the setting; countdown slots omitted).
2. [LyricCardRenderer.kt](../../feature/lyrics/src/main/java/com/anhtuta/lilimusic/feature/lyrics/LyricCardRenderer.kt) — 1800px square JPEG (web 450px × `pixelRatio` 4).
3. [LyricCardFont.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/LyricCardFont.kt) — web font names; Android uses system sans/serif/italic.
4. [SongDeepLink.kt](../../core/model/src/main/java/com/anhtuta/lilimusic/core/model/SongDeepLink.kt) — hash / custom scheme / HTTPS → `lilimusic://bai-hat/{path}`.
5. [MainActivity.kt](../../app/src/main/java/com/anhtuta/lilimusic/MainActivity.kt) — rewrite incoming VIEW data; `onNewIntent` for a warm start.
6. [AndroidManifest.xml](../../app/src/main/AndroidManifest.xml) — `lilimusic` + two `autoVerify` HTTPS filters; still one `LAUNCHER`.
7. [app/build.gradle.kts](../../app/build.gradle.kts) — `loadReleaseSigning()`, R8 on, versions from `version.properties`.
8. [app/proguard-rules.pro](../../app/proguard-rules.pro) — keep Retrofit / Room / Hilt / Media3 / kotlinx.serialization.
9. [Makefile](../../Makefile) — `bundle`, `release-apk`, `smoke`.
10. [docs/play/](../play/) — assetlinks template, signing, privacy policy, emulator checklist.

Canonicalise, from `SongDeepLink.kt`:

```kotlin
fun canonicalViewUri(raw: String): String? = songPath(raw)?.let(::customUri)
```

A web URL `https://anhtuta.github.io/lili-player/#/bai-hat/hello-artist` becomes `lilimusic://bai-hat/hello-artist` when the fragment is in the Intent. Navigation already had `lilimusic://bai-hat/{path}`; HTTPS path-style patterns were added next to it.

Release minify, from `app/build.gradle.kts`:

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
    if (releaseSigning != null) {
        signingConfig = signingConfigs.getByName("release")
    }
}
```

No keystore → unsigned artifact + a note pointing at [signing.md](../play/signing.md). Partial env/file fields fail configuration instead of silently dropping the signature.

CLI extras (also in [how to run](05-how-to-run.md)):

| Target | What it does |
| --- | --- |
| `make bundle` | `:app:bundleRelease` → `dist/lili-music-<version>.aab` |
| `make release-apk` | Minified APK → `dist/` (`-unsigned` if no keystore) |
| `make smoke` | Assemble that APK, require `mapping.txt`, `aapt dump badging` |

Owner actions this repo cannot do: host `/.well-known/assetlinks.json` on both HTTPS hosts, host the privacy policy URL, create `upload-keystore.jks` with `keytool`, enrol Play App Signing, bump `VERSION_CODE` for each Play upload.
