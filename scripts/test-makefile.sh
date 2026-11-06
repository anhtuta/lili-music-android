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

echo "test-makefile: ok"
