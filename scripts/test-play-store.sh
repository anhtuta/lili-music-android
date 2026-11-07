#!/usr/bin/env bash
# Source checks for Play packaging (task 064). Does not assemble or need a keystore.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

fail() {
  echo "test-play-store: $*" >&2
  exit 1
}

need() {
  [ -f "$1" ] || fail "missing $1"
}

need version.properties
need keystore.properties.example
need app/proguard-rules.pro
need docs/play/signing.md
need docs/play/privacy-policy.md
need app/build.gradle.kts

grep -q '^VERSION_CODE=' version.properties || fail "version.properties missing VERSION_CODE"
grep -q '^VERSION_NAME=' version.properties || fail "version.properties missing VERSION_NAME"
grep -Eq '^VERSION_CODE=[0-9]+$' version.properties || fail "VERSION_CODE must be an integer"
grep -Eq '^VERSION_NAME=.+$' version.properties || fail "VERSION_NAME must be non-empty"

grep -q 'keystore.properties' .gitignore || fail ".gitignore must ignore keystore.properties"
grep -q '\*\.jks' .gitignore || fail ".gitignore must ignore *.jks"
if git check-ignore -q keystore.properties.example; then
  fail "keystore.properties.example must not be gitignored"
fi

for key in storeFile storePassword keyAlias keyPassword; do
  grep -q "^${key}=" keystore.properties.example || fail "example missing ${key}"
done

gradle="app/build.gradle.kts"
grep -q 'isMinifyEnabled = true' "$gradle" || fail "release must enable R8 minify"
grep -q 'isShrinkResources = true' "$gradle" || fail "release must shrink resources"
grep -q 'LILI_STORE_FILE' "$gradle" || fail "signing must read LILI_STORE_FILE"
grep -q 'keystore.properties' "$gradle" || fail "signing must read keystore.properties"
grep -q 'version.properties' "$gradle" || fail "version must read version.properties"
grep -q 'loadReleaseSigning' "$gradle" || fail "signing helper loadReleaseSigning missing"

rules="app/proguard-rules.pro"
for token in Media3 Hilt Retrofit Room kotlinx.serialization; do
  grep -q "$token" "$rules" || fail "proguard-rules.pro must mention $token"
done
grep -q 'retrofit2.http' "$rules" || fail "proguard must keep Retrofit HTTP methods"
grep -q 'androidx.room.Entity' "$rules" || fail "proguard must keep Room entities"
grep -q 'HiltViewModel' "$rules" || fail "proguard must keep HiltViewModel"
grep -q 'MediaSessionService' "$rules" || fail "proguard must keep MediaSessionService"
grep -q 'kotlinx.serialization.Serializable' "$rules" || fail "proguard must keep Serializable"

grep -q 'keytool' docs/play/signing.md || fail "signing.md must document keytool"
grep -q 'Play App Signing' docs/play/signing.md || fail "signing.md must document Play App Signing"
grep -q 'com.anhtuta.lilimusic' docs/play/privacy-policy.md || fail "privacy policy must name the package"
grep -q 'RECORD_AUDIO' docs/play/privacy-policy.md || fail "privacy policy must say no RECORD_AUDIO"

for manifest in app/src/main/AndroidManifest.xml core/playback/src/main/AndroidManifest.xml; do
  if grep -q 'RECORD_AUDIO' "$manifest"; then
    fail "$manifest must not request RECORD_AUDIO"
  fi
done
grep -q 'POST_NOTIFICATIONS' app/src/main/AndroidManifest.xml || fail "app manifest needs POST_NOTIFICATIONS"
grep -q 'FOREGROUND_SERVICE_MEDIA_PLAYBACK' core/playback/src/main/AndroidManifest.xml ||
  fail "playback manifest needs FOREGROUND_SERVICE_MEDIA_PLAYBACK"
grep -q 'FOREGROUND_SERVICE_DATA_SYNC' core/playback/src/main/AndroidManifest.xml ||
  fail "playback manifest needs FOREGROUND_SERVICE_DATA_SYNC"
grep -q 'applicationId = "com.anhtuta.lilimusic"' "$gradle" || fail "applicationId must stay com.anhtuta.lilimusic"

echo "test-play-store: ok"
