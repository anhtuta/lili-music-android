# App Links (task 063)

The Android app opens a song when the `path` slug is present. The web player (HashRouter) uses **`#/bai-hat/{path}`**. Android App Links cannot match URL fragments, so verified HTTPS links use a **path-style** URL. This repository does **not** change `anhtuta/lili-player` or `anhtuta/hello_lumen`.

## URLs the app accepts

| Kind | Example | Needs `assetlinks.json`? |
| --- | --- | --- |
| Custom scheme (works now) | `lilimusic://bai-hat/{path}` | No |
| HTTPS on the API host | `https://blog.nenmongvietnam.com/bai-hat/{path}` | Yes |
| HTTPS on GitHub Pages | `https://anhtuta.github.io/lili-player/bai-hat/{path}` | Yes |
| Web hash (if the fragment is in the Intent) | `https://anhtuta.github.io/lili-player/#/bai-hat/{path}` | No (rewritten in-app to the custom scheme) |

`{path}` is the catalog song `path` (same slug as web). Zing-only / missing `file_name` songs still do not play.

## What you must host

Copy [assetlinks.json](assetlinks.json) to **both** of these (HTTPS, `Content-Type: application/json`, no redirect):

1. `https://blog.nenmongvietnam.com/.well-known/assetlinks.json`
2. `https://anhtuta.github.io/.well-known/assetlinks.json`

Replace the two `REPLACE_WITH_…` fingerprints with SHA-256 values from Play Console → **App integrity** → **App signing key certificate** (required) and the **upload key** (if you use a separate upload keystore from task 064).

Fingerprint from a local upload keystore:

```bash
keytool -list -v -keystore upload-keystore.jks -alias upload
# SHA256: AB:CD:...  →  remove colons, keep uppercase hex
```

Until those files are live, Android will not auto-verify HTTPS App Links. The custom scheme and `adb` still work:

```bash
adb shell am start -a android.intent.action.VIEW -d "lilimusic://bai-hat/hello-artist"
```

## Optional web follow-up (not in this repo)

A rewrite so `https://anhtuta.github.io/lili-player/bai-hat/{path}` serves the existing HashRouter app (or 302 to `/lili-player/#/bai-hat/{path}`) would make the same URL work in a browser and as an App Link. That change belongs in `lili-player` / the static host, not here.
