#!/usr/bin/env bash
# Assemble a minified release APK (unsigned unless a keystore is configured)
# and assert package, version, launcher, and Play-policy permissions.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

fail() {
  echo "test-release-smoke: $*" >&2
  exit 1
}

sdk_dir=""
if [ -n "${ANDROID_HOME:-}" ]; then
  sdk_dir="$ANDROID_HOME"
elif [ -n "${ANDROID_SDK_ROOT:-}" ]; then
  sdk_dir="$ANDROID_SDK_ROOT"
elif [ -f local.properties ]; then
  sdk_dir="$(sed -n 's/^sdk.dir=//p' local.properties | tail -n1)"
fi
[ -n "$sdk_dir" ] && [ -d "$sdk_dir" ] || fail "Android SDK not found (ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties sdk.dir)"

version_code="$(sed -n 's/^VERSION_CODE=//p' version.properties | head -n1)"
version_name="$(sed -n 's/^VERSION_NAME=//p' version.properties | head -n1)"
[ -n "${VERSION_CODE:-}" ] && version_code="$VERSION_CODE"
[ -n "${VERSION_NAME:-}" ] && version_name="$VERSION_NAME"
[ -n "$version_code" ] || fail "VERSION_CODE missing"
[ -n "$version_name" ] || fail "VERSION_NAME missing"

if [ "${SMOKE_SKIP_ASSEMBLE:-}" != "1" ]; then
  ./gradlew :app:assembleRelease
fi

aapt=""
if [ -d "$sdk_dir/build-tools" ]; then
  aapt="$(find "$sdk_dir/build-tools" -name aapt -type f | sort | tail -n1)"
fi
[ -n "$aapt" ] && [ -x "$aapt" ] || fail "aapt not found under $sdk_dir/build-tools (install build-tools)"

apk=""
if [ -f app/build/outputs/apk/release/app-release.apk ]; then
  apk="app/build/outputs/apk/release/app-release.apk"
elif [ -f app/build/outputs/apk/release/app-release-unsigned.apk ]; then
  apk="app/build/outputs/apk/release/app-release-unsigned.apk"
fi
[ -n "$apk" ] || fail "no release APK under app/build/outputs/apk/release"

mapping="app/build/outputs/mapping/release/mapping.txt"
[ -f "$mapping" ] || fail "R8 mapping.txt missing; release minify did not run"

badging="$("$aapt" dump badging "$apk")"
printf '%s\n' "$badging" | grep -q "package: name='com.anhtuta.lilimusic'" ||
  fail "package name is not com.anhtuta.lilimusic"
printf '%s\n' "$badging" | grep -q "versionCode='$version_code'" ||
  fail "versionCode is not $version_code"
printf '%s\n' "$badging" | grep -q "versionName='$version_name'" ||
  fail "versionName is not $version_name"
printf '%s\n' "$badging" | grep -q "launchable-activity: name='com.anhtuta.lilimusic.MainActivity'" ||
  fail "launchable activity is not MainActivity"
printf '%s\n' "$badging" | grep -q "sdkVersion:'26'" || fail "minSdk is not 26"
printf '%s\n' "$badging" | grep -q "targetSdkVersion:'35'" || fail "targetSdk is not 35"

for perm in \
  android.permission.INTERNET \
  android.permission.POST_NOTIFICATIONS \
  android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK \
  android.permission.FOREGROUND_SERVICE_DATA_SYNC
do
  printf '%s\n' "$badging" | grep -Fq "uses-permission: name='$perm'" || fail "missing $perm"
done

if printf '%s\n' "$badging" | grep -q 'RECORD_AUDIO'; then
  fail "release APK must not request RECORD_AUDIO"
fi

echo "test-release-smoke: ok $apk"
