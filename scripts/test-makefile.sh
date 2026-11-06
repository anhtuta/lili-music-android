#!/usr/bin/env bash
# Dry-run checks for the root Makefile. Does not assemble or need an SDK.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

fail() {
  echo "test-makefile: $*" >&2
  exit 1
}

help_out="$(make help)"
echo "$help_out" | grep -q 'Usage: make <target>' || fail "help missing usage line"

targets="help doctor build apk release-apk install test lint check clean"
for target in $targets; do
  echo "$help_out" | grep -Eq "^  ${target} " || fail "help missing target ${target}"
done

phony="$(grep -E '^\.PHONY:' Makefile)"
for target in $targets; do
  echo "$phony" | grep -Eq "(^|[[:space:]])${target}([[:space:]]|$)" || fail ".PHONY missing ${target}"
done

make -n doctor | grep -Eq 'java|ANDROID_HOME' || fail "make -n doctor should mention java or ANDROID_HOME"
make -n doctor | grep -q 'platforms;android-37.0' || fail "make -n doctor should suggest platforms;android-37.0"
make -n doctor | grep -q 'build-tools;37.0.0' || fail "make -n doctor should suggest build-tools;37.0.0"
make -n doctor | grep -q 'android sdk CLI' || fail "make -n doctor should mention the android sdk CLI deprecation warning"
make -n build | grep -q 'assembleDebug' || fail "make -n build should run assembleDebug"
make -n apk | grep -q 'assembleDebug' || fail "make -n apk should run assembleDebug"
make -n apk | grep -q 'dist/lili-music-' || fail "make -n apk should copy into dist/"
make -n release-apk | grep -q 'assembleRelease' || fail "make -n release-apk should run assembleRelease"
make -n release-apk | grep -q 'task 064' || fail "make -n release-apk should mention unsigned signing task 064"
make -n install | grep -q 'adb install -r' || fail "make -n install should adb install -r"
make -n test | grep -q 'testDebugUnitTest' || fail "make -n test should run testDebugUnitTest"
make -n lint | grep -q 'lintDebug' || fail "make -n lint should run lintDebug"
make -n check | grep -q './gradlew check' || fail "make -n check should run ./gradlew check"
make -n clean | grep -q './gradlew clean' || fail "make -n clean should run ./gradlew clean"
make -n clean | grep -q 'dist' || fail "make -n clean should remove dist/"

finder="scripts/find-android-platform.sh"
[ -f "$finder" ] || fail "missing $finder"
fake="$(mktemp -d)"
trap 'rm -rf "$fake"' EXIT
mkdir -p "$fake/only-dot-zero/platforms/android-37.0"
dot_zero="$("$finder" "$fake/only-dot-zero" 37)"
[ "$dot_zero" = "$fake/only-dot-zero/platforms/android-37.0" ] || fail "should accept platforms/android-37.0, got: ${dot_zero:-empty}"
mkdir -p "$fake/plain/platforms/android-37"
plain="$("$finder" "$fake/plain" 37)"
[ "$plain" = "$fake/plain/platforms/android-37" ] || fail "should accept platforms/android-37, got: ${plain:-empty}"
mkdir -p "$fake/minor/platforms/android-37.2"
minor="$("$finder" "$fake/minor" 37)"
[ "$minor" = "$fake/minor/platforms/android-37.2" ] || fail "should accept platforms/android-37.*, got: ${minor:-empty}"
if "$finder" "$fake" 37 >/dev/null 2>&1; then
  fail "empty platforms dir should not match"
fi

echo "test-makefile: ok"
