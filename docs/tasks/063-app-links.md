# 063 — External App Links / deep links (M9)

## Goal

A song URL opens the Android player for that `path` (if the song is playable). Custom-scheme links work immediately. HTTPS App Links are declared so they verify after the owner hosts `assetlinks.json` (this repo does not change the web or Lumen backends).

## Approach

Implements PLAN Phase 6 task **063** (milestone M9). In-app `bai-hat/{path}` navigation already exists from **006**. Backend unchanged.

Web (class `BaiHat.js` + `App.js` `HashRouter`):

- Library and play update `window.location.href = '#/bai-hat/' + song.path`
- Table links are `href={'#/bai-hat/' + song.path}`
- Full web URL is `https://<host>/#/bai-hat/{path}` (GitHub Pages player is typically `https://anhtuta.github.io/lili-player/#/bai-hat/{path}`)

Android App Links **cannot** match URL fragments. Hash links therefore cannot be verified App Links without a web change (out of scope). This task:

1. **Custom scheme (always works):** `lilimusic://bai-hat/{path}` VIEW/BROWSABLE intent-filter + existing `navDeepLink`.
2. **HTTPS path-style (verifies after Digital Asset Links):**
   - `https://blog.nenmongvietnam.com/bai-hat/{path}`
   - `https://anhtuta.github.io/lili-player/bai-hat/{path}`
   `autoVerify="true"`, `pathPrefix` `/bai-hat` or `/lili-player/bai-hat`.
3. **Hash fallback:** if an incoming VIEW URI has fragment `/bai-hat/{path}` (some share sheets keep it), `SongDeepLink` rewrites it to the custom scheme before Navigation handles it.
4. **Playable gate:** unchanged `PlayerViewModel.ensurePlaying` — unknown / Zing-only / offline-unavailable paths do not start playback.
5. **Owner action:** host `docs/play/assetlinks.json` at `https://<host>/.well-known/assetlinks.json` with the **Play App Signing** SHA-256 (and the upload-key SHA-256 if using a separate upload keystore). Do **not** change `anhtuta/lili-player` or `anhtuta/hello_lumen` from this repo. Optional later: a web rewrite so `/bai-hat/{path}` serves the HashRouter app; until then HTTPS path-style links are Android-first.

`MainActivity` stays the only `LAUNCHER`. `onNewIntent` + `NavController.handleDeepLink` cover a warm start.

### Modules

- **`:core:model`** — `SongDeepLink` parser (JVM).
- **`:app`** — intent-filters, `onNewIntent`, extra `navDeepLink`s, assetlinks template + hosting doc.

### Non-goals

Changing the web app or Lumen API, Play signing (064), lyric card (062), verifying App Links against a live `assetlinks.json` in CI (owner must host it).

## Files

- `docs/tasks/063-app-links.md` (this file)
- `docs/play/assetlinks.json` (placeholder fingerprint)
- `docs/play/app-links.md` (exact host paths and `keytool` fingerprint command)
- `core/model/.../SongDeepLink.kt` + tests
- `app` manifest, `MainActivity`, `LiliNavHost`, tests

## Tests

- **JVM:** custom scheme, both HTTPS hosts, hash fragment, URL-decoded path, empty/`bai-hat` with no id → null.
- **Nav (Robolectric):** `handleDeepLink` of `lilimusic://bai-hat/hello-artist` and `https://blog.nenmongvietnam.com/bai-hat/hello-artist` shows the player for that path.
- **Manifest:** VIEW/BROWSABLE filters for `lilimusic` and both HTTPS hosts; `autoVerify` on HTTPS; still exactly one LAUNCHER (`MainActivity`).
- **Activity (Robolectric):** hash URI is rewritten to `lilimusic://bai-hat/{path}`.

No emulator. No live Digital Asset Links fetch.

## Done when

- Task plan committed before code.
- Custom-scheme and HTTPS intent-filters exist; hash URIs rewrite when the fragment is present.
- `assetlinks.json` template + hosting instructions are in `docs/play/` for the owner.
- Playable-only gate still applies.
- Tests above exist.
