# Play Store signing (task 064)

Release builds minify with R8. They are **signed** only when a keystore is configured; otherwise `make bundle` / `make release-apk` still produce artifacts, named `*-unsigned.*`. Never commit `keystore.properties`, `*.jks`, or `*.keystore`.

## 1. Create an upload keystore

One-time, on the machine that will run `make bundle`:

```bash
keytool -genkeypair -v \
  -keystore upload-keystore.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 9125 \
  -storetype JKS
```

Keep the JKS and passwords offline. If you lose the upload key, you request a reset in Play Console (Play App Signing must already be on).

## 2. Point Gradle at it

Either copy [keystore.properties.example](../../keystore.properties.example) to **`keystore.properties`** at the repo root:

```
storeFile=/absolute/path/to/upload-keystore.jks
storePassword=...
keyAlias=upload
keyPassword=...
```

or export (values override the file):

```bash
export LILI_STORE_FILE=/absolute/path/to/upload-keystore.jks
export LILI_STORE_PASSWORD=...
export LILI_KEY_ALIAS=upload
export LILI_KEY_PASSWORD=...
```

Relative `storeFile` is resolved from the repo root, then from `:app`. All four fields are required; a partial set fails the Gradle configuration.

```bash
make bundle        # dist/lili-music-<versionName>.aab   (unsigned suffix if no keystore)
make release-apk   # dist/lili-music-<versionName>-release.apk
```

## 3. Enrol Play App Signing

Play Console → the app → **Test and release** → **App integrity** → **App signing**:

1. Create the Play Store listing with application id `com.anhtuta.lilimusic`.
2. Let Google generate the **app signing key** (recommended). You keep only the **upload key** from step 1.
3. First upload: the AAB from `make bundle` (signed with the upload key).
4. Copy the **App signing key certificate** SHA-256 (and the upload-key SHA-256) into [assetlinks.json](assetlinks.json) before hosting it (see [app-links.md](app-links.md)).

Debug installs stay `make apk` / `make install` (debug keystore, not the upload key).

## Version

[version.properties](../../version.properties) is the default `versionCode` / `versionName`. Override for a given build:

```bash
VERSION_CODE=2 VERSION_NAME=1.0.1 make bundle
```

Bump `VERSION_CODE` for every Play upload; `VERSION_NAME` is the user-visible label.
